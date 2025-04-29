package com.finance.tracker.classification.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

/**
 * Category Icon Manager - Responsible for loading and caching category icons
 */
public class CategoryIconManager {
    private static final Map<String, ImageIcon> iconCache = new HashMap<>();
    private static final String DEFAULT_ICON_PATH = "/icons/category_default.png";
    
    /**
     * Get category icon
     * 
     * @param iconPath Icon path
     * @param size Icon size
     * @return Icon object
     */
    public static ImageIcon getIcon(String iconPath, int size) {
        if (iconPath == null || iconPath.trim().isEmpty()) {
            return getDefaultIcon(size);
        }
        
        String cacheKey = iconPath + "_" + size;
        
        // Check cache
        if (iconCache.containsKey(cacheKey)) {
            return iconCache.get(cacheKey);
        }
        
        // Try to load icon
        ImageIcon icon = loadIcon(iconPath, size);
        
        // If loading fails, use default icon
        if (icon == null) {
            icon = getDefaultIcon(size);
        }
        
        // Cache and return
        iconCache.put(cacheKey, icon);
        return icon;
    }
    
    /**
     * Get default icon
     * 
     * @param size Icon size
     * @return Default icon
     */
    public static ImageIcon getDefaultIcon(int size) {
        String cacheKey = DEFAULT_ICON_PATH + "_" + size;
        
        if (iconCache.containsKey(cacheKey)) {
            return iconCache.get(cacheKey);
        }
        
        // Try to load default icon from resources
        URL resourceUrl = CategoryIconManager.class.getResource(DEFAULT_ICON_PATH);
        if (resourceUrl != null) {
            ImageIcon icon = new ImageIcon(resourceUrl);
            icon = resizeIcon(icon, size, size);
            iconCache.put(cacheKey, icon);
            return icon;
        }
        
        // If no default icon resource exists, create an empty icon
        return createEmptyIcon(size);
    }
    
    /**
     * Load icon
     * 
     * @param path Icon path
     * @param size Icon size
     * @return Loaded icon
     */
    private static ImageIcon loadIcon(String path, int size) {
        // First try to load from resources
        URL resourceUrl = CategoryIconManager.class.getResource(path);
        if (resourceUrl != null) {
            ImageIcon icon = new ImageIcon(resourceUrl);
            return resizeIcon(icon, size, size);
        }
        
        // Then try to load from file system
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            ImageIcon icon = new ImageIcon(file.getAbsolutePath());
            return resizeIcon(icon, size, size);
        }
        
        return null;
    }
    
    /**
     * Resize icon
     * 
     * @param icon Original icon
     * @param width Target width
     * @param height Target height
     * @return Resized icon
     */
    private static ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }
    
    /**
     * Create empty icon
     * 
     * @param size Icon size
     * @return Empty icon
     */
    private static ImageIcon createEmptyIcon(int size) {
        // Create a simple circular icon
        BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillOval(0, 0, size, size);
        g2d.setColor(Color.GRAY);
        g2d.drawOval(0, 0, size - 1, size - 1);
        
        g2d.dispose();
        
        ImageIcon icon = new ImageIcon(bi);
        String cacheKey = "empty_" + size;
        iconCache.put(cacheKey, icon);
        return icon;
    }
}