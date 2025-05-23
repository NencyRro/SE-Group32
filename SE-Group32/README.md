# SE-Group32 Maven项目使用说明

## 项目概述
本项目是将原SE-Group32项目转换为标准Maven格式的结果。项目包含金融应用相关功能，包括财务跟踪、反馈系统和报表生成等功能。

## 项目结构
项目已按照Maven标准结构组织：
- `src/main/java`: 包含所有Java源代码
- `src/main/resources`: 包含项目资源文件
- `target`: 包含编译和打包后的文件
- `pom.xml`: Maven项目配置文件

## 主要依赖
项目使用以下主要依赖：
- Apache HttpClient 4.5.13
- Apache HttpCore 4.4.12
- JSON库 (json-20160810.jar, json-simple-1.1.1.jar)
- JFreeChart 1.0.19 和 JCommon 1.0.23

## 构建与运行
### 构建项目
```bash
mvn clean compile
```

### 打包项目
```bash
mvn package
```
这将在`target`目录下生成两个jar文件：
- `finance-application-1.0-SNAPSHOT.jar`: 不包含依赖的jar包
- `finance-application-1.0-SNAPSHOT-jar-with-dependencies.jar`: 包含所有依赖的可执行jar包

### 运行项目
```bash
java -jar target/finance-application-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## 注意事项
1. 项目包含GUI界面，需要在具备图形界面的环境中运行
2. 所有依赖已配置在pom.xml中，Maven会自动下载所需依赖
3. 项目编译和打包已通过验证，所有依赖关系正确
4. 如需修改或扩展项目，请遵循Maven标准实践

## 转换说明
原项目已完全转换为Maven格式，主要变更包括：
1. 创建标准Maven目录结构
2. 将lib目录中的jar依赖转换为Maven依赖
3. 配置必要的Maven插件
4. 确保所有功能正常工作

如有任何问题，请参考项目中的文档或联系开发团队。
