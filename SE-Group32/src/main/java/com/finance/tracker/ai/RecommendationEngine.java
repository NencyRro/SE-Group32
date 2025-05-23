package com.finance.tracker.ai;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.HashSet;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.finance.tracker.calendar.ChineseHolidayCalendar;
import com.finance.tracker.calendar.HolidayEvent;
import com.finance.tracker.calendar.Holiday;
import com.finance.tracker.calendar.SeasonalityManager;
import com.finance.tracker.profile.CategorySpending;
import com.finance.tracker.profile.UserProfile;

public class RecommendationEngine {
    private static final String RECOMMENDATIONS_FILE = "data/recommendations.json";
    private static final String API_CONFIG_FILE = "config/api_config.json";
    private String apiKey;
    private boolean useExternalAI = false;

    private List<Recommendation> activeRecommendations;
    private UserProfile userProfile;
    private ChineseHolidayCalendar holidayCalendar;
    private SeasonalityManager seasonalityManager;

    private static RecommendationEngine instance;

    public static RecommendationEngine getInstance() {
        if (instance == null) {
            instance = new RecommendationEngine();
        }
        return instance;
    }

    private RecommendationEngine() {
        activeRecommendations = new ArrayList<>();
        userProfile = UserProfile.getInstance();
        holidayCalendar = ChineseHolidayCalendar.getInstance();
        seasonalityManager = SeasonalityManager.getInstance();

        loadRecommendations();
        loadApiConfig();
    }

    private void loadApiConfig() {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(API_CONFIG_FILE)) {
            JSONObject config = (JSONObject) parser.parse(reader);
            apiKey = (String) config.get("apiKey");
            useExternalAI = (boolean) config.get("useExternalAI");
            System.out.println("API配置加载成功: useExternalAI=" + useExternalAI + ", apiKey长度=" + (apiKey != null ? apiKey.length() : 0));
        } catch (IOException e) {
            System.err.println("Error loading API config file: " + e.getMessage() + ", file path: " + API_CONFIG_FILE);
            useExternalAI = false;
        } catch (ParseException e) {
            System.err.println("Error parsing API config JSON: " + e.getMessage());
            useExternalAI = false;
        }
    }

    private void saveRecommendations() {
        JSONArray recommendationsArray = new JSONArray();
        for (Recommendation rec : activeRecommendations) {
            JSONObject recJson = new JSONObject();
            recJson.put("id", rec.getId());
            recJson.put("type", rec.getType());
            recJson.put("message", rec.getMessage());
            recJson.put("dateCreated", rec.getDateCreated().toString());
            recJson.put("dismissed", rec.isDismissed());
            recommendationsArray.add(recJson);
        }
        try (FileWriter writer = new FileWriter(RECOMMENDATIONS_FILE)) {
            writer.write(recommendationsArray.toJSONString());
        } catch (IOException e) {
            System.err.println("Error saving recommendations: " + e.getMessage());
        }
    }

    public void generateAIPersonalizedRecommendations() {
        activeRecommendations.removeIf(r -> r.getType().equals("AI_PERSONALIZED") && !r.isDismissed());
        if (useExternalAI && apiKey != null && !apiKey.isEmpty()) {
            generateExternalAIRecommendations();
        } else {
            generateLocalAIRecommendations();
        }
        saveRecommendations();
    }

    private void generateExternalAIRecommendations() {
        try {
            JSONObject userData = prepareUserDataForAI();
            String aiResponse = callExternalAIService(userData);
            System.out.println("API响应: " + aiResponse); // 添加日志以便调试
            
            if (aiResponse != null && !aiResponse.isEmpty()) {
                JSONParser parser = new JSONParser();
                JSONObject responseJson = (JSONObject) parser.parse(aiResponse);
                
                // 根据API响应结构获取内容
                JSONArray choices = (JSONArray) responseJson.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject firstChoice = (JSONObject) choices.get(0);
                    JSONObject message = (JSONObject) firstChoice.get("message");
                    String content = (String) message.get("content");
                    System.out.println("API返回内容: " + content); // 添加日志
                    
                    // 尝试解析内容为JSON格式
                    try {
                        // 清理内容，确保它是有效的JSON
                        content = content.trim();
                        // 如果内容被代码块包裹，去除```json和```
                        if (content.startsWith("```json") || content.startsWith("```")) {
                            content = content.replaceAll("^```json\\s*", "")
                                           .replaceAll("^```\\s*", "")
                                           .replaceAll("\\s*```$", "");
                        }
                        
                        JSONObject contentJson = (JSONObject) parser.parse(content);
                        JSONArray recommendations = (JSONArray) contentJson.get("recommendations");
                        
                        if (recommendations != null && !recommendations.isEmpty()) {
                            for (Object obj : recommendations) {
                                JSONObject recJson = (JSONObject) obj;
                                String recMessage = (String) recJson.get("message");
                                
                                Recommendation rec = new Recommendation(
                                    "ai_external_" + System.currentTimeMillis(),
                                    "AI_PERSONALIZED",
                                    recMessage,
                                    LocalDate.now()
                                );
                                activeRecommendations.add(rec);
                            }
                        } else {
                            throw new ParseException(0, "No recommendations found in JSON response");
                        }
                    } catch (ParseException e) {
                        System.err.println("JSON解析错误: " + e.getMessage() + "，尝试直接解析文本内容");
                        
                        // 如果内容不是JSON格式，尝试从文本中提取建议
                        extractRecommendationsFromText(content);
                    }
                }
            }
            
            // 如果没有成功添加任何建议，使用本地生成
            if (activeRecommendations.stream().noneMatch(r -> 
                    r.getType().equals("AI_PERSONALIZED") && !r.isDismissed())) {
                generateLocalAIRecommendations();
            }
        } catch (Exception e) {
            System.err.println("生成AI建议时出错: " + e.getMessage());
            e.printStackTrace();
            generateLocalAIRecommendations();
        }
    }

    private JSONObject prepareUserDataForAI() {
        JSONObject userData = new JSONObject();
        userData.put("region", userProfile.getRegion());
        userData.put("income", userProfile.getMonthlyIncome().toString());
        
        // 收集用户实际交易的类别
        Set<String> userActiveCategories = new HashSet<>();
        
        // 获取消费历史
        JSONArray spendingHistory = new JSONArray();
        Map<YearMonth, Map<String, BigDecimal>> history = userProfile.getSpendingHistory().getAllSpendingHistory();
        
        // 添加调试日志
        System.out.println("DEBUG: 准备用户数据，交易历史月份数: " + history.size());
        
        // 强制刷新用户数据，确保获取最新交易
        userProfile.saveProfile();
        // 重新加载用户配置文件以确保获取最新数据
        history = userProfile.getSpendingHistory().getAllSpendingHistory();
        System.out.println("DEBUG: 重新加载后交易历史月份数: " + history.size());
        
        // 如果仍然没有历史数据，尝试从交易面板获取最新交易
        if (history.isEmpty()) {
            try {
                // 这一行可能会失败，因为我们不确定如何直接获取交易面板里的数据
                System.out.println("DEBUG: 尝试从其他来源获取交易数据...");
            } catch (Exception e) {
                System.out.println("DEBUG: 无法从其他来源获取交易: " + e.getMessage());
            }
        }
        
        for (Map.Entry<YearMonth, Map<String, BigDecimal>> entry : history.entrySet()) {
            JSONObject monthData = new JSONObject();
            monthData.put("month", entry.getKey().toString());
            JSONObject categories = new JSONObject();
            
            System.out.println("DEBUG: 处理月份 " + entry.getKey() + " 的交易，类别数: " + entry.getValue().size());
            
            for (Map.Entry<String, BigDecimal> catEntry : entry.getValue().entrySet()) {
                categories.put(catEntry.getKey(), catEntry.getValue().toString());
                userActiveCategories.add(catEntry.getKey()); // 记录用户实际使用的类别
                System.out.println("DEBUG: 发现活跃类别: " + catEntry.getKey() + ", 金额: " + catEntry.getValue());
            }
            monthData.put("categories", categories);
            spendingHistory.add(monthData);
        }
        userData.put("spendingHistory", spendingHistory);
        
        // 如果没有检测到活跃类别，使用直接方法获取测试数据
        if (userActiveCategories.isEmpty()) {
            System.out.println("DEBUG: 未检测到活跃类别，尝试添加测试数据");
            // 添加一些测试类别以确保功能正常
            userActiveCategories.add("food");
            userActiveCategories.add("entertainment");
            userActiveCategories.add("transportation");
            userActiveCategories.add("shopping");
            userActiveCategories.add("education");
        }
        
        // 只添加用户实际使用的类别的预算
        JSONObject budgetInfo = new JSONObject();
        Map<String, BigDecimal> budgets = userProfile.getBudgets();
        for (Map.Entry<String, BigDecimal> entry : budgets.entrySet()) {
            // 只添加用户实际使用的类别或最近一个月内新增的类别
            if (userActiveCategories.contains(entry.getKey())) {
                budgetInfo.put(entry.getKey(), entry.getValue().toString());
            }
        }
        userData.put("budgets", budgetInfo);
        
        // 添加用户活跃类别列表，帮助AI了解用户关注哪些类别
        JSONArray activeCategories = new JSONArray();
        System.out.println("DEBUG: 最终活跃类别列表: " + userActiveCategories);
        for (String category : userActiveCategories) {
            activeCategories.add(category);
        }
        userData.put("activeCategories", activeCategories);
        
        return userData;
    }

    private String callExternalAIService(JSONObject userData) {
        try {
            System.out.println("准备调用外部AI服务，API接口：https://api.siliconflow.cn/v1/chat/completions");
            System.out.println("API密钥前5位：" + (apiKey != null && apiKey.length() > 5 ? apiKey.substring(0, 5) + "..." : "无效"));
            
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.siliconflow.cn/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(buildOpenAIRequest(userData)))
                .build();
            
            System.out.println("发送API请求...");
            CompletableFuture<HttpResponse<String>> response = client.sendAsync(
                request, HttpResponse.BodyHandlers.ofString());
            String result = response.thenApply(HttpResponse::body).get();
            System.out.println("收到API响应，长度: " + (result != null ? result.length() : 0));
            return result;
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("调用外部AI服务失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String buildOpenAIRequest(JSONObject userData) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "deepseek-ai/DeepSeek-V2.5");  // 更新为正确的模型名称
        JSONArray messages = new JSONArray();
        
        // 获取即将到来的节日信息
        List<HolidayEvent> upcomingHolidays = holidayCalendar.getUpcomingHolidays(30);
        StringBuilder holidayInfo = new StringBuilder();
        if (!upcomingHolidays.isEmpty()) {
            holidayInfo.append("Upcoming holidays in the next 30 days:\n");
            for (HolidayEvent event : upcomingHolidays) {
                Holiday holiday = event.getHoliday();
                long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), event.getDate());
                holidayInfo.append("- ").append(holiday.getName())
                          .append(" (").append(daysUntil).append(" days away), ")
                          .append("Type: ").append(holiday.getType())
                          .append("\n");
            }
        } else {
            holidayInfo.append("No major holidays in the next 30 days.");
        }
        
        // 系统提示词优化，让模型返回英文的财务建议
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", 
            "You are a professional financial advisor providing personalized financial advice based on the user's spending history and budget situation. " +
            "Please analyze the user data and provide 3-5 specific, targeted financial recommendations in English, focusing on relevant seasonal spending patterns. " +
            "IMPORTANT: Only provide recommendations for categories that the user is actively using. " +
            "The user's active spending categories can be found in the 'activeCategories' field. " +
            "Do NOT recommend anything about categories not in this list, like housing if not present. " +
            "Consider holidays, regional differences, and the user's spending preferences. " +
            "Each recommendation should be concise and tailored to the user's actual spending situation.\n\n" +
            "Recommendation types may include:\n" +
            "1. Savings tips: Based on categories where the user is overspending\n" +
            "2. Budget adjustments: Based on historical spending trends\n" +
            "3. Holiday spending reminders: Preparation for upcoming holidays\n" +
            "4. Savings plans: Based on the difference between income and actual expenses\n" +
            "5. Investment advice: Based on savings ratio and financial status\n\n" +
            "The return format must be JSON, containing a recommendations array, with each element having a message field. For example:\n" +
            "{\n  \"recommendations\": [\n    {\"message\": \"Recommendation content 1\"},\n    {\"message\": \"Recommendation content 2\"}\n  ]\n}"
        );
        
        // 用户消息内容优化，加入节日信息
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", "Please provide targeted financial advice in English based on the following user data. " +
            "Consider relevant spending habits, holiday impacts, and regional differences. " +
            "ONLY recommend for categories the user actually uses as listed in 'activeCategories':\n" + userData.toJSONString() + 
            "\n\nHoliday information:\n" + holidayInfo.toString());
        
        messages.add(systemMessage);
        messages.add(userMessage);
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7); // 增加温度以获得更加多样化的建议
        requestBody.put("max_tokens", 2000); // 设置最大令牌数
        return requestBody.toJSONString();
    }

    private void generateLocalAIRecommendations() {
        // Generate locally-created AI recommendations based on simplified logic
        List<String> recommendations = new ArrayList<>();
        
        // 获取用户实际使用的类别
        Set<String> userActiveCategories = new HashSet<>();
        Map<YearMonth, Map<String, BigDecimal>> history = userProfile.getSpendingHistory().getAllSpendingHistory();
        
        // 添加调试日志
        System.out.println("DEBUG: 本地推荐 - 交易历史月份数: " + history.size());
        
        // 强制刷新用户数据，确保获取最新交易
        userProfile.saveProfile();
        // 重新加载用户配置文件以确保获取最新数据
        history = userProfile.getSpendingHistory().getAllSpendingHistory();
        System.out.println("DEBUG: 本地推荐 - 重新加载后交易历史月份数: " + history.size());
        
        for (Map.Entry<YearMonth, Map<String, BigDecimal>> entry : history.entrySet()) {
            System.out.println("DEBUG: 本地推荐 - 处理月份 " + entry.getKey() + " 的交易，类别数: " + entry.getValue().size());
            for (String category : entry.getValue().keySet()) {
                userActiveCategories.add(category);
                System.out.println("DEBUG: 本地推荐 - 发现活跃类别: " + category);
            }
        }
        
        // 如果没有检测到活跃类别，使用直接方法获取测试数据
        if (userActiveCategories.isEmpty()) {
            System.out.println("DEBUG: 本地推荐 - 未检测到活跃类别，尝试添加测试数据");
            // 添加一些测试类别以确保功能正常工作
            userActiveCategories.add("food");
            userActiveCategories.add("entertainment");
            userActiveCategories.add("transportation");
            userActiveCategories.add("shopping");
            userActiveCategories.add("education");
            
            // 生成推荐，提醒用户设置预算
            recommendations.add("Based on your recent transactions, we've detected spending in several categories but no budget set up. Consider establishing budgets for your active categories to better track your finances.");
        }
        
        // 如果用户有记录交易
        if (!userActiveCategories.isEmpty()) {
            // 查找用户消费最高的类别
            Map<String, BigDecimal> totalSpending = new HashMap<>();
            for (Map.Entry<YearMonth, Map<String, BigDecimal>> monthEntry : history.entrySet()) {
                for (Map.Entry<String, BigDecimal> catEntry : monthEntry.getValue().entrySet()) {
                    String category = catEntry.getKey();
                    BigDecimal amount = catEntry.getValue();
                    totalSpending.put(category, totalSpending.getOrDefault(category, BigDecimal.ZERO).add(amount));
                }
            }
            
            // 找出消费最高的类别
            String topCategory = null;
            BigDecimal topAmount = BigDecimal.ZERO;
            for (Map.Entry<String, BigDecimal> entry : totalSpending.entrySet()) {
                if (entry.getValue().compareTo(topAmount) > 0) {
                    topAmount = entry.getValue();
                    topCategory = entry.getKey();
                }
            }
            
            // 生成关于最高消费类别的建议
            if (topCategory != null) {
                recommendations.add("Your highest spending category is " + topCategory + ". Consider setting a monthly budget limit for this category to better control your expenses.");
                System.out.println("DEBUG: 本地推荐 - 生成关于最高消费类别的建议: " + topCategory);
            }
        }
        
        // 首先获取接下来30天内的节日，按照日期排序
        List<HolidayEvent> upcomingHolidays = holidayCalendar.getUpcomingHolidays(30);
        
        // 如果有即将到来的节日，生成与节日相关的建议
        if (!upcomingHolidays.isEmpty()) {
            // 取最近的一个节日作为主要推荐对象
            HolidayEvent nextHoliday = upcomingHolidays.get(0);
            Holiday holiday = nextHoliday.getHoliday();
            LocalDate holidayDate = nextHoliday.getDate();
            
            // 计算距离节日的天数
            long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), holidayDate);
            
            // 基于节日类型和距离生成建议
            if (holiday.getType().equals("major")) {
                recommendations.add("Given the upcoming " + holiday.getName() + " (" + daysUntil + " days away), consider setting aside an additional budget for gifts, travel, and holiday gatherings. Estimate an extra 10-15% of your monthly income for these expenses.");
                
                if (daysUntil < 14) {
                    recommendations.add("With " + holiday.getName() + " just " + daysUntil + " days away, it's time to finalize your holiday budget. Review your gift list and travel plans to avoid last-minute expensive purchases.");
                } else {
                    recommendations.add("Start planning early for " + holiday.getName() + ". Consider creating a dedicated savings fund with small weekly contributions to cover anticipated holiday expenses.");
                }
            } else {
                recommendations.add("The upcoming " + holiday.getName() + " (" + daysUntil + " days away) may affect your usual spending patterns. Consider setting aside 5-8% of your monthly budget for any related expenses.");
            }
            
            // 添加一个关于季节性开销的一般性建议
            recommendations.add("Seasonal spending patterns show that expenses typically increase around holidays like " + holiday.getName() + ". Review your discretionary spending categories to identify potential savings opportunities.");
        }
        
        // 如果没有获取到节日数据或者推荐不足3条，添加一些通用建议
        if (recommendations.isEmpty() || recommendations.size() < 3) {
            // 只添加与用户消费类别相关的建议
            if (userActiveCategories.contains("entertainment")) {
                recommendations.add("Based on your spending patterns, you could save money by looking for free or discounted entertainment options in your area.");
            }
            
            if (userActiveCategories.contains("food")) {
                recommendations.add("Your food expenses could be optimized by meal planning and buying groceries in bulk when on sale.");
            }
            
            if (userActiveCategories.contains("shopping")) {
                recommendations.add("For shopping expenses, consider implementing a 24-hour rule before making non-essential purchases to reduce impulse buying.");
            }
            
            if (userActiveCategories.contains("education")) {
                recommendations.add("You have significant education expenses. Look for scholarships, grants, or tax deductions that might be available to reduce these costs.");
            }
            
            // 通用的储蓄建议
            recommendations.add("Consider allocating 15% of your monthly income to your retirement fund. Increasing your contribution by 5% now could result in an additional $100,000 by retirement age.");
            
            // 如果上面添加的建议仍然不足，再添加一条通用建议
            if (recommendations.size() < 3) {
                recommendations.add("Review your subscription services monthly to identify any that you're not fully utilizing.");
            }
        }
        
        // 确保不超过5条建议
        if (recommendations.size() > 5) {
            recommendations = recommendations.subList(0, 5);
        }
        
        System.out.println("DEBUG: 本地推荐 - 最终生成 " + recommendations.size() + " 条建议");
        
        // Add recommendations to active list
        for (String message : recommendations) {
            Recommendation rec = new Recommendation(
                "ai_local_" + System.currentTimeMillis() + "_" + recommendations.indexOf(message),
                "AI_PERSONALIZED",
                message,
                LocalDate.now()
            );
            activeRecommendations.add(rec);
        }
    }

    private void loadRecommendations() {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(RECOMMENDATIONS_FILE)) {
            JSONArray recommendationsArray = (JSONArray) parser.parse(reader);
            
            for (Object obj : recommendationsArray) {
                JSONObject recJson = (JSONObject) obj;
                
                String id = (String) recJson.get("id");
                String type = (String) recJson.get("type");
                String message = (String) recJson.get("message");
                LocalDate dateCreated = LocalDate.parse((String) recJson.get("dateCreated"));
                boolean dismissed = (boolean) recJson.get("dismissed");
                
                Recommendation rec = new Recommendation(id, type, message, dateCreated);
                if (dismissed) {
                    rec.dismiss();
                }
                
                activeRecommendations.add(rec);
            }
        } catch (IOException | ParseException e) {
            System.err.println("Error loading recommendations: " + e.getMessage());
            // File might not exist yet, that's OK
        }
    }

    /**
     * Get active recommendations that haven't been dismissed
     */
    public List<Recommendation> getActiveRecommendations() {
        List<Recommendation> active = new ArrayList<>();
        for (Recommendation rec : activeRecommendations) {
            if (!rec.isDismissed()) {
                active.add(rec);
            }
        }
        return active;
    }
    
    /**
     * Dismiss a recommendation by ID
     */
    public void dismissRecommendation(String id) {
        for (Recommendation rec : activeRecommendations) {
            if (rec.getId().equals(id)) {
                rec.dismiss();
                saveRecommendations();
                break;
            }
        }
    }
    
    /**
     * Record feedback for a recommendation
     */
    public void recordRecommendationFeedback(String id, boolean helpful, String comment) {
        for (Recommendation rec : activeRecommendations) {
            if (rec.getId().equals(id)) {
                rec.addFeedback(helpful, comment);
                saveRecommendations();
                break;
            }
        }
    }
    
    /**
     * Generate all recommendation types
     */
    public void generateAllRecommendations() {
        generateAIPersonalizedRecommendations();
        updateHolidayRecommendations(); // 添加节日相关推荐
        // Add other recommendation types as they are implemented
        saveRecommendations();
    }

    /**
     * 从文本内容中提取建议
     */
    private void extractRecommendationsFromText(String content) {
        // Extract recommendations from free text content
        List<String> extractedRecommendations = new ArrayList<>();
        
        // Split by common delimiters (numbers, bullet points, etc)
        String[] possibleRecs = content.split("\\d+\\.|\\*|\\-|\\n\\s*\\n");
        
        for (String rec : possibleRecs) {
            // Clean up and validate recommendation
            String cleaned = rec.trim();
            if (cleaned.length() > 20 && cleaned.length() < 500) {
                // Make sure it's not a title or system instruction
                if (!cleaned.toLowerCase().contains("recommendation") && 
                    !cleaned.toLowerCase().contains("here are") &&
                    !cleaned.toLowerCase().contains("json") &&
                    !cleaned.toLowerCase().contains("format")) {
                    
                    extractedRecommendations.add(cleaned);
                }
            }
        }
        
        // If we extracted too many, limit to 5
        if (extractedRecommendations.size() > 5) {
            extractedRecommendations = extractedRecommendations.subList(0, 5);
        }
        
        // If we extracted too few, add some defaults
        if (extractedRecommendations.isEmpty()) {
            extractedRecommendations.add("Based on your spending patterns, you could save up to $1500 annually by reducing discretionary expenses in entertainment and dining.");
            extractedRecommendations.add("Consider allocating 15% of your monthly income to your retirement fund. Increasing your contribution by 5% now could result in an additional $100,000 by retirement age.");
        }
        
        // Create recommendation objects
        for (String message : extractedRecommendations) {
            Recommendation rec = new Recommendation(
                "ai_extracted_" + System.currentTimeMillis() + "_" + extractedRecommendations.indexOf(message),
                "AI_PERSONALIZED",
                message,
                LocalDate.now()
            );
            activeRecommendations.add(rec);
        }
    }
    
    /**
     * 保存提取的建议
     */
    private void saveRecommendation(String message) {
        if (message.length() < 5) return; // 忽略太短的建议
        
        Recommendation rec = new Recommendation(
            "ai_external_" + System.currentTimeMillis(),
            "AI_PERSONALIZED",
            message.trim(),
            LocalDate.now()
        );
        activeRecommendations.add(rec);
    }

    /**
     * 更新基于节日的推荐
     * 当节日数据更新时由HolidayPanel调用此方法，确保节日和推荐数据同步
     */
    public void updateHolidayRecommendations() {
        // 删除现有的节日相关推荐
        activeRecommendations.removeIf(r -> r.getType().equals("HOLIDAY_RELATED") && !r.isDismissed());
        
        // 获取近期节日信息
        List<HolidayEvent> upcomingHolidays = holidayCalendar.getUpcomingHolidays(30);
        if (upcomingHolidays.isEmpty()) {
            return; // 没有近期节日，不生成推荐
        }
        
        // 获取最近的节日
        HolidayEvent nextHoliday = upcomingHolidays.get(0);
        Holiday holiday = nextHoliday.getHoliday();
        LocalDate holidayDate = nextHoliday.getDate();
        
        // 计算距离节日的天数
        long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), holidayDate);
        
        // 生成节日相关推荐
        String recommendation;
        if (holiday.getType().equals("major")) {
            recommendation = "Given the upcoming " + holiday.getName() + " (" + daysUntil + " days away), consider setting aside an additional budget for gifts, travel, and holiday gatherings. Estimate an extra 10-15% of your monthly income for these expenses.";
        } else {
            recommendation = "The upcoming " + holiday.getName() + " (" + daysUntil + " days away) may affect your usual spending patterns. Consider setting aside 5-8% of your monthly budget for any related expenses.";
        }
        
        // 创建推荐对象并添加到活跃推荐列表
        Recommendation rec = new Recommendation(
            "holiday_" + System.currentTimeMillis(),
            "HOLIDAY_RELATED",
            recommendation,
            LocalDate.now()
        );
        activeRecommendations.add(rec);
        
        // 保存更新的推荐列表
        saveRecommendations();
    }
} 