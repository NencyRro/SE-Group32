/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.finance.tracker.calendar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.finance.tracker.localization.RegionalSettings;

/**
 * Represents a holiday event (a holiday on a specific date)
 */
public class HolidayEvent {
    private Holiday holiday;
    private LocalDate date;
    
    public HolidayEvent(Holiday holiday, LocalDate date) {
        this.holiday = holiday;
        this.date = date;
    }
    
    // Getters
    public Holiday getHoliday() { return holiday; }
    public LocalDate getDate() { return date; }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(RegionalSettings.getInstance().getDateFormat());
        return holiday.getName() + " (" + date.format(formatter) + ")";
    }
}
