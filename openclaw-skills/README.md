# OpenClaw Android Skills

这是一个为 OpenClaw 创建的 Android 开发技能集合，将项目中积累的经验封装为可复用的技能模块。

## 安装

```bash
cd openclaw-skills
npm install
```

## 快速开始

```javascript
const { executeSkill } = require('./openclaw-skills');

// 创建 Launcher 应用
await executeSkill('create-launcher-app', {
  targetPackage: 'com.larus.nova',
  appName: 'AI手机助手'
});
```

## 可用技能

| 技能名称 | 说明 | 使用示例 |
|----------|------|----------|
| setup-android-ci | 配置 Android CI/CD | "帮我配置 Android CI/CD" |
| add-feedback-form | 添加反馈表单 | "在 download.html 添加表单" |
| create-launcher-app | 创建 Launcher 应用 | "创建一个 Android Launcher" |
| monitor-github-build | 监控 GitHub 构建 | "查看当前构建状态" |
| generate-android-icons | 生成 Android 图标 | "从 icon.svg 生成图标" |
| create-tech-doc | 生成技术文档 | "创建项目技术文档" |
| secure-sensitive-config | 保护敏感配置 | "保护配置文件" |

## 在 OpenClaw 中使用

OpenClaw 会根据你的描述自动选择合适的技能并执行。

例如：
- "配置这个项目的 GitHub Actions"
- "给网页添加反馈功能"
- "生成项目文档"

技能会基于当前项目目录自动执行相应操作。

### 自然语言映射

| 用户需求描述 | 调用的技能 |
|-------------|-----------|
| "配置这个项目的 GitHub Actions" | setup-android-ci |
| "给网页添加反馈功能" | add-feedback-form |
| "创建一个 Android Launcher" | create-launcher-app |
| "查看当前构建状态" | monitor-github-build |
| "从 icon.svg 生成图标" | generate-android-icons |
| "生成项目技术文档" | create-tech-doc |
| "保护配置文件" | secure-sensitive-config |

## 文档

- [使用指南 (USAGE_GUIDE.md)](USAGE_GUIDE.md) - 详细的使用说明和代码示例
- [示例代码 (test/example-usage.js)](test/example-usage.js) - 完整的使用示例

## 技能结构

```
openclaw-skills/
├── index.js                    # 主入口，注册所有技能
├── package.json                # 项目配置
├── README.md                   # 本文件
├── USAGE_GUIDE.md              # 使用指南
├── skills/                     # 技能目录
│   ├── setup-android-ci.js     # CI/CD 配置
│   ├── add-feedback-form.js    # 反馈表单
│   ├── create-launcher-app.js  # Launcher 创建
│   ├── monitor-github-build.js # 构建监控
│   ├── generate-android-icons.js # 图标生成
│   ├── create-tech-doc.js      # 文档生成
│   └── secure-sensitive-config.js # 配置保护
└── test/                       # 测试目录
    └── example-usage.js        # 使用示例
```

## 开发

每个技能都是一个独立的 Node.js 模块，包含以下结构：

```javascript
module.exports = {
  name: 'skill-name',           // 技能名称
  description: '技能描述',       // 用于匹配
  category: 'android|web|...',  // 技能分类

  async execute(context) {
    // 从 context 中提取参数
    const { param1 = 'default' } = context;

    // 执行逻辑

    // 返回结果
    return '✅ 完成';
  }
};
```

## 运行示例

```bash
# 列出所有技能
node test/example-usage.js

# 运行特定示例
node test/example-usage.js CreateLauncher
```

## 贡献

欢迎提交 Pull Request 或建议新技能！

## 许可证

MIT
