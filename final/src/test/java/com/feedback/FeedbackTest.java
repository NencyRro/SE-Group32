package com.feedback;

import com.feedback.ui.MainFrame;
import com.feedback.ui.FeedbackForm;
import com.feedback.ui.FAQPanel;
import com.feedback.ui.ProblemForm;
import com.feedback.util.Constants;
import com.feedback.util.FileUtil;
import com.feedback.util.UIUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FeedbackTest {

    private MainFrame mainFrame;
    private FeedbackForm feedbackForm;
    private FAQPanel faqPanel;
    private ProblemForm problemForm;
    
    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() {
        // 设置测试数据目录
        System.setProperty("user.dir", tempDir.getAbsolutePath());
        
        // 初始化UI组件
        SwingUtilities.invokeLater(() -> {
            mainFrame = new MainFrame();
            feedbackForm = new FeedbackForm();
            faqPanel = new FAQPanel();
            problemForm = new ProblemForm();
        });
    }

    @Test
    void testMainFrameCreation() {
        assertNotNull(mainFrame);
        assertTrue(mainFrame.isVisible());
        assertEquals("Feedback System", mainFrame.getTitle());
    }

    @Test
    void testFeedbackFormCreation() {
        assertNotNull(feedbackForm);
        assertTrue(feedbackForm.isVisible());
    }

    @Test
    void testFAQPanelCreation() {
        assertNotNull(faqPanel);
        assertTrue(faqPanel.isVisible());
    }

    @Test
    void testProblemFormCreation() {
        assertNotNull(problemForm);
        assertTrue(problemForm.isVisible());
    }

    @Test
    void testConstants() {
        assertNotNull(Constants.DATA_DIR);
        assertNotNull(Constants.PRIMARY_COLOR);
        assertNotNull(Constants.ACCENT_COLOR);
    }

    @Test
    void testFileUtil() {
        // 测试文件操作
        String testContent = "Test feedback content";
        String testFile = "test_feedback.txt";
        
        // 写入文件
        boolean saved = FileUtil.saveToFile(testFile, testContent);
        assertTrue(saved, "File should be saved successfully");
        
        // 读取文件
        String content = FileUtil.readFromFile(testFile);
        
        // 验证内容
        assertTrue(content.contains(testContent), "File content should contain the test content");
    }

    @Test
    void testUIUtil() {
        // 测试UI工具类
        JButton button = UIUtil.createButton("Test", Constants.PRIMARY_COLOR);
        assertNotNull(button);
        assertEquals("Test", button.getText());
        
        JPanel panel = UIUtil.createHeaderPanel("Test Header");
        assertNotNull(panel);
    }

    @Test
    void testDataDirectoryCreation() {
        // 测试数据目录创建
        Path dataDir = Path.of(Constants.DATA_DIR);
        assertTrue(Files.exists(dataDir));
    }
} 