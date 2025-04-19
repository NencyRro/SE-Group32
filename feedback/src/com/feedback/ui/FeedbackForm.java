package com.feedback.ui;

import com.feedback.util.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class FeedbackForm extends JFrame {
    public FeedbackForm() {
        setTitle("Feedback Form");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create form components
        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        JLabel feedbackLabel = new JLabel("Feedback:");
        JTextArea feedbackArea = new JTextArea();
        JButton submitButton = new JButton("Submit");

        // Add components to panel
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(feedbackLabel);
        formPanel.add(new JScrollPane(feedbackArea));
        add(formPanel, BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);

        // Submit button action
        submitButton.addActionListener((ActionEvent e) -> {
            String name = nameField.getText();
            String feedback = feedbackArea.getText();
            FileUtil.saveToFile("feedback.txt", name + ": " + feedback);
            JOptionPane.showMessageDialog(this, "Feedback submitted!");
            dispose();
        });
    }
}
