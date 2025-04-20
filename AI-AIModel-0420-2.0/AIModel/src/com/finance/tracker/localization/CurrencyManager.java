package com.finance.tracker.localization;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Manages different currency units and conversion between them
 */
public class CurrencyManager {
    private static final String CONFIG_FILE = "config/currency_config.json";
    private Map<String, Currency> supportedCurrencies;
    private String defaultCurrency;
    
    // Singleton pattern
    private static CurrencyManager instance;
    
    public static CurrencyManager getInstance() {
        if (instance == null) {
            instance = new CurrencyManager();
        }
        return instance;
    }
    
    public CurrencyManager() {
        supportedCurrencies = new HashMap<>();
        loadCurrencies();
    }
    
    /**
     * Loads currency configuration from JSON file
     */
    private void loadCurrencies() {
        JSONParser parser = new JSONParser();
        
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            JSONObject config = (JSONObject) parser.parse(reader);
            defaultCurrency = (String) config.get("defaultCurrency");
            
            JSONArray currencies = (JSONArray) config.get("currencies");
            for (Object currencyObj : currencies) {
                JSONObject currencyJson = (JSONObject) currencyObj;
                
                String code = (String) currencyJson.get("code");
                String name = (String) currencyJson.get("name");
                String symbol = (String) currencyJson.get("symbol");
                double rate = ((Number) currencyJson.get("exchangeRate")).doubleValue();
                
                Currency currency = new Currency(code, name, symbol, BigDecimal.valueOf(rate));
                supportedCurrencies.put(code, currency);
            }
        } catch (IOException | ParseException e) {
            System.err.println("Error loading currencies: " + e.getMessage());
            // Initialize with default values if file not found
            initializeDefaultCurrencies();
        }
    }
    
    /**
     * Initialize with default values if config file is not found
     */
    private void initializeDefaultCurrencies() {
        defaultCurrency = "CNY";
        
        // Add some default currencies
        supportedCurrencies.put("CNY", new Currency("CNY", "Chinese Yuan", "¥", BigDecimal.ONE));
        supportedCurrencies.put("USD", new Currency("USD", "US Dollar", "$", BigDecimal.valueOf(0.14)));
        supportedCurrencies.put("EUR", new Currency("EUR", "Euro", "€", BigDecimal.valueOf(0.13)));
        supportedCurrencies.put("GBP", new Currency("GBP", "British Pound", "£", BigDecimal.valueOf(0.11)));
    }
    
    /**
     * Gets the default currency
     */
    public Currency getDefaultCurrency() {
        return supportedCurrencies.get(defaultCurrency);
    }
    
    /**
     * Sets the default currency
     */
    public void setDefaultCurrency(String currencyCode) {
        if (supportedCurrencies.containsKey(currencyCode)) {
            defaultCurrency = currencyCode;
            saveCurrencyPreference();
        }
    }
    
    /**
     * Gets all supported currencies
     */
    public Map<String, Currency> getSupportedCurrencies() {
        return new HashMap<>(supportedCurrencies);
    }
    
    /**
     * Converts amount from one currency to another
     */
    public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (!supportedCurrencies.containsKey(fromCurrency) || !supportedCurrencies.containsKey(toCurrency)) {
            throw new IllegalArgumentException("Unsupported currency");
        }
        
        // Convert from source currency to CNY (base currency)
        BigDecimal sourceRate = supportedCurrencies.get(fromCurrency).getExchangeRate();
        BigDecimal targetRate = supportedCurrencies.get(toCurrency).getExchangeRate();
        
        // Conversion formula: amount * (targetRate / sourceRate)
        return amount.multiply(sourceRate).divide(targetRate, 2, RoundingMode.HALF_UP);
    }
    
    /**
     * Formats an amount with the currency symbol
     */
    public String format(BigDecimal amount, String currencyCode) {
        if (!supportedCurrencies.containsKey(currencyCode)) {
            return amount.toString();
        }
        
        Currency currency = supportedCurrencies.get(currencyCode);
        return currency.getSymbol() + " " + amount.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Saves user currency preference to JSON
     */
    private void saveCurrencyPreference() {
        JSONObject config = new JSONObject();
        config.put("defaultCurrency", defaultCurrency);
        
        JSONArray currencies = new JSONArray();
        for (Currency currency : supportedCurrencies.values()) {
            JSONObject currencyJson = new JSONObject();
            currencyJson.put("code", currency.getCode());
            currencyJson.put("name", currency.getName());
            currencyJson.put("symbol", currency.getSymbol());
            currencyJson.put("exchangeRate", currency.getExchangeRate().doubleValue());
            currencies.add(currencyJson);
        }
        
        config.put("currencies", currencies);
        
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            writer.write(config.toJSONString());
        } catch (IOException e) {
            System.err.println("Error saving currency preferences: " + e.getMessage());
        }
    }
}

