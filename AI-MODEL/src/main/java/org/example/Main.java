package org.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try {
            // 构造符合DeepSeek API要求的JSON
            String jsonRequest = "{\"model\": \"deepseek-chat\",\"messages\": [{   \"role\": \"user\",   \"content\": \"请根据以下消费记录分析并给出：1. 建议的月度预算2. 合理的储蓄目标3. 可削减的成本项目消费记录:【肯德基】支出 100.00元 2025-04-01【肯德基】支出 100.00元 2025-04-01【肯德基】支出 100.00元 2025-04-01【肯德基】支出 100.00元 2025-04-01【肯德基】支出 100.00元 2025-04-01【肯德基】支出 100.00元 2025-04-01\"}]}";

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

            // 输出响应
            System.out.println(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}