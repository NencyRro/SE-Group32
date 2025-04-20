package com.finance.tracker.ai;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
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
        JSONArray spendingHistory = new JSONArray();
        Map<YearMonth, Map<String, BigDecimal>> history = userProfile.getSpendingHistory().getAllSpendingHistory();
        for (Map.Entry<YearMonth, Map<String, BigDecimal>> entry : history.entrySet()) {
            JSONObject monthData = new JSONObject();
            monthData.put("month", entry.getKey().toString());
            JSONObject categories = new JSONObject();
            for (Map.Entry<String, BigDecimal> catEntry : entry.getValue().entrySet()) {
                categories.put(catEntry.getKey(), catEntry.getValue().toString());
            }
            monthData.put("categories", categories);
            spendingHistory.add(monthData);
        }
        userData.put("spendingHistory", spendingHistory);
        JSONObject budgetInfo = new JSONObject();
        Map<String, BigDecimal> budgets = userProfile.getBudgets();
        for (Map.Entry<String, BigDecimal> entry : budgets.entrySet()) {
            budgetInfo.put(entry.getKey(), entry.getValue().toString());
        }
        userData.put("budgets", budgetInfo);
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
        
        // 系统提示词优化，让模型返回更加个性化和适合中国用户的财务建议
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", 
            "你是一位专业的中国财务顾问，根据用户的消费历史和预算情况，提供个性化的财务建议。" +
            "请分析用户数据并提供3-5条具体的、有针对性的财务建议，特别关注中国国情和季节性消费模式。" +
            "考虑节假日（春节、国庆等）、地区差异及用户的消费偏好。" +
            "每条建议应该简洁明了，并且针对用户的实际情况。\n\n" +
            "建议种类可以包括：\n" +
            "1. 节约建议：基于用户超支类别\n" +
            "2. 预算调整：根据历史消费趋势\n" +
            "3. 节假日消费提醒：即将到来的节日消费准备\n" +
            "4. 储蓄计划：根据收入和必要支出的差额\n" +
            "5. 投资建议：基于储蓄比例和财务状况\n\n" +
            "返回格式必须是JSON，包含recommendations数组，每个元素有message字段。例如：\n" +
            "{\n  \"recommendations\": [\n    {\"message\": \"建议内容1\"},\n    {\"message\": \"建议内容2\"}\n  ]\n}"
        );
        
        // 用户消息内容优化
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", "请根据以下用户数据提供针对性的中国本地化财务建议。" +
            "注意考虑中国特色的消费习惯、节假日影响和地区差异：\n" + userData.toJSONString());
        
        messages.add(systemMessage);
        messages.add(userMessage);
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7); // 增加温度以获得更加多样化的建议
        requestBody.put("max_tokens", 2000); // 设置最大令牌数
        return requestBody.toJSONString();
    }

    private void generateLocalAIRecommendations() {
        String message = "由于当前未启用外部 AI 服务，我们建议您：\n"
                + "1. 检查最近 3 个月的消费记录，重点分析支出排名前 3 的分类；\n"
                + "2. 对照预算目标，手动识别偏离较大的分类；\n"
                + "3. 设置储蓄目标，并考虑为固定支出设置提醒。";

        Recommendation rec = new Recommendation(
            "ai_local_" + System.currentTimeMillis(),
            "AI_PERSONALIZED",
            message,
            LocalDate.now()
        );

        activeRecommendations.add(rec);
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
        // Add other recommendation types as they are implemented
        saveRecommendations();
    }

    /**
     * 从文本内容中提取建议
     */
    private void extractRecommendationsFromText(String content) {
        // 处理可能的Markdown或普通文本格式
        String[] lines = content.split("\\n");
        StringBuilder currentRec = new StringBuilder();
        boolean inRecommendation = false;
        
        for (String line : lines) {
            line = line.trim();
            
            // 检测建议的开始
            if (line.startsWith("-") || line.matches("^\\d+\\..*") || 
                line.startsWith("*") || line.contains("建议") || line.contains("提醒")) {
                
                // 如果已经在处理一条建议，保存之前的建议
                if (inRecommendation && currentRec.length() > 0) {
                    saveRecommendation(currentRec.toString());
                    currentRec = new StringBuilder();
                }
                
                inRecommendation = true;
                currentRec.append(line);
            } 
            // 继续添加到当前建议
            else if (inRecommendation && !line.isEmpty()) {
                currentRec.append(" ").append(line);
            }
            // 空行可能表示建议的结束
            else if (inRecommendation && line.isEmpty() && currentRec.length() > 0) {
                saveRecommendation(currentRec.toString());
                currentRec = new StringBuilder();
                inRecommendation = false;
            }
        }
        
        // 保存最后一条建议
        if (inRecommendation && currentRec.length() > 0) {
            saveRecommendation(currentRec.toString());
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
} 