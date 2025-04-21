package com.feedback.ui;

import com.feedback.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProblemForm extends JFrame {
    private JTextField titleField;
    private JComboBox<String> priorityCombo;
    private JTextArea problemArea;
    private JTextField contactField;

    public ProblemForm() {
        setTitle("Submit Problem Report");
        setSize(550, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        mainPanel.add(UIUtil.createHeaderPanel("Report a Problem"), BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Constants.ACCENT_COLOR);

        // Title field
        titleField = new JTextField(30);
        formPanel.add(UIUtil.createFormField("Problem Title:", titleField));

        // Priority dropdown
        String[] priorities = {"Low", "Medium", "High", "Critical"};
        priorityCombo = new JComboBox<>(priorities);
        priorityCombo.setSelectedIndex(1); // Default to Medium
        formPanel.add(UIUtil.createFormField("Priority:", priorityCombo));

        // Problem description
        problemArea = new JTextArea(8, 30);
        problemArea.setLineWrap(true);
        problemArea.setWrapStyleWord(true);
        problemArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane scrollPane = new JScrollPane(problemArea);
        formPanel.add(UIUtil.createFormField("Problem Description:", scrollPane));

        // Contact field
        contactField = new JTextField(30);
        formPanel.add(UIUtil.createFormField("Contact Information (optional):", contactField));

        // Add some spacing
        formPanel.add(Box.createVerticalStrut(20));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Constants.ACCENT_COLOR);

        JButton cancelButton = UIUtil.createButton("Cancel", new Color(240, 240, 240));
        cancelButton.addActionListener(e -> dispose());

        JButton submitButton = UIUtil.createButton("Submit Problem", Constants.PRIMARY_COLOR);
        submitButton.setForeground(Color.BLACK);
        submitButton.addActionListener(e -> submitProblem());

        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);
        formPanel.add(buttonPanel);

        // Add form to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Set content pane
        setContentPane(mainPanel);
    }

    private void submitProblem() {
        // Basic validation
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a problem title.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            titleField.requestFocus();
            return;
        }

        if (problemArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please describe the problem.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            problemArea.requestFocus();
            return;
        }

        // Format problem data
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formatter);

        StringBuilder content = new StringBuilder();
        content.append("Timestamp: ").append(timestamp).append("\n");
        content.append("Title: ").append(titleField.getText().trim()).append("\n");
        content.append("Priority: ").append(priorityCombo.getSelectedItem()).append("\n");
        content.append("Description: ").append(problemArea.getText().trim()).append("\n");
        content.append("Contact: ").append(contactField.getText().trim()).append("\n");
        content.append("----------------------------------------\n");

        // Save problem report
        if (FileUtil.saveToFile(Constants.PROBLEMS_FILE, content.toString())) {
            JOptionPane.showMessageDialog(this,
                    "Thank you for submitting your problem report!\nWe will look into it as soon as possible.",
                    "Problem Submitted",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }
}
