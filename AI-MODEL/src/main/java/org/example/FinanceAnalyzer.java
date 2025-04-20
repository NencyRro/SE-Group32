package org.example;



import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FinanceAnalyzer extends JFrame {
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private JTextArea resultArea;

    public FinanceAnalyzer() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("消费分析工具");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();

        // 分析菜单
//        JMenu analysisMenu = new JMenu("分析");
//        JMenuItem showDetailsItem = new JMenuItem("显示明细");
//        showDetailsItem.addActionListener(this::showDetails);
//        analysisMenu.add(showDetailsItem);

        // 明细菜单
        JMenu detailMenu = new JMenu("明细");
        JMenuItem viewDetailsItem = new JMenuItem("查看明细");
        viewDetailsItem.addActionListener(this::showDetails);
        detailMenu.add(viewDetailsItem);

        //menuBar.add(analysisMenu);
        menuBar.add(detailMenu);
        setJMenuBar(menuBar);

        // 主界面布局
        JPanel mainPanel = new JPanel(new BorderLayout());
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        mainPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        add(mainPanel);
    }

    private void showDetails(ActionEvent e) {
        JDialog dialog = new JDialog(this, "消费明细", true);
        dialog.setSize(600, 400);

        JPanel panel = new JPanel(new BorderLayout());

        // 表格模型
        tableModel = new DefaultTableModel();
        tableModel.addColumn("内容");
        tableModel.addColumn("支出");
        tableModel.addColumn("日期");

        dataTable = new JTable(tableModel);

        // 加载CSV数据
        loadCSVData();

        JButton analyzeBtn = new JButton("分析");
        analyzeBtn.addActionListener(this::performAnalysis);

        panel.add(new JScrollPane(dataTable), BorderLayout.CENTER);
        panel.add(analyzeBtn, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void loadCSVData() {
        try {
            // 优先从资源目录加载
            InputStream is = getClass().getResourceAsStream("/data.csv");
            if (is == null) {
                // 如果资源目录找不到，尝试从当前工作目录加载
                File file = new File("data.csv");
                if (file.exists()) {
                    is = new FileInputStream(file);
                } else {
                    throw new FileNotFoundException("data.csv 文件未找到！请检查以下位置：\n" +
                            "1. 项目根目录\n" +
                            "2. src/main/resources 目录\n" +
                            "当前工作目录：" + System.getProperty("user.dir"));
                }
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                boolean isFirstLine = true;
                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue; // 跳过标题行
                    }
                    String[] data = line.split(",");
                    if (data.length == 3) {
                        tableModel.addRow(data);
                    }
                }
            }
        } catch (IOException e) {
            String errorMsg = "读取文件失败: " + e.getMessage() + "\n" +
                    "请确保文件存在且格式正确（UTF-8编码）";
            JOptionPane.showMessageDialog(this, errorMsg, "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performAnalysis(ActionEvent e) {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "没有可分析的数据", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }

        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground()  {
                try {
                    // 构建自然语言格式的消费数据
                    StringBuilder spendingData = new StringBuilder();
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        spendingData.append(String.format(
                                "【%s】支出 %.2f元 %s",
                                tableModel.getValueAt(i, 0),
                                Double.parseDouble(tableModel.getValueAt(i, 1).toString()),
                                tableModel.getValueAt(i, 2)
                        ));
                    }

                    // 构建完整的请求内容
                    String userQuestion = "请根据以下消费记录分析并给出：" +
                            "1. 建议的月度预算" +
                            "2. 合理的储蓄目标" +
                            "3. 可削减的成本项目" +
                            "消费记录:" + spendingData;

                    // 构造符合DeepSeek API要求的JSON
                String jsonRequest = String.format(
                        "{" +
                                "\"model\": \"deepseek-chat\"," +
                                "\"messages\": [{" +
                                "   \"role\": \"user\"," +
                                "   \"content\": \"%s\"" +
                                "}]" +
                                "}",
                        userQuestion.replace("\"", "\\\"")  // 转义双引号
                );
                    // 构造符合DeepSeek API要求的JSON
                    //String jsonRequest = "{\"model\": \"deepseek-chat\",\"messages\": [{   \"role\": \"user\",   \"content\": \"请根据以下消费记录分析并给出：1. 建议的月度预算2. 合理的储蓄目标3. 可削减的成本项目消费记录:【肯德基】支出 100.00元 2025-04-01【肯德基】支出 100.00元 2025-04-01【肯德基】支出 100.00元 2025-04-01【肯德基】支出 100.00元 2025-04-01【肯德基】支出 100.00元 2025-04-01【肯德基】支出 100.00元 2025-04-01\"}]}";

                    // API配置
                    String apiUrl = "https://api.deepseek.com/v1/chat/completions";
                    String apiKey = "sk-9fc3f1a52c3143989fa3457ccf857b10";

                    URL url = new URL(apiUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Authorization", "Bearer " + apiKey);
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(150000);
                    conn.setReadTimeout(100000);

                    // 发送请求体
                    try (DataOutputStream os = new DataOutputStream(conn.getOutputStream())) {
                        os.write(jsonRequest.getBytes(StandardCharsets.UTF_8));
                    }

                    // 处理响应
                    int responseCode = conn.getResponseCode();
                    System.out.println("Response Code: " + responseCode);

                    BufferedReader br;
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                    } else {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                    }

                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();

                    // 解析返回的JSON
                    String analysisResult = parseApiResponse(response.toString());
                    System.out.println(analysisResult);
                    publish("分析结果：\n" + analysisResult);

                }  catch (Exception e) {
                    e.printStackTrace();
                }

//                    } else {
//                        System.out.println("PPPPPPPPP");
//                        BufferedReader br = new BufferedReader( new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
//                        String line;
//                        StringBuilder response = new StringBuilder();
//                        while ((line = br.readLine()) != null) {
//                            response.append(line);
//                        }
//                        // 解析返回的JSON
//                        String analysisResult = parseApiResponse(response.toString());
//                        System.out.println(analysisResult);
//                        publish("分析结果：\n" + analysisResult);
//                    }

//                } catch (Eception ex) {
//                    publish("API调用失败: " + ex.getMessage());
//                } finally {
//                    if (conn != null) conn.disconnect();
//                }
                return null;
            }

            // 新增响应解析方法
            private String parseApiResponse(String jsonResponse) {
                System.out.println("KKKKK");
                try {
                    JSONObject responseJson = new JSONObject(jsonResponse);
                    JSONArray choices = responseJson.getJSONArray("choices");
                    if (choices.length() > 0) {
                        return choices.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                    }
                    return "未收到有效响应";
                } catch (Exception e) {
                    return "响应解析失败: " + e.getMessage();
                }
            }

            // 原有process和done方法保持不变
            @Override
            protected void process(List<String> chunks) {
                chunks.forEach(resultArea::append);
            }

            @Override
            protected void done() {
                resultArea.append("\n\n分析完成");
            }
        }.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            FinanceAnalyzer app = new FinanceAnalyzer();
            app.setLocationRelativeTo(null); // 窗口居中
            app.setVisible(true);
        });
    }
}