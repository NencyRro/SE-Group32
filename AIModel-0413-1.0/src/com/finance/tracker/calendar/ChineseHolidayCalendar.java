/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.finance.tracker.calendar;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.finance.tracker.localization.RegionalSettings;

/**
 * Manages Chinese holidays and seasonal events
 */
public class ChineseHolidayCalendar {
    private static final String HOLIDAYS_FILE = "config/chinese_holidays.json";
    private Map<String, List<Holiday>> yearlyHolidays;
    private Map<String, List<Holiday>> lunarHolidays;
    
    // Singleton pattern
    private static ChineseHolidayCalendar instance;
    
    public static ChineseHolidayCalendar getInstance() {
        if (instance == null) {
            instance = new ChineseHolidayCalendar();
        }
        return instance;
    }
    
    private ChineseHolidayCalendar() {
        yearlyHolidays = new HashMap<>();
        lunarHolidays = new HashMap<>();
        loadHolidays();
    }
    
    /**
     * Loads holiday data from JSON file
     */
    private void loadHolidays() {
        JSONParser parser = new JSONParser();
        
        try (FileReader reader = new FileReader(HOLIDAYS_FILE)) {
            JSONObject holidaysConfig = (JSONObject) parser.parse(reader);
            
            // Load yearly holidays (solar calendar)
            JSONArray yearlyArray = (JSONArray) holidaysConfig.get("yearlyHolidays");
            for (Object obj : yearlyArray) {
                JSONObject holidayJson = (JSONObject) obj;
                
                String name = (String) holidayJson.get("name");
                String description = (String) holidayJson.get("description");
                String type = (String) holidayJson.get("type");
                String monthStr = (String) holidayJson.get("month");
                String dayStr = (String) holidayJson.get("day");
                
                Month month = Month.valueOf(monthStr.toUpperCase());
                int day = Integer.parseInt(dayStr);
                
                Holiday holiday = new Holiday(name, description, type);
                
                String key = month.getValue() + "-" + day;
                if (!yearlyHolidays.containsKey(key)) {
                    yearlyHolidays.put(key, new ArrayList<>());
                }
                yearlyHolidays.get(key).add(holiday);
            }
            
            // Load lunar holidays
            JSONArray lunarArray = (JSONArray) holidaysConfig.get("lunarHolidays");
            for (Object obj : lunarArray) {
                JSONObject holidayJson = (JSONObject) obj;
                
                String name = (String) holidayJson.get("name");
                String description = (String) holidayJson.get("description");
                String type = (String) holidayJson.get("type");
                String monthStr = (String) holidayJson.get("lunarMonth");
                String dayStr = (String) holidayJson.get("lunarDay");
                
                int month = Integer.parseInt(monthStr);
                int day = Integer.parseInt(dayStr);
                
                Holiday holiday = new Holiday(name, description, type);
                
                String key = month + "-" + day;
                if (!lunarHolidays.containsKey(key)) {
                    lunarHolidays.put(key, new ArrayList<>());
                }
                lunarHolidays.get(key).add(holiday);
            }
            
        } catch (IOException | ParseException e) {
            System.err.println("Error loading holidays: " + e.getMessage());
            // Initialize with default values
            initializeDefaultHolidays();
        }
    }
    
    /**
     * Initialize with default values if holidays file is not found
     */
    private void initializeDefaultHolidays() {
        // Add some default Chinese holidays (solar calendar)
        addDefaultYearlyHoliday("New Year's Day", "Public holiday celebrating the start of the Gregorian calendar year", "public", Month.JANUARY, 1);
        addDefaultYearlyHoliday("National Day", "Celebration of the founding of the People's Republic of China", "public", Month.OCTOBER, 1);
        addDefaultYearlyHoliday("Labor Day", "International Workers' Day", "public", Month.MAY, 1);
        
        // Add some default lunar holidays
        addDefaultLunarHoliday("Chinese New Year", "Spring Festival celebrating the beginning of the lunar calendar", "major", 1, 1);
        addDefaultLunarHoliday("Mid-Autumn Festival", "Celebration of the harvest and family reunion", "major", 8, 15);
        addDefaultLunarHoliday("Dragon Boat Festival", "Commemorates the death of poet Qu Yuan", "major", 5, 5);
    }
    
    private void addDefaultYearlyHoliday(String name, String description, String type, Month month, int day) {
        Holiday holiday = new Holiday(name, description, type);
        String key = month.getValue() + "-" + day;
        
        if (!yearlyHolidays.containsKey(key)) {
            yearlyHolidays.put(key, new ArrayList<>());
        }
        yearlyHolidays.get(key).add(holiday);
    }
    
    private void addDefaultLunarHoliday(String name, String description, String type, int month, int day) {
        Holiday holiday = new Holiday(name, description, type);
        String key = month + "-" + day;
        
        if (!lunarHolidays.containsKey(key)) {
            lunarHolidays.put(key, new ArrayList<>());
        }
        lunarHolidays.get(key).add(holiday);
    }
    
    /**
     * Check if a given date is a holiday
     */
    public boolean isHoliday(LocalDate date) {
        String key = date.getMonthValue() + "-" + date.getDayOfMonth();
        if (yearlyHolidays.containsKey(key)) {
            return true;
        }
        
        // For lunar holidays, we'd need to convert from Gregorian to Lunar
        // This would require a more complex lunar calendar implementation
        // For simplicity, we're just checking Gregorian dates here
        
        return false;
    }
    
    /**
     * Get holidays for a given date
     */
    public List<Holiday> getHolidays(LocalDate date) {
        List<Holiday> holidays = new ArrayList<>();
        
        String key = date.getMonthValue() + "-" + date.getDayOfMonth();
        if (yearlyHolidays.containsKey(key)) {
            holidays.addAll(yearlyHolidays.get(key));
        }
        
        // Add lunar holidays if applicable
        // This would require a proper lunar calendar conversion
        
        return holidays;
    }
    
    /**
     * Get upcoming holidays within the next specified days
     */
    public List<HolidayEvent> getUpcomingHolidays(int days) {
        List<HolidayEvent> upcomingHolidays = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 0; i < days; i++) {
            LocalDate date = today.plusDays(i);
            List<Holiday> holidays = getHolidays(date);
            
            for (Holiday holiday : holidays) {
                upcomingHolidays.add(new HolidayEvent(holiday, date));
            }
        }
        
        return upcomingHolidays;
    }
    
    /**
     * Check if the current date is within a seasonal spending period
     */
    public boolean isInSeasonalSpendingPeriod() {
        LocalDate today = LocalDate.now();
        List<HolidayEvent> upcomingHolidays = getUpcomingHolidays(30);
        
        for (HolidayEvent event : upcomingHolidays) {
            if (event.getHoliday().getType().equals("major")) {
                long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, event.getDate());
                // If a major holiday is within 14 days
                if (daysUntil <= 14) {
                    return true;
                }
            }
        }
        
        return false;
    }
}
