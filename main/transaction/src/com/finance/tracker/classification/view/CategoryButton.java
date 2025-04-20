package com.finance.tracker.classification.view;

import com.finance.tracker.classification.model.Category;
import com.finance.tracker.classification.util.CategoryIconManager;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import javax.swing.*;

/**
 * Circular Category Button Component - Used to display a category option
 */
public class CategoryButton extends JButton {
    private Category category;
    private boolean isHovered = false;
    private boolean isSelected = false;
    private static final int ICON_SIZE = 24; // Icon size
    
    /**
     * Create a new category button
     * 
     * @param category Associated category
     */
    public CategoryButton(Category category) {
        this.category = category;
        
        // Set button display properties
        setText(category.getName());
        setPreferredSize(new Dimension(80, 80));
        setMaximumSize(new Dimension(80, 80));
        setMinimumSize(new Dimension(80, 80));
        
        // Set to transparent for custom drawing
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        
        // Set tooltip
        setToolTipText(category.getName());
        
        // Add mouse listener to implement hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }
    
    /**
     * Custom component painting
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Create circle
        Shape circle = new Ellipse2D.Double(2, 2, width - 4, height - 4);
        
        // Draw circular background
        Color baseColor = category.getColor();
        if (isSelected) {
            // Selected state - use darker color
            g2.setColor(baseColor.darker());
        } else if (isHovered) {
            // Hover state - use slightly brighter color
            g2.setColor(baseColor.brighter());
        } else {
            // Normal state
            g2.setColor(baseColor);
        }
        
        g2.fill(circle);
        
        // Draw border
        g2.setColor(isSelected ? Color.BLACK : Color.GRAY);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(circle);
        
        // If there's an icon, draw it
        if (category.getIconPath() != null) {
            ImageIcon icon = CategoryIconManager.getIcon(category.getIconPath(), ICON_SIZE);
            if (icon != null) {
                int iconX = (width - ICON_SIZE) / 2;
                int iconY = height / 3 - ICON_SIZE / 2;
                g2.drawImage(icon.getImage(), iconX, iconY, this);
            }
        }
        
        // Draw text - use multiline drawing to accommodate long text
        drawMultilineText(g2, getText(), width, height);
        
        g2.dispose();
    }
    
    /**
     * Draw multiline text
     */
    private void drawMultilineText(Graphics2D g2, String text, int width, int height) {
        if (text == null || text.isEmpty()) {
            return;
        }
        
        // Set text color, automatically choose black or white based on background color for better readability
        if (isDarkColor(category.getColor())) {
            g2.setColor(Color.WHITE);
        } else {
            g2.setColor(Color.BLACK);
        }
        
        // Use a smaller font
        Font originalFont = g2.getFont();
        Font smallerFont = originalFont.deriveFont(originalFont.getSize() - 1.0f);
        g2.setFont(smallerFont);
        
        FontMetrics fm = g2.getFontMetrics();
        
        // If text is too long, try to break at spaces
        if (fm.stringWidth(text) > width - 10) {
            String[] words = text.split(" ");
            java.util.List<String> lines = new java.util.ArrayList<>();
            StringBuilder currentLine = new StringBuilder();
            
            for (String word : words) {
                if (fm.stringWidth(currentLine + word) < width - 10) {
                    if (currentLine.length() > 0) currentLine.append(" ");
                    currentLine.append(word);
                } else {
                    if (currentLine.length() > 0) {
                        lines.add(currentLine.toString());
                        currentLine = new StringBuilder(word);
                    } else {
                        // Word is too long, force line break
                        lines.add(word);
                    }
                }
            }
            
            if (currentLine.length() > 0) {
                lines.add(currentLine.toString());
            }
            
            // Calculate vertical position of text - place text in bottom area of circle
            int textY = height / 2 + fm.getAscent();
            
            // If there's only one line, draw it in the center
            if (lines.size() == 1) {
                int textX = (width - fm.stringWidth(lines.get(0))) / 2;
                g2.drawString(lines.get(0), textX, textY);
            } else {
                // For multiple lines, start from calculated Y position and increment for each line
                for (int i = 0; i < lines.size() && i < 2; i++) { // Show maximum 2 lines
                    String line = lines.get(i);
                    int textX = (width - fm.stringWidth(line)) / 2;
                    g2.drawString(line, textX, textY + i * fm.getHeight());
                }
            }
        } else {
            // Single line text, draw in center
            int textX = (width - fm.stringWidth(text)) / 2;
            int textY = height / 2 + fm.getAscent();
            g2.drawString(text, textX, textY);
        }
        
        // Restore original font
        g2.setFont(originalFont);
    }
    
    /**
     * Determine if a color is dark (used to decide text color)
     */
    private boolean isDarkColor(Color color) {
        // Use relative luminance formula (0.299*R + 0.587*G + 0.114*B)
        double brightness = 0.299 * color.getRed() + 
                            0.587 * color.getGreen() + 
                            0.114 * color.getBlue();
        return brightness < 128;
    }
    
    /**
     * Make the button's clickable area circular
     */
    @Override
    public boolean contains(int x, int y) {
        // Check if click is inside the circle
        double center_x = getWidth() / 2.0;
        double center_y = getHeight() / 2.0;
        double radius = Math.min(getWidth(), getHeight()) / 2.0 - 2;
        
        return Math.pow(x - center_x, 2) + Math.pow(y - center_y, 2) <= Math.pow(radius, 2);
    }
    
    /**
     * Set the selected state of the button
     * 
     * @param selected Whether selected
     */
    public void setSelected(boolean selected) {
        this.isSelected = selected;
        repaint();
    }
    
    /**
     * Get the associated category
     * 
     * @return Category object
     */
    public Category getCategory() {
        return category;
    }
}