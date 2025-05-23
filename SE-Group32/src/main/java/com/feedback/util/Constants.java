package com.feedback.util;

import java.awt.Color;
import java.awt.Font;

public class Constants {
    // File paths
    public static final String DATA_DIR = "feedback/";
    public static final String FEEDBACK_FILE = DATA_DIR + "feedback.txt";
    public static final String FAQ_FILE = DATA_DIR + "faq.txt";
    public static final String PROBLEMS_FILE = DATA_DIR + "problems.txt";

    // UI Constants
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 18);
    public static final Font CONTENT_FONT = new Font("Arial", Font.PLAIN, 14);
    public static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 14);

    // Colors
    public static final Color PRIMARY_COLOR = new Color(70, 130, 180); // Steel Blue
    public static final Color ACCENT_COLOR = new Color(240, 248, 255); // Alice Blue
    public static final Color TEXT_COLOR = new Color(50, 50, 50);
}

