package com.feedback.ui;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.feedback.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Feedback System");
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        mainPanel.add(UIUtil.createHeaderPanel("Feedback Center"), BorderLayout.NORTH);

        // Center panel with card layout for different views
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Constants.ACCENT_COLOR);

        // Welcome content
        JPanel welcomePanel = new JPanel(new BorderLayout(0, 20));
        welcomePanel.setBackground(Constants.ACCENT_COLOR);

        JLabel welcomeLabel = new JLabel("How can we help you today?");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);

        // Button panel for main options
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        buttonPanel.setBackground(Constants.ACCENT_COLOR);

        // Create action buttons
        createActionButton(buttonPanel, "Submit Feedback",
                "Share your thoughts and suggestions with us",
                e -> new FeedbackForm().setVisible(true));

        createActionButton(buttonPanel, "View FAQ",
                "Find answers to commonly asked questions",
                e -> new FAQPanel().setVisible(true));

        createActionButton(buttonPanel, "Report Problem",
                "Let us know about any issues you're experiencing",
                e -> new ProblemForm().setVisible(true));

        welcomePanel.add(buttonPanel, BorderLayout.CENTER);

//        // Add recent activity panel
//        JPanel recentPanel = new JPanel(new BorderLayout(0, 10));
//        recentPanel.setBackground(Color.WHITE);
//        recentPanel.setBorder(BorderFactory.createTitledBorder("Recent Activity"));
//
//        JTextArea recentActivity = new JTextArea(5, 20);
//        recentActivity.setEditable(false);
//        recentActivity.setText("No recent activity.");
//        JScrollPane recentScroll = new JScrollPane(recentActivity);
//        recentPanel.add(recentScroll, BorderLayout.CENTER);
//
//        welcomePanel.add(recentPanel, BorderLayout.SOUTH);

        // Add welcome panel to content panel
        contentPanel.add(welcomePanel, BorderLayout.CENTER);

        // Status bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusBar.setBackground(new Color(240, 240, 240));

        JLabel statusLabel = new JLabel("Ready");
        statusBar.add(statusLabel, BorderLayout.WEST);

        // Add content panel and status bar
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        // Create menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // Set content pane
        setContentPane(mainPanel);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        // Feedback menu
        JMenu feedbackMenu = new JMenu("Feedback");
        JMenuItem submitFeedbackItem = new JMenuItem("Submit Feedback");
        submitFeedbackItem.addActionListener(e -> new FeedbackForm().setVisible(true));
        feedbackMenu.add(submitFeedbackItem);

        JMenuItem viewFAQItem = new JMenuItem("View FAQ");
        viewFAQItem.addActionListener(e -> new FAQPanel().setVisible(true));
        feedbackMenu.add(viewFAQItem);

        JMenuItem reportProblemItem = new JMenuItem("Report Problem");
        reportProblemItem.addActionListener(e -> new ProblemForm().setVisible(true));
        feedbackMenu.add(reportProblemItem);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(feedbackMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private void createActionButton(JPanel panel, String title, String description,
                                    java.awt.event.ActionListener listener) {
        JPanel buttonCard = new JPanel();
        buttonCard.setLayout(new BoxLayout(buttonCard, BoxLayout.Y_AXIS));
        buttonCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        buttonCard.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea descLabel = new JTextArea(description);
        descLabel.setWrapStyleWord(true);
        descLabel.setLineWrap(true);
        descLabel.setEditable(false);
        descLabel.setBackground(Color.WHITE);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton actionButton = UIUtil.createButton("Open", Constants.PRIMARY_COLOR);
        actionButton.setForeground(Color.BLACK);
        actionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionButton.addActionListener(listener);

        buttonCard.add(titleLabel);
        buttonCard.add(Box.createVerticalStrut(10));
        buttonCard.add(descLabel);
        buttonCard.add(Box.createVerticalStrut(15));
        buttonCard.add(actionButton);

        panel.add(buttonCard);
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "Feedback System v1.0\n" +
                        "A simple application for collecting user feedback and problem reports.\n" +
                        "Created with Java Swing.",
                "About Feedback System",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create directories for data storage
        try {
            Files.createDirectories(Paths.get(Constants.DATA_DIR));
        } catch (Exception e) {
            System.err.println("Error creating data directories: " + e.getMessage());
        }

        // Launch application
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}