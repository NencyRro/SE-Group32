# 数据架构重构说明文档

## 重构背景和问题

原系统存在以下问题：
1. 各个模块使用不同的数据源加载交易数据
2. AI Recommendations 模块使用 Transaction 模块共享数据，而 Statistics Chart、Export CSV、Transaction Records 等模块各自维护独立的数据副本
3. 当交易数据变更时，模块间无法同步更新
4. 没有统一的数据管理接口

## 重构方案

创建了一个中央数据类 `TransactionDataCenter`，实现以下功能：
1. 作为单例提供统一的数据访问点
2. 管理所有交易数据的读取和修改操作
3. 提供观察者模式支持监听数据变化
4. 与原有的 TransactionManager 配合使用，确保数据持久化

## 架构变更

### 新增类
- `TransactionDataCenter`: 中央数据管理类
  - 提供了单例访问模式
  - 包含交易数据的完整操作API
  - 实现了观察者模式用于数据变更通知

### 修改的类
1. `TransactionManager`: 
   - 添加了对 `TransactionDataCenter` 的初始化支持
   - 保持了原有API以确保兼容性

2. `ChartGenerator`: 
   - 添加了 `refreshDataFromDataCenter()` 方法从中央数据中心获取数据
   - 增强了与数据中心的集成

3. `TransactionDashboardPanel`: (UI层)
   - 修改了 `openChartPanel()` 方法使用数据中心
   - 修改了 `openReportUI()` 方法使用数据中心

4. `FinanceAppGUI`: (统计图表UI)
   - 修改为从数据中心获取数据而非本地文件
   - 增强了用户界面，添加了更多分析类型

5. `TransactionList`: (交易列表UI)
   - 修改为使用数据中心而非直接访问TransactionManager
   - 实现了数据变更监听器

## 数据流

重构后的数据流：
1. 所有交易数据通过 `TransactionDataCenter` 统一管理
2. 数据持久化仍由 `TransactionManager` 处理
3. 数据变更时，`TransactionDataCenter` 通知所有注册的监听器
4. 所有UI模块都从数据中心获取最新数据

```
┌───────────────┐     ┌──────────────────┐     ┌───────────────┐
│  Transaction  │────►│ TransactionData  │◄────│ Statistics    │
│  UI模块       │     │ Center (单例)    │     │ Chart模块     │
└───────────────┘     └──────────────────┘     └───────────────┘
                              │  ▲
                              │  │
                              ▼  │
┌───────────────┐     ┌──────────────────┐     ┌───────────────┐
│  Report       │◄────┤ TransactionMgr   │────►│ CSV Export    │
│  模块         │     │ (持久化)         │     │ 模块          │
└───────────────┘     └──────────────────┘     └───────────────┘
                              │  ▲
                              │  │
                              ▼  │
                      ┌──────────────────┐
                      │    数据文件      │
                      │  (CSV/JSON)      │
                      └──────────────────┘
```

## 如何使用新的数据中心

### 获取数据中心实例
```java
TransactionDataCenter dataCenter = TransactionDataCenter.getInstance();
```

### 添加交易
```java
Transaction newTransaction = ...;
dataCenter.addTransaction(newTransaction);
```

### 删除交易
```java
dataCenter.deleteTransaction(transaction);
```

### 获取交易数据
```java
// 获取所有交易
List<Transaction> allTransactions = dataCenter.getAllTransactions();

// 获取特定类型交易
List<Transaction> incomeTransactions = dataCenter.getTransactionsByType(CategoryType.INCOME);
```

### 监听数据变化
```java
dataCenter.addTransactionChangeListener(new TransactionDataCenter.TransactionChangeListener() {
    @Override
    public void onTransactionDataChanged(TransactionDataCenter.ChangeType type, Transaction transaction) {
        // 处理数据变化
        if (type == TransactionDataCenter.ChangeType.ADD) {
            // 处理新增交易
        } else if (type == TransactionDataCenter.ChangeType.DELETE) {
            // 处理删除交易
        } else if (type == TransactionDataCenter.ChangeType.REFRESH) {
            // 处理数据刷新
        }
    }
});
```

## 注意事项

1. 不要直接修改 `TransactionDataCenter` 中的交易列表，始终使用提供的API
2. 所有UI模块在显示数据前应调用 `refreshDataFromDataCenter()` 或类似方法获取最新数据
3. 如需添加新功能模块，请使用数据中心作为唯一数据源

## 测试验证

重构后，以下场景的数据同步已经验证通过：

1. 添加新交易后，所有模块显示最新数据
2. 删除交易后，所有模块显示最新数据
3. 查看统计图表时显示的是最新交易数据
4. 导出报告时使用的是最新交易数据

## 未来扩展

未来可考虑的扩展：

1. 添加缓存机制改善性能
2. 实现分页加载大量交易数据
3. 添加搜索和过滤功能
4. 支持更复杂的数据同步机制，如多设备同步 