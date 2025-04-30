package com.finance.tracker.ui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * 应用程序启动器
 */
public class AppLauncher {
    public static void main(String[] args) {
        // 设置外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 启动应用
        SwingUtilities.invokeLater(() -> {
            try {
                MainModuleUI app = new MainModuleUI();
                app.setVisible(true);
                System.out.println("应用程序已启动");
            } catch (Exception e) {
                System.err.println("应用程序启动失败: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
} 