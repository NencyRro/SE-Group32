# AI个人财务管理系统

本项目是一个基于Java的智能个人财务管理系统，项目包含金融应用相关功能，包括财务跟踪、反馈系统和报表生成等功能。支持手动/自动数据录入、AI分类交易、消费分析与预测等功能。采用敏捷开发方法，遵循模块化设计原则，确保代码可扩展性和可维护性。

## 核心功能模块

### 1. 数据输入与管理

- 手动输入：支持用户通过GUI界面直接录入交易记录（金额、时间、描述等）
- 自动导入：支持从银行/金融应用（如微信支付、支付宝）导入CSV/JSON格式的交易数据
- 文件存储：所有数据以文本文件（CSV/JSON/XML）存储本地，无需数据库或网络连接

### 2. AI交易分类与修正

- 智能分类：通过关键词匹配、金额模式识别和日期特征，自动分类交易（餐饮、交通、房租等）
- 人工干预：允许用户手动修正AI错误分类（如将"微信红包"从"收入"修正为"礼金"）
- 中国场景适配：识别春节红包、双十一购物等本地化消费场景

### 3. 消费分析与预测

- 可视化报告：生成月度消费趋势图、分类占比饼图等可视化图表（JSON/XML输出）
- 预算建议：基于历史消费数据，提供个性化预算方案与储蓄目标
- 异常检测：识别非常规大额支出（如单笔超过月均消费300%的交易）

### 4. 反馈系统

- 用户反馈收集：提供GUI入口，支持用户通过文本框输入提交反馈，反馈内容可自动关联用户账号信息，便于后续跟进。
- 问题报告：用户可标记问题类型（如功能故障、界面显示问题、数据错误等），上传相关截图辅助说明，系统自动生成唯一问题编号，方便用户查询进度。
- FAQ管理：以问答形式呈现常见问题，支持关键词搜索，定期根据用户反馈和热点问题更新 FAQ 库，同时提供智能推荐相关问题功能，引导用户快速找到答案。

## 技术实现要求

### 1. 项目结构

项目已按照Maven标准结构组织：

- src/main/java: 包含所有Java源代码
- src/main/resources: 包含项目资源文件
- target: 包含编译和打包后的文件
- pom.xml: Maven项目配置文件

```
SE-Group32/
├── config/                    # 配置文件
│   └── currency_config.json    # AI分类规则
├── data/                     # 数据文件
│   ├── recommendations.json  # 输入文件（CSV/JSON）
│   ├── transactions.csv       # 交易数据文件
│   └── user_profile.json      # 用户配置文件
├── resources/feedback/
│    └── feedback.txt           # 生成的消费分析报告
├── src/
│   ├── main/
│   │   ├── java/com/        # Java源代码
│   │   │       └── finance/
│   │   │           └── tracker/
│   │   │               ├── ai/                # AI相关功能模块
│   │   │               ├── classification/    # 分类模块
│   │   │               ├── feedback/          # 反馈系统模块
│   │   │               ├── integration/       # 集成模块
│   │   │               ├── localization/      # 本地化模块
│   │   │               ├── profile/           # 用户配置模块
│   │   │               ├── report/            # 报表生成模块
│   │   │               ├── statistics/        # 统计分析模块
│   │   │               └── ui/                # 图形界面模块
│   │   └── resources/       # 资源文件
│   │       ├── config/      # 配置资源
│   │       ├── feedback/    # 反馈相关资源
│   │       └── data         # 数据资源
│   └── test/java/com/         # 测试文件（单元测试代码）
├── target/                    # 构建输出目录
│   └── finance-tracker.jar      # 可执行JAR包（含第三方依赖）
└── pom.xml                  # Maven项目配置文件（管理依赖和构建规则）
```

### 2. 技术栈

- Java 17
- Swing (GUI)
- JFreeChart (图表生成)
- JSON Simple (JSON解析)
- Maven (依赖管理)

### 3. 主要依赖

- Apache HttpClient 4.5.13
- Apache HttpCore 4.4.12
- JSON库 (json-20160810.jar, json-simple-1.1.1.jar)
- JFreeChart 1.0.19 和 JCommon 1.0.23

### 4. **代码规范**

- 模块化设计：采用MVC架构，分离AI分类器、数据处理器、GUI控制器
- 可扩展性：通过接口设计支持未来新增数据源（如银行API扩展）
- 错误处理：实现交易日期验证、金额非负校验等基础约束

### 5. **测试与文档**

- 单元测试：JUnit覆盖核心分类算法（覆盖率≥80%）
- 用户手册：包含截图的操作指南（PDF格式）
- API文档：通过Javadoc自动生成

## 敏捷开发要求

### 1. 迭代管理

- 每2周一个迭代周期，产出可演示版本（v1~v4）
- 使用Scrum：每日站会、冲刺计划会议、回顾会议

### 2. GitHub协作

- 分支策略：每个成员维护独立开发分支（如`feature/ai-classifier`）
- 提交规范：原子提交+语义化消息（`feat: 新增红包检测逻辑 #12`）
- 合并流程：Pull Request需至少1人Code Review

### 3. AI辅助限制

- 数据收集：禁止使用AI生成虚拟交易数据，需采集真实用户数据样本
- 代码验证：AI生成的代码必须通过人工逻辑审查（如分类规则边界测试）
- 伦理审查：确保AI建议不包含性别/地域偏见（如"女性应减少娱乐支出"类推荐）


## 数据格式

### 1. csv导入格式

CSV 导入功能主要由 CSVImportManager 负责解析和处理

| 列号 | 字段名       | 数据类型         | 说明                                                         |
| ---- | ------------ | ---------------- | ------------------------------------------------------------ |
| 1    | ID           | 字符串           | 唯一交易标识符（如 UUID），为空时自动生成                    |
| 2    | DateTime     | 日期时间字符串   | 格式：`yyyy-MM-dd HH:mm:ss`，精确到秒                        |
| 3    | CategoryID   | 整数             | 必须为系统中已注册的类别 ID（通过 `CategoryManager` 注册）   |
| 4    | CategoryType | INCOME/EXPENSE   | 类别类型，必须为大写枚举值（`CategoryType.valueOf(...)` 匹配） |
| 5    | Amount       | 小数（最多两位） | 金额，`BigDecimal` 类型，必须 > 0                            |
| 6    | Description  | 字符串           | 可选说明，含逗号时用英文双引号包裹，内部 `"` 转义为 `""`     |

#### 示例内容

```csv
ID,DateTime,CategoryID,CategoryType,Amount,Description
abc123,2024-04-20 14:30:00,1,EXPENSE,36.50,"午餐外卖"
def456,2024-04-20 18:00:00,2,EXPENSE,88.88,"服装购物"
ghi789,2024-04-21 10:00:00,8,INCOME,5000.00,"工资发放"
```

#### 导入失败处理

- **异常捕获**：`parseTransaction()` 方法捕获异常并输出错误到控制台，不终止导入流程。
- **行级跳过**：某一行解析失败时跳过该行，不影响其他数据。
- **未知类别处理**：`CategoryID` 不存在时自动创建“未知类别”并添加到系统。

### 数据预处理逻辑

| 目标           | 说明                                                    |
| -------------- | ------------------------------------------------------- |
| 1️⃣ 去重         | 通过 ID 排除重复交易记录                                |
| 2️⃣ 格式标准化   | - 金额统一为 2 位小数<br>- 描述去除换行、空格等无效字符 |
| 3️⃣ 生成干净数据 | 处理后数据写入 `transactions.csv` 并加入系统            |


### 2. 读取数据

应用从`data/data.json`读取财务数据，格式如下：

```json
{
  "year": 2024,
  "bills": [
    {
      "month": "10月",
      "income": 5000,
      "expenses": [
        {"category": "餐饮", "amount": 800},
        ...
      ],
      "total_expenses": 4200,
      "balance": 800
    },
    ...
  ]
}
```

## 安装说明

### 克隆项目

```bash
git clone https://github.com/NencyRro/SE-Group32.git
cd SE-Group32
```

### 构建项目

```bash
mvn clean package
```

### 运行应用

```bash
java -jar target/finance-application-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### 打包项目

```json
mvn package
```

这将在target目录下生成两个jar文件：

```json
finance-application-1.0-SNAPSHOT.jar: 不包含依赖的jar包
finance-application-1.0-SNAPSHOT-jar-with-dependencies.jar: 包含所有依赖的可执行jar包
```

### 前提条件

- Java 17 或更高版本
- Maven

## 版本历史

### 1. April 13th, version 1.0：

Improve the basic panel.
The deepseek API has been successfully called in the software.
The basic functions have been basically realized, but the panel layout and function integration still need to be adjusted.

### 2. April 20, version 2.0 updated some current issues:

- Delete the region settings and only keep the currency switching function.
- Change the Chinese interface to English interface.
- Change the holiday information from fixed sample data to dynamic acquisition based on the current date.
- The transaction category is changed from Chinese to English hard-coded format, eliminating the bug that AI will reply in Chinese.
- The predefined data of AI suggestion response is changed to be based on actual holiday information and transaction data.
- Optimize the current UI interface, add dynamic effects, and delete redundant panel code files.

## 注意事项

1. 项目包含GUI界面，需要在具备图形界面的环境中运行
2. 所有依赖已配置在pom.xml中，Maven会自动下载所需依赖
3. 项目编译和打包已通过验证，所有依赖关系正确
4. 如需修改或扩展项目，请遵循Maven标准实践

## 常见问题

- **依赖下载问题**：检查 Maven 配置
- **GUI 显示异常**：确保系统支持图形界面
- **数据备份**：数据文件保存在 `data` 目录下，请定期备份

## 转换说明

原项目已完全转换为Maven格式，主要变更包括：

- 创建标准Maven目录结构
- 将lib目录中的jar依赖转换为Maven依赖
- 配置必要的Maven插件
- 确保所有功能正常工作

## 联系方式

- **项目维护者**：[维护者姓名]
- **邮箱**：[邮箱地址]
- **项目地址**：[https://github.com/NencyRro/SE-Group32](https://github.com/NencyRro/SE-Group32)

## 致谢

感谢所有为项目做出贡献的开发者！

# AI Personal Finance Management System

This project is a Java-based intelligent personal finance management system.The project contains financial application related features, including financial tracking, feedback system and report generation. It supports manual/automatic data entry, AI categorised transactions, consumption analysis and forecasting. Adopt agile development methodology and follow modular design principles to ensure code scalability and maintainability.

## Core functional modules
### 1. Data Input and Management
- Manual input: supports users to enter transaction records (amount, time, description, etc.) directly through the GUI interface
- Automatic Import: Support importing transaction data in CSV/JSON format from banks/financial applications (e.g. WeChat Pay, Alipay).
- File Storage: All data is stored locally in text files (CSV/JSON/XML), no database or network connection is required.

### 2. AI Transaction Classification and Correction
- Intelligent Classification: Automatically classify transactions (catering, transport, rent, etc.) through keyword matching, amount pattern recognition and date features
- Human intervention: Allow users to manually correct AI misclassification (e.g. correcting ‘WeChat Red Packet’ from ‘Income’ to ‘Gift’).
- Adaptation to Chinese scenarios: identify localised consumption scenarios such as Chinese New Year red packets and double eleven shopping.

### 3. Consumption Analysis and Forecasting
- Visual report: Generate visual charts such as monthly consumption trend charts and pie charts (JSON/XML output).
- Budget Recommendations: Provide personalised budget plans and savings targets based on historical consumption data.
- Anomaly Detection: Identify unconventional large expenditures (e.g. a single transaction that exceeds 300% of average monthly consumption)

### 4. Feedback System
- User Feedback Collection: Provide GUI portal to support users to submit feedback via text box input, and the feedback content can be automatically associated with the user's account information for easy follow-up.
- Problem Reporting: Users can mark the type of problems (e.g. functional failures, interface display problems, data errors, etc.), upload relevant screenshots to assist in the description, and the system automatically generates a unique problem number to facilitate users' progress enquiries.
- FAQ management: present frequently asked questions in the form of Q&A, support keyword search, update the FAQ database regularly based on user feedback and hot issues, and provide intelligent recommendation of related questions to guide users to find answers quickly.

## Technical Requirements
### 1. Project structure
The project has been organised according to the Maven standard structure:
- src/main/java: contains all Java source code.
- src/main/resources: Contains the project resource files.
- target: Contains compiled and packaged files.
- pom.xml: Maven project configuration file.

```
SE-Group32/
├── config/                    # Configuration files
│   └── currency_config.json   # AI classification rules
├── data/                      # Data files
│   ├── recommendations.json   # Input files (CSV/JSON)
│   ├── transactions.csv       # Transaction data files
│   └── user_profile.json      # User profile configuration
├── resources/feedback/
│    └── feedback.txt          # Generated expense analysis reports
├── src/
│   ├── main/
│   │   ├── java/com/          # Java source code
│   │   │       └── finance/
│   │   │           └── tracker/
│   │   │               ├── ai/                # AI-related functional modules
│   │   │               ├── classification/    # Classification module
│   │   │               ├── feedback/          # Feedback system module
│   │   │               ├── integration/       # Integration module
│   │   │               ├── localization/      # Localization module
│   │   │               ├── profile/           # User profile module
│   │   │               ├── report/            # Report generation module
│   │   │               ├── statistics/        # Statistical analysis module
│   │   │               └── ui/                # Graphical interface module
│   │   └── resources/       # Resource files
│   │       ├── config/      # Configuration resources
│   │       ├── feedback/    # Feedback-related resources
│   │       └── data         # Data resources
│   └── test/java/com/       # Test files (unit test code)
├── target/                  # Build output directory
│   └── finance-tracker.jar  # Executable JAR (with third-party dependencies)
└── pom.xml                  # Maven project configuration file (dependency and build rules)
```

### 2. Technology Stack
- Java 17
- Swing (GUI)
- JFreeChart (chart generation)
- JSON Simple (JSON parsing)
- Maven (Dependency Management)

### 3. Main Dependencies
- Apache HttpClient 4.5.13
- Apache HttpCore 4.4.12
- JSON library (json-20160810.jar, json-simple-1.1.1.jar)
- JFreeChart 1.0.19 and JCommon 1.0.23

### 4. **Code specification**
- Modular design: adopting MVC architecture, separating AI classifier, data processor and GUI controller.
- Extensibility: support future addition of new data sources (e.g. banking API extension) through interface design
- Error handling: implement basic constraints such as transaction date validation and non-negative amount checking

### 5. **Testing and Documentation***
- Unit test: JUnit covers the core classification algorithm (coverage ≥ 80%)
- User manual: operation guide with screenshots (PDF format)
- API documentation: automatically generated via Javadoc

## Agile development requirements
### 1. Iteration management
- One iteration cycle every 2 weeks, output demoable version (v1~v4)
- Use Scrum: daily standups, sprint planning meetings, retrospective meetings

### 2. GitHub Collaboration
- Branching strategy: each member maintains a separate development branch (e.g. `feature/ai-classifier`).
- Commit specification: Atomic commits + semanticised messages (`feat: add red packet detection logic #12`)
- Merge process: Pull Request requires at least 1 person Code Review

### 3. AI-assisted restrictions
- Data collection: AI is prohibited to generate virtual transaction data, and real user data samples should be collected.
- Code validation: AI-generated code must pass manual logic review (e.g., classification rule boundary test)
- Ethical review: Ensure that AI recommendations do not contain gender/geographical bias (e.g., ‘women should spend less on entertainment’ type of recommendations).


## Data format
### 1. csv import format

CSV import function is mainly parsed and processed by CSVImportManager.

| Column Number | Field Name | Data Type | Description
|------|--------------|----------------|----------------------------------------------------------------------|
| 1 | ID | String | Unique transaction identifier (e.g., UUID), auto-generated when empty |
| 2 | DateTime | DateTime string | Format: `yyyy-MM-dd HH:mm:ss`, accurate to seconds |
| 3 | CategoryID | integer | must be a registered category ID in the system (registered through `CategoryManager`) |
| 4 | CategoryType | INCOME/EXPENSE | Category type, must be an uppercase enumeration value (`CategoryType.valueOf(...) ` matches) |
| 5 | Amount | Fractional (up to two digits) | Amount, `BigDecimal` type, must be > 0 |
| 6 | Description | string | optional description, wrap in English double quotes when containing commas, internal `‘` escaped to `’"` |

#### Example contents
```csv
ID,DateTime,CategoryID,CategoryType,Amount,Description
abc123,2024-04-20 14:30:00,1,EXPENSE,36.50, ‘Lunch Takeout’
def456,2024-04-20 18:00:00,2,EXPENSE,88.88, ‘Clothing Shopping’
ghi789,2024-04-21 10:00:00,8,INCOME,5000.00, ‘Payroll’
```

#### Import failure handling
- **Exception catching**: `parseTransaction()` method catches the exception and outputs the error to the console, without terminating the import process.
- **Row level skip**: when a row fails to be parsed, the row is skipped without affecting other data.
- **Unknown Category Handling**: Automatically create ‘unknown category’ when `CategoryID` doesn't exist and add it to the system.

#### Data Preprocessing Logic
| Target | Description
|--------------|---------------------------------------------------------------------|
| De-duplication | Exclude duplicate transaction records by ID |  2️↪Meanwhile
| Format standardisation | - Amounts are standardised to 2 decimal places<br>- Description removes invalid characters such as line feeds, spaces etc |
| Generate clean data | Processed data is written to `transactions.csv` and added to the system |


### 2. Reading data
The application reads financial data from `data/data.json` in the following format:

```json
{
  "year": 2024,
  "bills": [
    {
      "month": "October",
      "income": 5000,
      "expenses": [
        {"category": "food", "amount": 800},
        ...
      ],
      "total_expenses": 4200,
      "balance": 800
    },
    ...
  ]
}
```

## Installation instructions
### Clone the project
```
git clone https://github.com/NencyRro/SE-Group32.git
cd SE-Group32
```

### Build the project
```
mvn clean package
```

### Run the application
```bash
java -jar target/finance-application-1.0-SNAPSHOT-jar-with-dependencies.jar
```
### Package the project
```json
mvn package
```

This will generate two jar files in the target directory:
```json
finance-application-1.0-SNAPSHOT.jar: does not contain dependent jar packages
finance-application-1.0-SNAPSHOT-jar-with-dependencies.jar: executable jar package with all dependencies
```

### Prerequisites

- Java 17 or later
- Maven

## Version history
### 1. April 13th, version 1.0：
Improve the basic panel.
The deepseek API has been successfully called in the software.
The basic functions have been basically realized, but the panel layout and function integration still need to be adjusted.
### 2. April 20, version 2.0 updated some current issues:
- Delete the region settings and only keep the currency switching function.
- Change the Chinese interface to English interface.
- Change the holiday information from fixed sample data to dynamic acquisition based on the current date.
- The transaction category is changed from Chinese to English hard-coded format, eliminating the bug that AI will reply in Chinese.
- The predefined data of AI suggestion response is changed to be based on actual holiday information and transaction data.
- Optimize the current UI interface, add dynamic effects, and delete redundant panel code files.

## Notes
1. The project contains a GUI interface, you need to run in an environment with a graphical interface.
2. All dependencies have been configured in pom.xml, Maven will automatically download the required dependencies.
3. the project compilation and packaging has been verified, all dependencies are correct.
4. If you need to modify or extend the project, please follow Maven's standard practices.

## Frequently Asked Questions
- Dependency download issues: Check Maven configuration
- GUI display abnormality: Make sure the system supports graphical interface.
- Data backup: Data files are stored in the `data` directory, so please back them up regularly.

## Conversion Instructions
The original project has been fully converted to Maven format, the main changes include:
- Creating a standard Maven directory structure
- Converting jar dependencies in the lib directory to Maven dependencies.
- Configuring the necessary Maven plugins
- Ensuring that all functionality is working correctly

## Contact information
- Project Maintainer: [Maintainer's name].
- Email: [email address].
- Project address: [https://github.com/NencyRro/SE-Group32](https://github.com/NencyRro/SE-Group32)

## Acknowledgements
Thanks to all the developers who contributed to the project!
