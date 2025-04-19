package com.feedback.ui;

import com.feedback.util.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ProblemForm extends JFrame {
    public ProblemForm() {
        setTitle("Submit Problem");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create form components
        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        JLabel problemLabel = new JLabel("Problem Description:");
        JTextArea problemArea = new JTextArea();
        JButton submitButton = new JButton("Submit");

        // Add components to panel
        formPanel.add(problemLabel);
        formPanel.add(new JScrollPane(problemArea));
        add(formPanel, BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);

        // Submit button action
        submitButton.addActionListener((ActionEvent e) -> {
            String problem = problemArea.getText();
            FileUtil.saveToFile("problems.txt", problem);
            JOptionPane.showMessageDialog(this, "Problem submitted!");
            dispose();
        });
    }
}
