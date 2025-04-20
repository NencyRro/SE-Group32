package com.financeapp.model;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FinanceData {
    private int year;
    private List<MonthData> bills;
    
    public FinanceData() {
        bills = new ArrayList<>();
    }
    
    public void loadData(String filePath) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(filePath));
        
        year = ((Long) jsonObject.get("year")).intValue();
        JSONArray billsArray = (JSONArray) jsonObject.get("bills");
        
        for (Object obj : billsArray) {
            JSONObject monthObj = (JSONObject) obj;
            String month = (String) monthObj.get("month");
            double income = ((Number) monthObj.get("income")).doubleValue();
            
            List<Expense> expenses = new ArrayList<>();
            JSONArray expensesArray = (JSONArray) monthObj.get("expenses");
            
            for (Object expObj : expensesArray) {
                JSONObject expenseObj = (JSONObject) expObj;
                String category = (String) expenseObj.get("category");
                double amount = ((Number) expenseObj.get("amount")).doubleValue();
                expenses.add(new Expense(category, amount));
            }
            
            double totalExpenses = ((Number) monthObj.get("total_expenses")).doubleValue();
            double balance = ((Number) monthObj.get("balance")).doubleValue();
            
            bills.add(new MonthData(month, income, expenses, totalExpenses, balance));
        }
    }
    
    public int getYear() {
        return year;
    }
    
    public List<MonthData> getBills() {
        return bills;
    }
    
    public MonthData getMonthData(String month) {
        for (MonthData data : bills) {
            if (data.getMonth().equals(month)) {
                return data;
            }
        }
        return null;
    }
    
    public List<MonthData> getMonthsData(List<String> months) {
        List<MonthData> result = new ArrayList<>();
        for (String month : months) {
            MonthData data = getMonthData(month);
            if (data != null) {
                result.add(data);
            }
        }
        return result;
    }
    
    public Map<String, Double> predictNextMonthExpenses(String currentMonth) {
        MonthData currentData = getMonthData(currentMonth);
        if (currentData == null) {
            return new HashMap<>();
        }
        
        Map<String, Double> prediction = new HashMap<>();
        for (Expense expense : currentData.getExpenses()) {
            // Simple prediction: using current month's data with 5% increase
            prediction.put(expense.getCategory(), expense.getAmount() * 1.05);
        }
        
        return prediction;
    }

    public static class MonthData {
        private String month;
        private double income;
        private List<Expense> expenses;
        private double totalExpenses;
        private double balance;
        
        public MonthData(String month, double income, List<Expense> expenses, 
                        double totalExpenses, double balance) {
            this.month = month;
            this.income = income;
            this.expenses = expenses;
            this.totalExpenses = totalExpenses;
            this.balance = balance;
        }
        
        public String getMonth() {
            return month;
        }
        
        public double getIncome() {
            return income;
        }
        
        public List<Expense> getExpenses() {
            return expenses;
        }
        
        public double getTotalExpenses() {
            return totalExpenses;
        }
        
        public double getBalance() {
            return balance;
        }
        
        public Map<String, Double> getExpensesByCategory() {
            Map<String, Double> result = new HashMap<>();
            for (Expense expense : expenses) {
                result.put(expense.getCategory(), expense.getAmount());
            }
            return result;
        }
    }
    
    public static class Expense {
        private String category;
        private double amount;
        
        public Expense(String category, double amount) {
            this.category = category;
            this.amount = amount;
        }
        
        public String getCategory() {
            return category;
        }
        
        public double getAmount() {
            return amount;
        }
    }
} 