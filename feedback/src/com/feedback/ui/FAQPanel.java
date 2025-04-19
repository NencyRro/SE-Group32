package com.feedback.ui;

import com.feedback.util.FileUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class FAQPanel extends JFrame {
    public FAQPanel() {
        setTitle("FAQ");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center window on screen

        // Create main panel with margins
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(mainPanel);

        // Create title label
        JLabel titleLabel = new JLabel("Frequently Asked Questions");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Create FAQ content area
        JTextArea faqArea = new JTextArea();
        faqArea.setEditable(false);
        faqArea.setLineWrap(true);
        faqArea.setWrapStyleWord(true);
        faqArea.setFont(new Font("Arial", Font.PLAIN, 14));

        // Read FAQ content from file
        faqArea.setText(FileUtil.readFromFile("faq.txt"));

        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(faqArea);
        scrollPane.setBorder(BorderFactory.createEtchedBorder());

        // Create bottom button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        // Add components to main panel
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
}