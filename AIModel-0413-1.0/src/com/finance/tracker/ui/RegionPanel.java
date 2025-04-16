package com.finance.tracker.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 地区设置面板
 */
public class RegionPanel extends JPanel {
    private MainModuleUI parentFrame;
    private JList<String> regionList;
    private DefaultListModel<String> regionListModel;
    private JTextArea regionInfoArea;
    
    // 地区信息映射
    private final Map<String, String> regionInfo = new HashMap<>();
    
    /**
     * 构造函数
     */
    public RegionPanel(MainModuleUI parent) {
        this.parentFrame = parent;
        
        // 初始化地区信息
        regionInfo.put("中国大陆", "使用人民币(¥)货币\n支持中国农历节日\n包含中国特色财务分析和建议");
        regionInfo.put("香港", "默认使用港币(HK$)货币\n支持香港本地节日\n包含香港地区财务分析和建议");
        regionInfo.put("台湾", "默认使用新台币(NT$)货币\n支持台湾本地节日\n包含台湾地区财务分析和建议");
        regionInfo.put("新加坡", "默认使用新加坡元(S$)货币\n支持新加坡本地节日\n包含新加坡地区财务分析和建议");
        regionInfo.put("美国", "默认使用美元($)货币\n支持美国本地节日\n包含美国地区财务分析和建议");
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }
    
    /**
     * 初始化组件
     */
    private void initComponents() {
        // 顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("地区设置", JLabel.LEFT);
        titleLabel.setFont(new Font("黑体", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // 中间内容区域
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        contentPanel.setBackground(Color.WHITE);
        
        // 左侧地区列表
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setPreferredSize(new Dimension(200, 300));
        listPanel.setBorder(BorderFactory.createTitledBorder("选择地区"));
        listPanel.setBackground(Color.WHITE);
        
        regionListModel = new DefaultListModel<>();
        regionListModel.addElement("中国大陆");
        regionListModel.addElement("香港");
        regionListModel.addElement("台湾");
        regionListModel.addElement("新加坡");
        regionListModel.addElement("美国");
        
        regionList = new JList<>(regionListModel);
        regionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        regionList.setSelectedIndex(0);
        regionList.setFont(new Font("Dialog", Font.PLAIN, 14));
        regionList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                updateRegionInfo();
            }
        });
        
        JScrollPane listScrollPane = new JScrollPane(regionList);
        listPanel.add(listScrollPane, BorderLayout.CENTER);
        
        contentPanel.add(listPanel, BorderLayout.WEST);
        
        // 右侧地区信息
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("地区信息"));
        infoPanel.setBackground(Color.WHITE);
        
        regionInfoArea = new JTextArea();
        regionInfoArea.setEditable(false);
        regionInfoArea.setFont(new Font("Dialog", Font.PLAIN, 14));
        regionInfoArea.setLineWrap(true);
        regionInfoArea.setWrapStyleWord(true);
        regionInfoArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane infoScrollPane = new JScrollPane(regionInfoArea);
        infoPanel.add(infoScrollPane, BorderLayout.CENTER);
        
        contentPanel.add(infoPanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // 底部按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton applyButton = new JButton("应用设置");
        applyButton.setFont(new Font("Dialog", Font.BOLD, 14));
        applyButton.setBackground(new Color(25, 118, 210));
        applyButton.setForeground(Color.BLACK);
        applyButton.setFocusPainted(false);
        applyButton.addActionListener(e -> applyRegionSettings());
        
        buttonPanel.add(applyButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // 初始显示第一个地区的信息
        updateRegionInfo();
    }
    
    /**
     * 更新地区信息显示
     */
    private void updateRegionInfo() {
        String selectedRegion = regionList.getSelectedValue();
        if (selectedRegion != null && regionInfo.containsKey(selectedRegion)) {
            regionInfoArea.setText(regionInfo.get(selectedRegion));
        } else {
            regionInfoArea.setText("无选中地区或地区信息不可用");
        }
    }
    
    /**
     * 应用地区设置
     */
    private void applyRegionSettings() {
        String selectedRegion = regionList.getSelectedValue();
        if (selectedRegion != null) {
            // 更新主界面状态
            if (parentFrame != null) {
                parentFrame.setRegion(selectedRegion);
                
                // 显示成功消息
                JOptionPane.showMessageDialog(this,
                    "地区已设置为: " + selectedRegion + "\n系统将根据所选地区提供个性化内容和建议。",
                    "设置成功",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
} 