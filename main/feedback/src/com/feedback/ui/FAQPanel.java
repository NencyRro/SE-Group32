package com.feedback.ui;

import com.feedback.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FAQPanel extends JFrame {
    private JTextArea faqArea;

    public FAQPanel() {
        setTitle("Frequently Asked Questions");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        mainPanel.add(UIUtil.createHeaderPanel("Frequently Asked Questions"), BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        contentPanel.setBackground(Constants.ACCENT_COLOR);

        // FAQ content area
        faqArea = new JTextArea();
        faqArea.setEditable(false);
        faqArea.setLineWrap(true);
        faqArea.setWrapStyleWord(true);
        faqArea.setFont(Constants.CONTENT_FONT);
        faqArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        faqArea.setBackground(Color.WHITE);

        // Read FAQ content from file
        faqArea.setText(FileUtil.readFromFile(Constants.FAQ_FILE));

        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(faqArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        searchPanel.setBackground(Constants.ACCENT_COLOR);

        JTextField searchField = new JTextField();
        JButton searchButton = UIUtil.createButton("Search", Constants.PRIMARY_COLOR);
        searchButton.setForeground(Color.BLACK);

        searchPanel.add(new JLabel("Search FAQ: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        contentPanel.add(searchPanel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Constants.ACCENT_COLOR);
        JButton closeButton = UIUtil.createButton("Close", new Color(240, 240, 240));
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add content panel to main panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Set content pane
        setContentPane(mainPanel);

        // Add search functionality
        searchButton.addActionListener(e -> searchFAQ(searchField.getText()));
        searchField.addActionListener(e -> searchFAQ(searchField.getText()));
    }

    private void searchFAQ(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return;
        }

        String text = faqArea.getText();
        if (text == null || text.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No FAQ content to search.",
                    "Search",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Find and highlight search term
        String searchTermLower = searchTerm.toLowerCase();
        int index = text.toLowerCase().indexOf(searchTermLower);

        if (index != -1) {
            faqArea.requestFocus();
            faqArea.setCaretPosition(index);
            faqArea.select(index, index + searchTerm.length());
            faqArea.grabFocus();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Search term not found.",
                    "Search Result",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}