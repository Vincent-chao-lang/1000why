/**
 * 技术文档生成技能
 * 为项目创建完整的技术文档
 */

const fs = require('fs-extra');
const path = require('path');

module.exports = {
  name: 'create-tech-doc',
  description: '为项目创建完整的技术文档，包括搭建过程、选型分析等',
  category: 'documentation',

  async execute(context) {
    const { projectPath = '.', projectName = 'AI手机助手', projectType = 'Android' } = context;

    const docContent = `# ${projectName} - 技术文档

## 项目概述

**项目名称**: ${projectName}
**项目类型**: ${projectType}
**文档生成时间**: ${new Date().toLocaleDateString('zh-CN')}

## 开发环境

- **IDE**: Android Studio / VS Code
- **语言**: Java
- **最低 SDK**: API 26 (Android 8.0)
- **目标 SDK**: API 35 (Android 15)

## 核心功能

1. 开机自动启动 AI 助手
2. 应用锁定功能
3. 解锁机制
4. 极简设计

## 技术架构

\`\`\`
MainActivity (主界面)
├── 应用列表
├── 自动启动逻辑
└── 解锁机制

AccessibilityLockService (锁定服务)
└── 返回键拦截
\`\`\`

## 构建与部署

### CI/CD

使用 GitHub Actions 自动构建：
- Debug APK
- Release APK
- Release AAB

### 签名配置

动态生成 keystore，支持 GitHub Actions。

## 问题与解决

| 问题 | 解决方案 |
|------|----------|
| Gradle Wrapper | 使用 gradle-build-action |
| 签名格式 | 在 runner 上动态生成 |
| Formspree 不可用 | 切换到 Formsubmit.co |

## 经验总结

1. **GitHub Actions 真的很强大** - 省去本地环境搭建
2. **模拟器完全够用** - BlueStacks 功能完整
3. **开源服务丰富** - Formsubmit.co 等免费工具

---

生成于: ${new Date().toLocaleString('zh-CN')}
`;

    const docsDir = path.join(projectPath, 'docs');
    await fs.ensureDir(docsDir);

    const docPath = path.join(docsDir, 'TECHNICAL_DOC.md');
    await fs.writeFile(docPath, docContent);

    return `✅ 技术文档已生成: ${docPath}`;
  }
};
