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