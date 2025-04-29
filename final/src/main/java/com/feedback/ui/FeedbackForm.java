package com.feedback.ui;

import com.feedback.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FeedbackForm extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JTextArea feedbackArea;
    private JComboBox<String> categoryCombo;
    private JSlider ratingSlider;

    public FeedbackForm() {
        setTitle("Submit Feedback");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Main container with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        mainPanel.add(UIUtil.createHeaderPanel("We Value Your Feedback"), BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Constants.ACCENT_COLOR);

        // Name field
        nameField = new JTextField(20);
        formPanel.add(UIUtil.createFormField("Name:", nameField));

        // Email field
        emailField = new JTextField(20);
        formPanel.add(UIUtil.createFormField("Email Address:", emailField));

        // Category dropdown
        String[] categories = {"General Feedback", "Feature Request", "Bug Report", "Compliment", "Other"};
        categoryCombo = new JComboBox<>(categories);
        formPanel.add(UIUtil.createFormField("Category:", categoryCombo));

        // Rating slider
        ratingSlider = new JSlider(JSlider.HORIZONTAL, 1, 5, 3);
        ratingSlider.setMajorTickSpacing(1);
        ratingSlider.setPaintTicks(true);
        ratingSlider.setPaintLabels(true);
        ratingSlider.setBackground(Constants.ACCENT_COLOR);

        // Custom labels for the slider
        java.util.Hashtable<Integer, JLabel> labelTable = new java.util.Hashtable<>();
        labelTable.put(1, new JLabel("Poor"));
        labelTable.put(2, new JLabel("Fair"));
        labelTable.put(3, new JLabel("Good"));
        labelTable.put(4, new JLabel("Great"));
        labelTable.put(5, new JLabel("Excellent"));
        ratingSlider.setLabelTable(labelTable);

        formPanel.add(UIUtil.createFormField("Rating:", ratingSlider));

        // Feedback text area
        feedbackArea = new JTextArea(6, 20);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        feedbackArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane scrollPane = new JScrollPane(feedbackArea);
        formPanel.add(UIUtil.createFormField("Feedback:", scrollPane));

        // Add some spacing
        formPanel.add(Box.createVerticalStrut(20));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Constants.ACCENT_COLOR);

        JButton cancelButton = UIUtil.createButton("Cancel", new Color(240, 240, 240));
        cancelButton.addActionListener(e -> dispose());

        JButton submitButton = UIUtil.createButton("Submit Feedback", Constants.PRIMARY_COLOR);
        submitButton.setForeground(Color.BLACK);
        submitButton.addActionListener(e -> submitFeedback());

        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);
        formPanel.add(buttonPanel);

        // Add form to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Set content pane
        setContentPane(mainPanel);
    }

    private void submitFeedback() {
        // Basic validation
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your name.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return;
        }

        if (feedbackArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your feedback.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            feedbackArea.requestFocus();
            return;
        }

        // Format feedback data
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formatter);

        StringBuilder content = new StringBuilder();
        content.append("Timestamp: ").append(timestamp).append("\n");
        content.append("Name: ").append(nameField.getText().trim()).append("\n");
        content.append("Email: ").append(emailField.getText().trim()).append("\n");
        content.append("Category: ").append(categoryCombo.getSelectedItem()).append("\n");
        content.append("Rating: ").append(ratingSlider.getValue()).append("/5\n");
        content.append("Feedback: ").append(feedbackArea.getText().trim()).append("\n");
        content.append("----------------------------------------\n");

        // Save feedback
        if (FileUtil.saveToFile(Constants.FEEDBACK_FILE, content.toString())) {
            JOptionPane.showMessageDialog(this,
                    "Thank you for your feedback!",
                    "Feedback Submitted",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }
}