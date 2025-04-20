/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.finance.tracker.localization;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Represents a single currency with its properties
 */
public class Currency {
    private String code;
    private String name;
    private String symbol;
    private BigDecimal exchangeRate; // Relative to base currency (CNY)
    
    public Currency(String code, String name, String symbol, BigDecimal exchangeRate) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
        this.exchangeRate = exchangeRate;
    }
    
    // Getters
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getSymbol() { return symbol; }
    public BigDecimal getExchangeRate() { return exchangeRate; }
    
    @Override
    public String toString() {
        return name + " (" + symbol + ")";
    }
}

