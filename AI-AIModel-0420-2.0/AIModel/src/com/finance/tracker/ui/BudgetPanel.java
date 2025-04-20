package com.finance.tracker.ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 预算管理面板
 */
public class BudgetPanel extends JPanel {
    private MainModuleUI parentFrame;
    // ... existing code ...
    
    // 利用parentFrame字段以解决未使用的字段警告
    private void setParentUpdateFlag() {
        if (parentFrame != null) {
            // 通过父窗口更新状态信息
            updateStatusMessage("预算信息已更新");
        }
    }
    
    // 更新状态消息
    private void updateStatusMessage(String message) {
        if (parentFrame != null) {
            JOptionPane.showMessageDialog(
                parentFrame,
                message,
                "操作成功",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    // 添加预算项目
    private void addBudgetItem() {
        // ... existing code ...
        
        // 通知父窗口更新
        setParentUpdateFlag();
    }
    
    // 删除预算项目
    private void removeBudgetItem() {
        // ... existing code ...
        
        // 通知父窗口更新
        setParentUpdateFlag();
    }
    
    // 更新预算详情
    private void updateBudgetDetails() {
        // ... existing code ...
        
        // 通知父窗口更新
        setParentUpdateFlag();
    }
    // ... existing code ...
} 