package com.feedback.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Feedback Application");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        JMenuItem feedbackMenu = new JMenuItem("Feedback Form");
        JMenuItem faqMenu = new JMenuItem("FAQ");
        JMenuItem problemMenu = new JMenuItem("Submit Problem");

        // Add menu items
        menu.add(feedbackMenu);
        menu.add(faqMenu);
        menu.add(problemMenu);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        // Add action listeners
        feedbackMenu.addActionListener((ActionEvent e) -> new FeedbackForm().setVisible(true));
        faqMenu.addActionListener((ActionEvent e) -> new FAQPanel().setVisible(true));
        problemMenu.addActionListener((ActionEvent e) -> new ProblemForm().setVisible(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
