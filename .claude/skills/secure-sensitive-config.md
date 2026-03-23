---
name: secure-sensitive-config
description: 保护项目中的敏感配置信息，使用模板文件 + .gitignore 隔离
---

# 保护敏感配置信息

我会帮你保护项目中的敏感配置信息。

## 场景

你的项目包含敏感信息：
- API 密钥
- 数据库密码
- 签名文件
- 邮箱地址
- Form ID 等

## 解决方案

### 1. 创建模板文件

将敏感配置替换为占位符：

```properties
# config.properties.template
API_KEY=your_api_key_here
DATABASE_URL=your_database_url_here
SECRET=your_secret_here
```

### 2. 更新 .gitignore

```gitignore
# 配置文件（包含敏感信息）
config.properties
keystore.jks
```

### 3. 创建说明文档

指导如何配置实际文件。

## 使用方法

告诉我：
1. 哪些文件包含敏感信息
2. 这些敏感信息的类型

我会：
- 创建模板文件
- 更新 .gitignore
- 移除已跟踪的敏感文件
- 创建配置说明文档

是否继续？
