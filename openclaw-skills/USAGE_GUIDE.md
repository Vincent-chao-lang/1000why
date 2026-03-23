# OpenClaw Skills 使用指南

## 概述

这些技能可以在 OpenClaw 中通过自然语言描述自动调用。当用户描述需求时，OpenClaw 会分析意图并选择合适的技能。

## 自然语言映射

| 用户需求描述 | 调用的技能 | 参数示例 |
|-------------|-----------|---------|
| "配置这个项目的 GitHub Actions" | setup-android-ci | `{ projectPath: '.', packageName: 'com.example.app' }` |
| "给网页添加反馈功能" | add-feedback-form | `{ htmlFilePath: 'download.html', email: 'user@example.com' }` |
| "创建一个 Android Launcher" | create-launcher-app | `{ targetPackage: 'com.larus.nova', appName: 'AI助手' }` |
| "查看当前构建状态" | monitor-github-build | `{ owner: 'Vincent-chao-lang', repo: '1000why' }` |
| "从 icon.svg 生成图标" | generate-android-icons | `{ svgPath: 'icon.svg', outputDir: './app/src/main/res' }` |
| "生成项目技术文档" | create-tech-doc | `{ projectPath: '.', projectName: 'MyApp' }` |
| "保护配置文件" | secure-sensitive-config | `{ projectPath: '.' }` |

## 代码调用示例

### 示例 1: 配置 Android CI/CD

```javascript
const { executeSkill } = require('./openclaw-skills');

// 用户说："帮我配置 Android CI/CD"
await executeSkill('setup-android-ci', {
  projectPath: '/path/to/project',
  packageName: 'com.example.myapp',
  minSdk: 26,
  targetSdk: 35,
  appName: 'MyApp'
});
// 输出: ✅ GitHub Actions CI/CD 配置完成！
```

### 示例 2: 添加反馈表单

```javascript
// 用户说："在 download.html 添加用户反馈表单"
await executeSkill('add-feedback-form', {
  htmlFilePath: '/path/to/download.html',
  email: '2819699195@qq.com'
});
// 输出: ✅ 反馈表单已添加到 /path/to/download.html
```

### 示例 3: 创建 Launcher 应用

```javascript
// 用户说："创建一个开机启动豆包的 Launcher"
await executeSkill('create-launcher-app', {
  projectPath: './AI手机助手',
  targetPackage: 'com.larus.nova',
  appName: 'AI手机助手',
  packageName: 'com.launcher.home'
});
// 输出: ✅ Android Launcher 应用创建完成！
```

### 示例 4: 监控 GitHub 构建

```javascript
// 用户说："查看当前构建状态"
await executeSkill('monitor-github-build', {
  owner: 'Vincent-chao-lang',
  repo: '1000why'
});
// 输出: ⏳ 构建进行中...
```

### 示例 5: 生成应用图标

```javascript
// 用户说："生成所有尺寸的 Android 图标"
await executeSkill('generate-android-icons', {
  svgPath: 'icon.svg',
  outputDir: './app/src/main/res'
});
// 输出: ✅ 图标生成完成！生成了 5 个图标
```

### 示例 6: 生成技术文档

```javascript
// 用户说："为项目创建技术文档"
await executeSkill('create-tech-doc', {
  projectPath: '.',
  projectName: 'AI手机助手',
  projectType: 'Android'
});
// 输出: ✅ 技术文档已生成: ./docs/TECHNICAL_DOC.md
```

### 示例 7: 保护敏感配置

```javascript
// 用户说："保护项目中的敏感配置文件"
await executeSkill('secure-sensitive-config', {
  projectPath: '.',
  sensitiveFiles: ['api-keys.properties', 'config.secrets']
});
// 输出: ✅ 敏感配置保护完成！
```

## 在 OpenClaw 中的工作流程

### 流程图

```
用户输入需求
       ↓
OpenClaw 分析意图
       ↓
匹配对应技能
       ↓
提取参数
       ↓
执行技能
       ↓
返回结果
```

### 技能匹配逻辑

OpenClaw 会根据关键词匹配技能：

1. **CI/CD 相关**: "配置 CI/CD"、"GitHub Actions"、"自动构建"、"打包部署"
2. **反馈表单**: "反馈表单"、"留言板"、"用户反馈"、"意见收集"
3. **Launcher 应用**: "Launcher"、"桌面应用"、"开机启动"、"Home 应用"
4. **构建监控**: "构建状态"、"查看构建"、"监控构建"、"GitHub 构建"
5. **图标生成**: "生成图标"、"图标转换"、"SVG 转图标"
6. **技术文档**: "技术文档"、"项目文档"、"生成文档"
7. **敏感配置**: "保护配置"、"敏感文件"、"gitignore"、"密钥保护"

## 高级用法

### 批量执行技能

```javascript
const skills = require('./openclaw-skills');

// 创建完整的项目
async function createProject() {
  await skills.executeSkill('create-launcher-app', { ... });
  await skills.executeSkill('generate-android-icons', { ... });
  await skills.executeSkill('setup-android-ci', { ... });
  await skills.executeSkill('add-feedback-form', { ... });
  await skills.executeSkill('secure-sensitive-config', { ... });
}
```

### 自定义技能

你可以创建自己的技能：

```javascript
// skills/my-custom-skill.js
module.exports = {
  name: 'my-custom-skill',
  description: '我的自定义技能',
  category: 'custom',

  async execute(context) {
    const { param1, param2 } = context;
    // 你的逻辑
    return '✅ 完成';
  }
};
```

然后在 `index.js` 中注册：

```javascript
const skills = {
  // ... 其他技能
  'my-custom-skill': require('./skills/my-custom-skill')
};
```

## 技能开发规范

每个技能模块必须导出：

- `name`: 技能名称（唯一标识）
- `description`: 技能描述（用于匹配）
- `category`: 技能分类
- `execute(context)`: 异步执行函数

```javascript
module.exports = {
  name: 'skill-name',
  description: '技能描述',
  category: 'android|web|devops|design|documentation|security|general',

  async execute(context) {
    // 从 context 中提取参数
    const { param1 = 'default' } = context;

    // 执行逻辑

    // 返回结果（字符串）
    return '✅ 完成';
  }
};
```

## 常见问题

**Q: 如何在 OpenClaw 中安装这些技能？**

A: 将 `openclaw-skills` 目录复制到 OpenClaw 的技能目录，或在 OpenClaw 的配置中引用此路径。

**Q: 技能可以链式调用吗？**

A: 可以，每个技能的输出可以作为下一个技能的输入。

**Q: 如何调试技能？**

A: 每个技能返回详细的执行结果，包括下一步建议。

**Q: 技能支持哪些参数？**

A: 每个技能定义了默认值，你可以覆盖任何参数。参考各技能文件查看完整参数列表。
