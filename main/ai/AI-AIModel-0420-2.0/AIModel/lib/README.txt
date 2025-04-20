此目录包含项目所需的外部库：

1. json-simple-1.1.1.jar - 用于JSON数据处理
   - 下载地址：https://code.google.com/archive/p/json-simple/downloads
   - Maven依赖：
     <dependency>
       <groupId>com.googlecode.json-simple</groupId>
       <artifactId>json-simple</artifactId>
       <version>1.1.1</version>
     </dependency>

请将上述JAR文件放置在此目录中，并在NetBeans中右键点击项目 -> Properties -> Libraries -> Add JAR/Folder 添加这些库文件。

你是一个资深的 Java 桌面应用开发专家。现在我正参与一个名为“AI-Empowered Personal Finance Tracker”的团队项目，语言限定为 Java，使用 GUI（Swing 或 JavaFX），不得联网，不使用数据库，只使用本地文件（如 JSON、CSV）进行数据存储和读取。最终所有模块需要集成到 main branch 中。

我负责的模块是【本地财务环境定制 + AI 个性化建议 + UI优化】。我需要你帮我完成如下内容：

1. 设计一套可扩展、可维护的 Java 类结构，包含：
   - 货币单位切换
   - 中国节假日识别
   - 地域性预算调整（比如春节前建议多存钱）
   - 用户画像建模（如消费偏好）
   - AI 个性化建议（如预算建议、节假日高消费提醒）
   - 建议展示与用户反馈记录
   - 与主项目的集成接口（如导入Transaction数据、输出建议结果）

2. 请基于这些需求，给出：
   - 清晰的类名、职责、字段、关键方法（接口级别设计）
   - 合理的包结构（package structure）用于与main项目集成
   - 每个模块的Java代码骨架（Skeleton Code），可以直接修改后使用
   - 建议使用 JSON 文件读写方式，支持配置加载与建议输出
   - GUI层面请使用 Swing（或 JavaFX）来创建设置页面和建议展示区域
   - 所有代码应保持解耦、注释清晰、便于和主项目合并


UI 优化请求：
请你帮我优化 Java Swing UI 面板，目标是打造一个更现代、清晰、好用的“AI 财务助手本地模块测试”界面。当前界面按钮全部堆在顶栏，弹窗杂乱、层次不清，观感落后。请参考以下优化建议，重新生成完整的 Java Swing UI 代码（可使用 FlatLaf、JPanel、CardLayout、JDialog 等）：

【界面重构建议】：
1. 顶部：保留标题“AI 财务助手”，居中加粗，美化字体。
2. 左侧导航：垂直按钮栏，图标 + 文字（如下）：
   - 生成 AI 财务建议
   - 查看近期节假日
   - 切换货币单位
   - 设置地区
   - 添加测试交易
3. 右侧主面板：CardLayout 实现功能区切换，点击左侧按钮加载对应面板内容。
4. 弹窗优化：
   - 添加交易使用 `JDialog` 表单收集输入。
   - 生成建议改为非弹窗显示（在右侧面板滚动显示）。
   - 建议展示使用 `JTextPane` 支持多行、分段、emoji图标。
5. UI 美化：
   - 使用 FlatLaf（或其他现代 Swing 主题）
   - 所有按钮加 padding、圆角边框
   - 添加底部状态栏，显示地区、货币单位、上次建议生成时间

【行为要求】：
- 所有建议项请格式化为有序列表或卡片样式，避免一整段文字
- 保留所有原有功能逻辑（可伪造调用函数，例如 generateRecommendations()）
- 提供可运行的 Java UI 类（MainPanel / MainFrame 均可）

你可以先生成 UI 主界面类（比如 MainModuleTestPanel.java），再按模块拆分 panel 类。