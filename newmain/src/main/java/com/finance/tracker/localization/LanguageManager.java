package com.finance.tracker.localization;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages language settings and text localization for the application
 * Note: Language switching has been disabled, English is now the only supported language
 */
public class LanguageManager {
    
    public enum Language {
        ENGLISH,
        CHINESE  // Kept for compatibility but not used
    }
    
    private static LanguageManager instance;
    private Language currentLanguage = Language.ENGLISH;
    private Map<String, Map<Language, String>> translations = new HashMap<>();
    
    // Translation keys
    public static final String TITLE = "title";
    public static final String NAVIGATION = "navigation";
    public static final String GENERATE_AI = "generate_ai";
    public static final String VIEW_HOLIDAYS = "view_holidays";
    public static final String CHANGE_CURRENCY = "change_currency";
    public static final String ADD_TRANSACTION = "add_transaction";
    public static final String READY = "ready";
    public static final String CURRENCY = "currency";
    public static final String LAST_RECOMMENDATION = "last_recommendation";
    public static final String NONE = "none";
    public static final String WELCOME_TITLE = "welcome_title";
    public static final String WELCOME_DESC = "welcome_desc";
    public static final String WELCOME_CLICK = "welcome_click";
    public static final String WELCOME_TIP = "welcome_tip";
    public static final String AI_RECOMMENDATIONS = "ai_recommendations";
    public static final String GENERATING = "generating";
    public static final String ANALYZING = "analyzing";
    public static final String RECOMMENDATION = "recommendation";
    public static final String NO_RECOMMENDATIONS = "no_recommendations";
    public static final String UNABLE_CONNECT = "unable_connect";
    public static final String APPLY_SETTINGS = "apply_settings";
    public static final String SETTINGS_UPDATED = "settings_updated";
    public static final String UPDATING = "updating";
    public static final String ERROR_UPDATE = "error_update";
    
    // Holiday panel texts
    public static final String HOLIDAY_GUIDE = "holiday_guide";
    public static final String REFRESH_HOLIDAYS = "refresh_holidays";
    public static final String UPCOMING_HOLIDAYS = "upcoming_holidays";
    public static final String TODAY_IS = "today_is";
    public static final String PREPARE_BUDGET = "prepare_budget";
    public static final String REFRESH_COMPLETE = "refresh_complete";
    public static final String HOLIDAY_BUDGET_PLANNING = "holiday_budget_planning";
    public static final String NO_HOLIDAYS = "no_holidays";
    
    // Transaction panel texts
    public static final String TRANSACTION_MANAGEMENT = "transaction_management";
    public static final String DATE = "date";
    public static final String CATEGORY = "category";
    public static final String AMOUNT = "amount";
    public static final String DESCRIPTION = "description";
    public static final String ADD_NEW_TRANSACTION = "add_new_transaction";
    public static final String ADD_TRANSACTION_BTN = "add_transaction_btn";
    public static final String ACTIONS = "actions";
    public static final String DELETE_SELECTED = "delete_selected";
    public static final String GENERATE_REPORT = "generate_report";
    public static final String TOTAL_TRANSACTIONS = "total_transactions";
    public static final String TOTAL_EXPENSE = "total_expense";
    public static final String TRANSACTION_ADDED = "transaction_added";
    public static final String INPUT_ERROR = "input_error";
    public static final String ENTER_AMOUNT = "enter_amount";
    public static final String INVALID_AMOUNT = "invalid_amount";
    public static final String TRANSACTION_DELETED = "transaction_deleted";
    public static final String SELECT_TRANSACTION = "select_transaction";
    public static final String TRANSACTION_REPORT = "transaction_report";
    public static final String INFORMATION = "information";
    public static final String NO_TRANSACTIONS = "no_transactions";
    public static final String SUCCESS = "success";
    public static final String CLOSE = "close";
    
    // Currency panel texts
    public static final String CURRENCY_SETTINGS = "currency_settings";
    public static final String SELECT_CURRENCY = "select_currency";
    public static final String CURRENCY_INFO = "currency_info";
    public static final String CURRENCY_INSTRUCTION = "currency_instruction";
    public static final String NO_CURRENCY_INFO = "no_currency_info";
    public static final String CURRENCY_UPDATED = "currency_updated";
    
    /**
     * Get singleton instance
     */
    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }
    
    /**
     * Private constructor - initialize translations
     */
    private LanguageManager() {
        initializeTranslations();
    }
    
    /**
     * Set up all translations
     */
    private void initializeTranslations() {
        // Main UI texts
        addTranslation(TITLE, "AI Financial Assistant", "AI财务助手");
        addTranslation(NAVIGATION, "Navigation", "导航");
        addTranslation(GENERATE_AI, "Generate AI Recommendations", "生成AI推荐");
        addTranslation(VIEW_HOLIDAYS, "View Upcoming Holidays", "查看即将到来的节日");
        addTranslation(CHANGE_CURRENCY, "Change Currency", "更改货币");
        addTranslation(ADD_TRANSACTION, "Add Test Transaction", "添加测试交易");
        addTranslation(READY, "Ready", "就绪");
        addTranslation(CURRENCY, "Currency", "货币");
        addTranslation(LAST_RECOMMENDATION, "Last recommendation", "上次推荐");
        addTranslation(NONE, "None", "无");
        
        // Recommendation panel texts
        addTranslation(AI_RECOMMENDATIONS, "AI Financial Recommendations", "AI财务推荐");
        addTranslation(WELCOME_TITLE, "Welcome to AI Financial Assistant", "欢迎使用AI财务助手");
        addTranslation(WELCOME_DESC, "AI Financial Assistant provides personalized financial advice based on your spending habits and budget.", 
                      "AI财务助手基于您的消费习惯和预算提供个性化的财务建议。");
        addTranslation(WELCOME_CLICK, "Click \"Generate New Recommendations\" to get your personalized financial advice.", 
                      "点击\"生成新推荐\"获取您的个性化财务建议。");
        addTranslation(WELCOME_TIP, "Tip: Recommendations will be displayed in this panel. You can save or copy the content you need.", 
                      "提示：推荐将显示在此面板中。您可以保存或复制所需的内容。");
        addTranslation(GENERATING, "Generating AI financial recommendations, please wait...", 
                      "正在生成AI财务推荐，请稍候...");
        addTranslation(ANALYZING, "Analyzing your financial data, please wait...", 
                      "正在分析您的财务数据，请稍候...");
        addTranslation(RECOMMENDATION, "Recommendation", "推荐");
        addTranslation(NO_RECOMMENDATIONS, "No recommendations found for you", 
                      "未找到适合您的推荐");
        addTranslation(UNABLE_CONNECT, "Unable to connect to AI service, showing simulated recommendations", 
                      "无法连接到AI服务，显示模拟推荐");
        
        // 添加新的翻译条目
        addTranslation(UPDATING, "Updating recommendations...", "正在更新推荐...");
        addTranslation(ERROR_UPDATE, "Error updating recommendations", "更新推荐时出错");
        
        // Currency panel texts
        addTranslation(CURRENCY_SETTINGS, "Currency Settings", "货币设置");
        addTranslation(SELECT_CURRENCY, "Select Currency", "选择货币");
        addTranslation(CURRENCY_INFO, "Currency Information", "货币信息");
        addTranslation(CURRENCY_INSTRUCTION, "Select the currency you wish to use in the application. All monetary values will be displayed in the selected currency.", 
                      "选择您希望在应用程序中使用的货币。所有货币值将以所选货币显示。");
        addTranslation(NO_CURRENCY_INFO, "No information available for the selected currency.", 
                      "所选货币没有可用信息。");
        addTranslation(APPLY_SETTINGS, "Apply Settings", "应用设置");
        addTranslation(SETTINGS_UPDATED, "Settings Updated", "设置已更新");
        addTranslation(CURRENCY_UPDATED, "Currency settings updated to", "货币设置已更新为");
        
        // Holiday panel texts
        addTranslation(HOLIDAY_GUIDE, "Holiday Spending Guide", "节日消费指南");
        addTranslation(REFRESH_HOLIDAYS, "Refresh Holidays", "刷新节日");
        addTranslation(UPCOMING_HOLIDAYS, "Upcoming holidays may affect your spending patterns. Plan your budget in advance.", 
                      "即将到来的节日可能会影响您的消费模式。提前规划您的预算。");
        addTranslation(TODAY_IS, "Today is", "今天是");
        addTranslation(PREPARE_BUDGET, "Prepare Budget", "准备预算");
        addTranslation(REFRESH_COMPLETE, "Holiday list refreshed based on current date", 
                      "基于当前日期刷新节日列表");
        addTranslation(HOLIDAY_BUDGET_PLANNING, "Holiday Budget Planning", "节日预算规划");
        addTranslation(NO_HOLIDAYS, "No upcoming holidays found for the next 180 days.", 
                      "未找到未来180天的节日。");
        
        // Transaction panel texts
        addTranslation(TRANSACTION_MANAGEMENT, "Transaction Management", "交易管理");
        addTranslation(DATE, "Date", "日期");
        addTranslation(CATEGORY, "Category", "类别");
        addTranslation(AMOUNT, "Amount", "金额");
        addTranslation(DESCRIPTION, "Description", "描述");
        addTranslation(ADD_NEW_TRANSACTION, "Add New Transaction", "添加新交易");
        addTranslation(ADD_TRANSACTION_BTN, "Add Transaction", "添加交易");
        addTranslation(ACTIONS, "Actions", "操作");
        addTranslation(DELETE_SELECTED, "Delete Selected", "删除所选");
        addTranslation(GENERATE_REPORT, "Generate Report", "生成报告");
        addTranslation(TOTAL_TRANSACTIONS, "Total", "总计");
        addTranslation(TOTAL_EXPENSE, "Total expense", "总支出");
        addTranslation(TRANSACTION_ADDED, "Transaction added successfully!", "交易添加成功！");
        addTranslation(INPUT_ERROR, "Input Error", "输入错误");
        addTranslation(ENTER_AMOUNT, "Please enter an amount", "请输入金额");
        addTranslation(INVALID_AMOUNT, "Invalid amount format. Please enter a valid number", 
                      "金额格式无效。请输入有效数字");
        addTranslation(TRANSACTION_DELETED, "Transaction deleted", "交易已删除");
        addTranslation(SELECT_TRANSACTION, "Please select a transaction to delete", 
                      "请选择要删除的交易");
        addTranslation(TRANSACTION_REPORT, "Transaction Report", "交易报告");
        addTranslation(INFORMATION, "Information", "信息");
        addTranslation(NO_TRANSACTIONS, "No transactions available to generate a report", 
                      "没有可用的交易来生成报告");
        addTranslation(SUCCESS, "Success", "成功");
        addTranslation(CLOSE, "Close", "关闭");
    }
    
    /**
     * Add a translation for a key
     */
    private void addTranslation(String key, String englishText, String chineseText) {
        Map<Language, String> languageMap = new HashMap<>();
        languageMap.put(Language.ENGLISH, englishText);
        languageMap.put(Language.CHINESE, chineseText);
        translations.put(key, languageMap);
    }
    
    /**
     * Get current language setting
     */
    public Language getCurrentLanguage() {
        return currentLanguage;
    }
    
    /**
     * Set current language
     */
    public void setLanguage(Language language) {
        // Always set to English regardless of input
        this.currentLanguage = Language.ENGLISH;
    }
    
    /**
     * Toggle between available languages (now disabled)
     */
    public Language toggleLanguage() {
        // Language switching has been disabled
        // Always return English
        return Language.ENGLISH;
    }
    
    /**
     * Get text for a specific key in the current language
     */
    public String getText(String key) {
        if (translations.containsKey(key)) {
            Map<Language, String> languageMap = translations.get(key);
            if (languageMap.containsKey(Language.ENGLISH)) {
                return languageMap.get(Language.ENGLISH);
            }
        }
        return key; // Return the key itself as fallback
    }
    
    /**
     * Get text for a specific key in a specific language
     */
    public String getText(String key, Language language) {
        // Always return English text regardless of requested language
        if (translations.containsKey(key)) {
            Map<Language, String> languageMap = translations.get(key);
            if (languageMap.containsKey(Language.ENGLISH)) {
                return languageMap.get(Language.ENGLISH);
            }
        }
        return key; // Return the key itself as fallback
    }
} 