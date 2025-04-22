package com.finance.tracker.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.finance.tracker.calendar.ChineseHolidayCalendar;
import com.finance.tracker.calendar.HolidayEvent;
import com.finance.tracker.localization.LanguageManager;

/**
 * Holiday Spending Guide Panel
 */
public class HolidayPanel extends JPanel {
    private MainModuleUI parentFrame;
    private JPanel holidaysContainer;
    private ChineseHolidayCalendar holidayCalendar;
    private DateTimeFormatter dateFormatter;
    private LanguageManager languageManager;
    private JLabel titleLabel;
    private JButton refreshButton;
    
    /**
     * Constructor
     */
    public HolidayPanel(MainModuleUI parent) {
        this.parentFrame = parent;
        this.languageManager = parent.getLanguageManager();
        this.holidayCalendar = ChineseHolidayCalendar.getInstance();
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }
    
    /**
     * Initialize components
     */
    private void initComponents() {
        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        titleLabel = new JLabel("Holiday Spending Guide", JLabel.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        // Add refresh button
        refreshButton = new JButton("Refresh Holidays");
        refreshButton.addActionListener(e -> refreshHolidays());
        topPanel.add(refreshButton, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Introduction text
        JLabel introLabel = new JLabel("<html><p>Upcoming holidays may affect your spending patterns. Plan your budget in advance.</p><p>Today is: " 
                + LocalDate.now().format(dateFormatter) + "</p></html>");
        introLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        contentPanel.add(introLabel, BorderLayout.NORTH);
        
        // Holidays container with vertical layout
        holidaysContainer = new JPanel();
        holidaysContainer.setLayout(new BoxLayout(holidaysContainer, BoxLayout.Y_AXIS));
        
        // Add holidays
        populateHolidays();
        
        JScrollPane scrollPane = new JScrollPane(holidaysContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Refresh holidays display
     */
    private void refreshHolidays() {
        holidaysContainer.removeAll();
        populateHolidays();
        holidaysContainer.revalidate();
        holidaysContainer.repaint();
        
        // Show confirmation
        JOptionPane.showMessageDialog(this, 
            "Holiday list refreshed based on current date: " + LocalDate.now().format(dateFormatter),
            "Refresh Complete",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Populate with upcoming holidays
     */
    private void populateHolidays() {
        List<Holiday> holidays = getUpcomingHolidays();
        
        if (holidays.isEmpty()) {
            JLabel noHolidaysLabel = new JLabel("No upcoming holidays found for the next 180 days.");
            noHolidaysLabel.setFont(new Font("Dialog", Font.ITALIC, 14));
            noHolidaysLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            holidaysContainer.add(noHolidaysLabel);
            return;
        }
        
        for (Holiday holiday : holidays) {
            JPanel holidayPanel = createHolidayPanel(holiday);
            holidaysContainer.add(holidayPanel);
            holidaysContainer.add(Box.createVerticalStrut(15)); // Add spacing
        }
    }
    
    /**
     * Create a panel for displaying a holiday
     */
    private JPanel createHolidayPanel(Holiday holiday) {
        JPanel panel = new JPanel(new BorderLayout(15, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Left side - Holiday name and date
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        
        // Holiday name
        JLabel nameLabel = new JLabel(holiday.getName());
        nameLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        nameLabel.setForeground(new Color(25, 118, 210));
        leftPanel.add(nameLabel, BorderLayout.NORTH);
        
        // Holiday date
        JLabel dateLabel = new JLabel(holiday.getDate());
        dateLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        leftPanel.add(dateLabel, BorderLayout.CENTER);
        
        // Holiday description
        JLabel descLabel = new JLabel("<html><p>" + holiday.getDescription() + "</p></html>");
        descLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        leftPanel.add(descLabel, BorderLayout.SOUTH);
        
        panel.add(leftPanel, BorderLayout.CENTER);
        
        // Right side - Budget preparation button
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        
        JLabel budgetLabel = new JLabel("<html><p style='color:green'>" + holiday.getBudgetTip() + "</p></html>");
        budgetLabel.setFont(new Font("Dialog", Font.ITALIC, 13));
        rightPanel.add(budgetLabel, BorderLayout.CENTER);
        
        JButton prepareButton = new JButton("Prepare Budget");
        prepareButton.addActionListener((ActionEvent e) -> prepareBudget(holiday));
        rightPanel.add(prepareButton, BorderLayout.SOUTH);
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Prepare budget for a holiday
     */
    private void prepareBudget(Holiday holiday) {
        JOptionPane.showMessageDialog(this,
            "Budget planning for " + holiday.getName() + ":\n\n" +
            "1. Review last year's spending for this holiday\n" +
            "2. Set a reasonable spending limit\n" +
            "3. Create a shopping list before purchasing\n" +
            "4. Look for deals and discounts\n" +
            "5. Track all holiday-related expenses",
            "Holiday Budget Planning",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Get list of upcoming holidays using real calendar
     */
    private List<Holiday> getUpcomingHolidays() {
        // First try to get holidays from the calendar service
        List<Holiday> result = new ArrayList<>();
        
        try {
            // Get holidays for the next 180 days
            List<HolidayEvent> calendarEvents = holidayCalendar.getUpcomingHolidays(180);
            
            for (HolidayEvent event : calendarEvents) {
                com.finance.tracker.calendar.Holiday calHoliday = event.getHoliday();
                LocalDate date = event.getDate();
                String formattedDate = date.format(dateFormatter);
                
                // Calculate days from today
                long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), date);
                
                // Skip past holidays
                if (daysUntil < 0) continue;
                
                String budgetTip = generateBudgetTip(calHoliday.getName(), calHoliday.getType(), daysUntil);
                
                result.add(new Holiday(
                    calHoliday.getName(),
                    formattedDate,
                    calHoliday.getDescription(),
                    budgetTip
                ));
            }
            
            // Sort by date
            Collections.sort(result, Comparator.comparing(Holiday::getDate));
            
            // 获取到有效节日数据后，更新AI推荐引擎以保持数据同步
            if (!result.isEmpty()) {
                // 通知RecommendationEngine更新节日相关建议
                // 这样可以确保AI推荐系统和节日面板看到相同的节日数据
                com.finance.tracker.ai.RecommendationEngine.getInstance().updateHolidayRecommendations();
                
                return result;
            }
        } catch (Exception e) {
            System.err.println("Error getting holidays from calendar: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Fallback to sample holidays if the calendar service failed or returned empty
        return getSampleHolidays();
    }
    
    /**
     * Generate appropriate budget tip based on holiday and days until
     */
    private String generateBudgetTip(String holidayName, String holidayType, long daysUntil) {
        // Major holidays need more budget
        if ("major".equalsIgnoreCase(holidayType)) {
            if (daysUntil < 30) {
                return "Budget Tip: This major holiday is coming soon! Set aside 15-20% of your monthly budget.";
            } else {
                return "Budget Tip: Start planning early for this major holiday. Consider saving 5% monthly until then.";
            }
        }
        
        // Different holidays have different spending impacts
        if (holidayName.contains("New Year") || holidayName.contains("Spring Festival")) {
            return "Budget Tip: Typically high spending for gifts and celebrations. Plan for 10-15% of monthly income.";
        } else if (holidayName.contains("Mid-Autumn")) {
            return "Budget Tip: Major expenses are for mooncakes and gifts. Set aside 15-20% of your monthly budget.";
        } else if (holidayName.contains("Dragon Boat")) {
            return "Budget Tip: Focus spending on food and festival items. Set aside 5-10% of your monthly budget.";
        } else if (holidayName.contains("National Day")) {
            return "Budget Tip: If planning travel, start preparing " + (daysUntil > 90 ? "now" : "immediately") + 
                   ". Budget may reach 1-2 months of total expenses.";
        }
        
        // Generic tip for other holidays
        return "Budget Tip: Consider setting aside some funds for this holiday, around 5-10% of your monthly budget.";
    }
    
    /**
     * Get sample holidays as a fallback
     */
    private List<Holiday> getSampleHolidays() {
        List<Holiday> holidays = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        
        // Create sample holidays with current year instead of hardcoded 2025
        holidays.add(new Holiday(
            "Dragon Boat Festival", 
            currentYear + "-06-10",
            "A traditional holiday commemorating the ancient poet Qu Yuan. People eat rice dumplings and race dragon boats.",
            "Budget Tip: Focus spending on food and festival items. Set aside 5-10% of your monthly budget."
        ));
        
        holidays.add(new Holiday(
            "Mid-Autumn Festival", 
            currentYear + "-09-21",
            "A traditional celebration of family reunion symbolized by moon-gazing and eating mooncakes.",
            "Budget Tip: Major expenses are for mooncakes and gifts. Set aside 15-20% of your monthly budget."
        ));
        
        holidays.add(new Holiday(
            "National Day", 
            currentYear + "-10-01",
            "Celebration of the founding of the People's Republic of China. Typically a 7-day holiday.",
            "Budget Tip: If planning travel, start preparing 3 months in advance. Budget may reach 1-2 months of total expenses."
        ));
        
        return holidays;
    }
    
    /**
     * Holiday data class
     */
    private class Holiday {
        private final String name;
        private final String date;
        private final String description;
        private final String budgetTip;
        
        public Holiday(String name, String date, String description, String budgetTip) {
            this.name = name;
            this.date = date;
            this.description = description;
            this.budgetTip = budgetTip;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDate() {
            return date;
        }
        
        public String getDescription() {
            return description;
        }
        
        public String getBudgetTip() {
            return budgetTip;
        }
    }
    
    /**
     * Update the language of UI components
     */
    public void updateLanguage() {
        titleLabel.setText("Holiday Spending Guide");
        refreshButton.setText("Refresh Holidays");
        
        // Refresh the holidays to update all text
        refreshHolidays();
    }
} 