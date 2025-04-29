package com.feedback.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UIUtil {
    public static JPanel createHeaderPanel(String title) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Constants.PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Constants.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        return headerPanel;
    }

    public static JButton createButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(Constants.TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(background.darker(), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        return button;
    }

    public static JPanel createFormField(String labelText, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBorder(new EmptyBorder(5, 0, 5, 0));

        JLabel label = new JLabel(labelText);
        label.setFont(Constants.LABEL_FONT);

        panel.add(label, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);

        return panel;
    }
}