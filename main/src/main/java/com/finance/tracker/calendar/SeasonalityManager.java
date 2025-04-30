/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.finance.tracker.calendar;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages seasonality for budgeting recommendations
 */
public class SeasonalityManager {
    // 节日特定的支出系数
    private Map<String, Double> holidayExpenseFactor;
    // 节日特定的提前准备天数
    private Map<String, Integer> holidayPrepDays;
    
    // Singleton pattern
    private static SeasonalityManager instance;
    
    public static SeasonalityManager getInstance() {
        if (instance == null) {
            instance = new SeasonalityManager();
        }
        return instance;
    }
    
    private SeasonalityManager() {
        initializeHolidayFactors();
    }
    
    /**
     * 初始化节日支出系数和准备天数
     */
    private void initializeHolidayFactors() {
        holidayExpenseFactor = new HashMap<>();
        holidayPrepDays = new HashMap<>();
        
        // 添加主要中国节日的支出系数（相对于平时支出的倍数）
        holidayExpenseFactor.put("春节", 2.5);  // 春节支出是平时的2.5倍
        holidayExpenseFactor.put("元宵节", 1.2);
        holidayExpenseFactor.put("清明节", 1.3);
        holidayExpenseFactor.put("劳动节", 1.4);
        holidayExpenseFactor.put("端午节", 1.3);
        holidayExpenseFactor.put("中秋节", 1.8);
        holidayExpenseFactor.put("国庆节", 1.7);
        holidayExpenseFactor.put("重阳节", 1.2);
        
        // 添加节日前需要提前准备的天数
        holidayPrepDays.put("春节", 30);  // 春节前1个月开始准备
        holidayPrepDays.put("元宵节", 7);
        holidayPrepDays.put("清明节", 10);
        holidayPrepDays.put("劳动节", 14);
        holidayPrepDays.put("端午节", 10);
        holidayPrepDays.put("中秋节", 20);
        holidayPrepDays.put("国庆节", 14);
        holidayPrepDays.put("重阳节", 7);
    }
    
    /**
     * Gets the current seasonality factor for budget adjustments
     * Values > 1.0 indicate increased spending periods
     * Values < 1.0 indicate saving periods
     */
    public double getCurrentSeasonalityFactor() {
        ChineseHolidayCalendar calendar = ChineseHolidayCalendar.getInstance();
        LocalDate today = LocalDate.now();
        
        // 检查即将到来的节日
        List<HolidayEvent> upcomingHolidays = calendar.getUpcomingHolidays(45);
        
        for (HolidayEvent event : upcomingHolidays) {
            String holidayName = event.getHoliday().getName();
            if (holidayExpenseFactor.containsKey(holidayName)) {
                long daysUntilHoliday = ChronoUnit.DAYS.between(today, event.getDate());
                int prepDays = holidayPrepDays.getOrDefault(holidayName, 14);
                
                // 如果在节日准备期内
                if (daysUntilHoliday <= prepDays) {
                    double baseFactor = holidayExpenseFactor.get(holidayName);
                    // 越接近节日，支出系数越高
                    double proximityFactor = 1.0 - (daysUntilHoliday / (double)prepDays);
                    return 1.0 + (baseFactor - 1.0) * proximityFactor;
                }
            }
        }
        
        // Check for month-specific patterns
        Month currentMonth = today.getMonth();
        
        // 夏季假期高支出
        if (currentMonth == Month.JULY || currentMonth == Month.AUGUST) {
            return 1.15; // 增加15%预算
        }
        
        // 开学季
        if (currentMonth == Month.AUGUST && today.getDayOfMonth() > 15 || 
            currentMonth == Month.SEPTEMBER && today.getDayOfMonth() < 15) {
            return 1.25; // 增加25%预算
        }
        
        // 年末消费季
        if (currentMonth == Month.NOVEMBER || currentMonth == Month.DECEMBER) {
            return 1.2; // 增加20%预算
        }
        
        // 节后省钱期
        if (currentMonth == Month.FEBRUARY || currentMonth == Month.MARCH) {
            return 0.85; // 减少15%预算
        }
        
        // Default - normal spending
        return 1.0;
    }
    
    /**
     * 检查当前是否在特定节日的准备期
     */
    public boolean isInHolidayPrepPeriod() {
        ChineseHolidayCalendar calendar = ChineseHolidayCalendar.getInstance();
        LocalDate today = LocalDate.now();
        List<HolidayEvent> upcomingHolidays = calendar.getUpcomingHolidays(45);
        
        for (HolidayEvent event : upcomingHolidays) {
            String holidayName = event.getHoliday().getName();
            if (holidayPrepDays.containsKey(holidayName)) {
                long daysUntilHoliday = ChronoUnit.DAYS.between(today, event.getDate());
                int prepDays = holidayPrepDays.get(holidayName);
                if (daysUntilHoliday <= prepDays) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * 获取当前正在准备的节日（如果有）
     */
    public HolidayEvent getUpcomingHolidayInPrepPeriod() {
        ChineseHolidayCalendar calendar = ChineseHolidayCalendar.getInstance();
        LocalDate today = LocalDate.now();
        List<HolidayEvent> upcomingHolidays = calendar.getUpcomingHolidays(45);
        
        for (HolidayEvent event : upcomingHolidays) {
            String holidayName = event.getHoliday().getName();
            if (holidayPrepDays.containsKey(holidayName)) {
                long daysUntilHoliday = ChronoUnit.DAYS.between(today, event.getDate());
                int prepDays = holidayPrepDays.get(holidayName);
                if (daysUntilHoliday <= prepDays) {
                    return event;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Get seasonal budget recommendation
     */
    public String getSeasonalBudgetRecommendation() {
        double factor = getCurrentSeasonalityFactor();
        LocalDate today = LocalDate.now();
        ChineseHolidayCalendar calendar = ChineseHolidayCalendar.getInstance();
        List<HolidayEvent> upcomingHolidays = calendar.getUpcomingHolidays(45);
        
        StringBuilder recommendation = new StringBuilder();
        recommendation.append("近期节假日及财务建议：\n\n");
        
        // 添加即将到来的节日列表
        if (!upcomingHolidays.isEmpty()) {
            recommendation.append("即将到来的节日：\n");
            int count = 0;
            for (HolidayEvent event : upcomingHolidays) {
                if (count >= 3) break; // 最多显示3个节日
                
                long daysUntil = ChronoUnit.DAYS.between(today, event.getDate());
                recommendation.append("- ").append(event.getHoliday().getName())
                             .append("：").append(event.getDate().getMonthValue()).append("月")
                             .append(event.getDate().getDayOfMonth()).append("日");
                
                if (daysUntil <= 7) {
                    recommendation.append("（即将到来）");
                } else {
                    recommendation.append("（").append(daysUntil).append("天后）");
                }
                recommendation.append("\n");
                count++;
            }
            recommendation.append("\n");
        }
        
        // 添加预算建议
        HolidayEvent prepHoliday = getUpcomingHolidayInPrepPeriod();
        if (prepHoliday != null) {
            String holidayName = prepHoliday.getHoliday().getName();
            long daysUntil = ChronoUnit.DAYS.between(today, prepHoliday.getDate());
            double expenseFactor = holidayExpenseFactor.getOrDefault(holidayName, 1.5);
            
            recommendation.append("节日预算建议：\n");
            
            // 针对特定节日的具体建议
            if (holidayName.equals("春节")) {
                recommendation.append("即将迎来春节，这是全年消费最高的时期。建议：\n")
                             .append("1. 提前预留约").append(String.format("%.1f", expenseFactor)).append("倍于平时的预算\n")
                             .append("2. 优先规划年货、礼品和红包支出\n")
                             .append("3. 关注促销活动，避免节日期间高价购物\n");
            } else if (holidayName.equals("中秋节")) {
                recommendation.append("中秋节临近，预计礼品支出会增加。建议：\n")
                             .append("1. 月饼和礼品预算约为平时月度预算的").append(String.format("%.1f", expenseFactor / 2)).append("倍\n")
                             .append("2. 提前购买月饼礼盒可能有优惠\n");
            } else if (holidayName.equals("国庆节")) {
                recommendation.append("国庆长假即将到来，如有出行计划，建议：\n")
                             .append("1. 提前").append(daysUntil > 14 ? "2-4周" : "尽快").append("预订交通和住宿\n")
                             .append("2. 旅游预算应为平时月度预算的").append(String.format("%.1f", expenseFactor)).append("倍\n")
                             .append("3. 避开热门景点高峰期，可节省30%费用\n");
            } else {
                recommendation.append("即将迎来").append(holidayName).append("，预计支出将增加。建议：\n")
                             .append("1. 调整预算至平时的").append(String.format("%.0f%%", expenseFactor * 100)).append("\n")
                             .append("2. 提前").append(daysUntil > 14 ? "两周" : "尽快").append("规划节日消费\n");
            }
        } else if (factor > 1.1) {
            recommendation.append("当前处于消费高峰期：\n")
                         .append("1. 建议将预算临时调整为平时的").append(String.format("%.0f%%", factor * 100)).append("\n")
                         .append("2. 关注必要和非必要支出的区分，控制冲动消费\n");
        } else if (factor < 0.9) {
            recommendation.append("当前是存钱的好时机：\n")
                         .append("1. 建议将支出控制在平时的").append(String.format("%.0f%%", factor * 100)).append("\n")
                         .append("2. 可以增加储蓄或投资比例\n")
                         .append("3. 为将来的大额支出提前做准备\n");
        } else {
            recommendation.append("当前处于正常消费期：\n")
                         .append("1. 保持正常预算分配\n")
                         .append("2. 建议遵循50/30/20法则：50%必要支出，30%个人支出，20%储蓄\n");
        }
        
        return recommendation.toString();
    }
}
