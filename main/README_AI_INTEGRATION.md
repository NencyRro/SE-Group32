# AI Module Integration Guide

This document describes how to integrate the AI recommendation functionality from the AI-MODEL folder into the main application.

## Integration Overview

The integration incorporates a financial analysis tool based on the DeepSeek API, replacing the original recommendation feature. The new AI recommendation feature has the following characteristics:

1. Uses TransactionDataCenter to obtain unified transaction data
2. Provides expense details viewing functionality
3. Provides AI-powered analysis capability
4. Displays in a standalone window, without affecting the main application

## Integrated Files

The following files have been integrated:

1. `src/main/java/com/finance/module/ai/FinanceAnalyzer.java` - AI analysis tool main interface
2. `lib/json/json-20160810.jar` - JSON processing library
3. `lib/httpclient-4.5.13.jar` - HTTP client library
4. `lib/httpcore-4.4.12.jar` - HTTP core library

The following files have been modified:

- `src/main/java/com/finance/tracker/ui/TransactionDashboardPanel.java` - Updated the openRecommendationPanel method

## Usage Instructions

1. Click the "AI Recommendations" button on the main interface
2. This will open a new AI analysis tool window
3. Click "Details" -> "View Details" in the menu to view expense details
4. Click the "Analyze" button in the details dialog to get AI analysis results

## Notes

1. AI analysis requires internet connection to access the DeepSeek API
2. Ensure all necessary JAR files are added to the project's classpath
3. The AI module obtains data directly from TransactionDataCenter, no need to maintain a separate data source
4. Closing the AI analysis window will not affect the operation of the main application 