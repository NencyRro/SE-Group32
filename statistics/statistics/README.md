# 财务分析应用

这是一个基于Java开发的财务分析应用，可以分析和可视化个人财务数据。

## 功能

- 单月分析：显示当月支出饼图及预测下月开销
- 三个月趋势：显示平均支出趋势和预算建议
- 季度分析：显示季度的消费类别分布和储蓄完成情况

## 技术栈

- Java 17
- Swing (GUI)
- JFreeChart (图表生成)
- JSON Simple (JSON解析)
- Maven (依赖管理)

## 数据格式

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

## 构建和运行

### 前提条件

- Java 17 或更高版本
- Maven

### 构建应用

```bash
mvn clean package
```

### 运行应用

```bash
java -jar target/finance-analytics-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## 使用指南

1. 启动应用后，在顶部选择分析类型：单月分析、三个月趋势或季度分析
2. 如果选择单月分析，请选择要分析的月份
3. 点击"查看数据"按钮显示相应的图表和分析结果 