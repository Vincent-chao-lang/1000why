# AI手机助手 - 完整使用指南与最佳实践

> 版本：V2.1.70 | 更新日期：2025年

---

## 目录

1. [快速开始](#快速开始)
2. [核心功能详解](#核心功能详解)
3. [最佳实践组合](#最佳实践组合)
4. [使用场景与推荐配置](#使用场景与推荐配置)
5. [高级配置](#高级配置)
6. [常见问题解决方案](#常见问题解决方案)
7. [故障排除](#故障排除)

---

## 快速开始

### 一键安装流程

```mermaid
graph LR
    A[下载APK] --> B[安装应用]
    B --> C[设为默认桌面]
    C --> D[启用无障碍服务]
    D --> E[重启设备]
    E --> F[享受AI手机体验]
```

### 三分钟快速设置

| 步骤 | 操作 | 预计时间 |
|------|------|----------|
| 1 | 下载并安装 APK | 30秒 |
| 2 | 设置为默认桌面 | 45秒 |
| 3 | 启用无障碍服务 | 60秒 |
| 4 | 了解解锁方式 | 45秒 |
| **总计** | | **3分钟** |

---

## 核心功能详解

### 1. AI 优先启动

**工作原理：**
- 应用声明为 HOME 类型（桌面启动器）
- 开机自动调用系统启动器选择
- 自动启动豆包语音助手

**技术实现：**
```
设备唤醒 → Home Launcher → 深度链接启动 → 豆包语音界面
```

### 2. 应用锁定系统

**三级锁定机制：**

| 级别 | 机制 | 效果 | 启用方式 |
|------|------|------|----------|
| 基础级 | 开机自动启动 AI | 无法停留在桌面 | 默认启用 |
| 标准级 | 应用白名单过滤 | 只显示指定应用 | 代码配置 |
| 高级级 | 无障碍服务拦截返回键 | 无法退出 AI | 手动启用 |

### 3. 智能解锁系统

**双重解锁方式：**

```java
// 方式一：音量键组合解锁
同时按 音量+ + 音量-
或快速交替 音量+ → 音量- → 音量+

// 方式二：触摸解锁
桌面连续点击 5次
```

---

## 最佳实践组合

### 推荐组合 1：儿童学习模式

**适用场景：** 小学生专用学习手机

**配置要点：**
```java
// 白名单配置示例
private static final String[] APP_WHITELIST = {
    "com.larus.nova",                    // 豆包 AI
    "com.android.chrome",                // 浏览器（学习搜索）
    "com.ss.android.lark",               // 钉钉（网课）
    "com.tencent.weread",                // 微信读书
    "com.netease.cloudmusic",            // 网易云音乐
    "com.edmodo.classroom",              // 课堂应用
    "com.khanacademy.android"            // 可汗学院
};
```

**推荐设置：**
- ✅ 启用无障碍服务（完全锁定）
- ✅ 开机自动启动 AI
- ✅ 移除游戏类应用
- ✅ 保留学习工具

**效果：**
- 开机直达 AI 助手
- 只能使用学习相关应用
- 防止沉迷游戏和娱乐

---

### 推荐组合 2：老人关怀模式

**适用场景：** 老年人简化手机

**配置要点：**
```java
private static final String[] APP_WHITELIST = {
    "com.larus.nova",                    // 豆包 AI
    "com.tencent.mm",                    // 微信
    "com.android.phone",                 // 电话
    "com.android.messaging",             // 短信
    "com.android.camera",                // 相机
    "com.ss.android.ugc.aweme",          // 抖音（如需要）
    "ctrip.android.view"                 // 携程（出行）
};
```

**推荐设置：**
- ✅ 禁用无障碍服务（方便返回）
- ✅ 开机自动启动 AI
- ✅ 大字体显示
- ✅ 语音优先交互

**效果：**
- 简化界面，减少困扰
- 语音操作降低学习成本
- 保留核心通讯功能

---

### 推荐组合 3：专注工作模式

**适用场景：** 提升工作效率

**配置要点：**
```java
private static final String[] APP_WHITELIST = {
    "com.larus.nova",                    // 豆包 AI
    "com.android.chrome",                // 浏览器
    "com.slack",                         // Slack
    "com.microsoft.teams",               // Teams
    "com.notion.android",                // Notion
    "com.microsoft.office.outlook",      // 邮件
    "com.github.android",                // GitHub
};
```

**推荐设置：**
- ⚠️ 可选启用无障碍服务
- ✅ 定时 AI 提醒
- ✅ 移除社交媒体

**效果：**
- 减少 app 切换干扰
- AI 作为工作助手
- 保持工作专注状态

---

### 推荐组合 4：极简体验模式

**适用场景：** 追求纯净体验

**配置要点：**
```java
private static final String[] APP_WHITELIST = {
    "com.larus.nova",                    // 豆包 AI
    "com.android.settings",              // 设置（必要）
};
```

**推荐设置：**
- ✅ 启用无障碍服务
- ✅ 开机自动启动 AI
- ✅ 最小化应用列表

**效果：**
- 真正的 AI 手机体验
- 所有操作通过语音完成
- 极致的界面简化

---

## 使用场景与推荐配置

### 场景决策树

```
开始使用
    |
    ├─ 主要用户是儿童？ → 是 → [儿童学习模式]
    |                       ↓ 否
    ├─ 主要用户是老人？ → 是 → [老人关怀模式]
    |                       ↓ 否
    ├─ 需要提高工作效率？ → 是 → [专注工作模式]
    |                       ↓ 否
    └─ 想要极致简化？ → 是 → [极简体验模式]
                         ↓ 否
                         → [标准模式：显示所有应用]
```

---

## 高级配置

### 1. 自定义 AI 助手

**修改 MainActivity.java：**

```java
// 修改 AI 助手包名
private static final String AI_ASSISTANT_PACKAGE = "com.你的AI助手包名";

// 常见 AI 助手包名
"com.larus.nova"           // 豆包
"com.baidu.duer.voice"     // 小度
"com.alibaba.android.ailab" // 天猫精灵
"com.iflytek.speechassistant" // 讯飞
```

### 2. 调整解锁灵敏度

```java
// 修改点击次数
private static final int UNLOCK_PRESS_COUNT = 5;  // 默认5次

// 修改音量键时间窗口
private static final long VOLUME_KEY_TIMEOUT = 2000;  // 2秒内完成
```

### 3. 深色主题自定义

**修改 colors.xml：**

```xml
<color name="background">#1a1a2e</color>      <!-- 背景色 -->
<color name="primary">#667eea</color>         <!-- 主色调 -->
<color name="accent">#764ba2</color>          <!-- 强调色 -->
```

---

## 常见问题解决方案

### Q1: 豆包没有自动启动？

**诊断步骤：**
1. 检查豆包是否已安装
2. 检查包名是否正确（`com.larus.nova`）
3. 查看 logcat 日志

**解决方案：**
```bash
# 使用 ADB 查看日志
adb logcat | grep -i "launch"

# 手动获取豆包包名
adb shell pm list packages | grep -i "dou"
```

### Q2: 无障碍服务无法启用？

**原因：**
- 系统权限限制
- 服务声明错误

**解决方法：**
1. 设置 → 无障碍 → 找到 "AI手机助手"
2. 如果找不到，重启设备
3. 检查 AndroidManifest.xml 配置

### Q3: 如何卸载？

**完整步骤：**
```
1. 使用音量键解锁
2. 设置 → 应用管理 → AI手机助手
3. 清除默认设置
4. 返回并按 Home 键
5. 选择其他桌面
6. 再次进入应用管理 → 卸载
```

---

## 故障排除

### 问题诊断清单

| 症状 | 可能原因 | 解决方案 |
|------|----------|----------|
| 开机没有启动 AI | 未设为默认桌面 | 重新设置为默认启动器 |
| 可以退出 AI | 无障碍服务未启用 | 手动启用无障碍服务 |
| 无法解锁 | 操作太快/太慢 | 按照正确节奏操作 |
| 应用不显示 | 不在白名单中 | 修改白名单配置 |
| 安装失败 | 存储空间不足 | 清理手机存储 |

### 调试模式启用

```bash
# 启用详细日志
adb shell setprop log.tag.HomeLauncher DEBUG

# 查看实时日志
adb logcat -s HomeLauncher
```

---

## 版本信息

- **当前版本**：V2.1.70
- **最低系统要求**：Android 8.0 (API 26)
- **目标系统**：Android 14 (API 35)
- **包名**：`com.launcher.home`

---

## 技术支持

- **项目地址**：https://github.com/Vincent-chao-lang/1000why
- **问题反馈**：提交 GitHub Issue
- **用户讨论**：GitHub Discussions

---

## 附录：常用应用包名速查

### 通讯社交
```java
"com.tencent.mm"                    // 微信
"com.tencent.mobileqq"              // QQ
"com.alibaba.android.rimet"         // 钉钉
"com.ss.android.lark"               // Lark
```

### 浏览器
```java
"com.android.chrome"                // Chrome
"com.uc.browser"                    // UC浏览器
"com.oupeng.browser"                // 欧朋浏览器
```

### 音乐视频
```java
"com.netease.cloudmusic"            // 网易云音乐
"com.tencent.qqmusic"               // QQ音乐
"com.tencent.qqlive"                // 腾讯视频
"com.qiyi.video"                    // 爱奇艺
```

### 学习教育
```java
"com.zxx.client"                    // 学习通
"com.kouyuyi.kouyuyi"               // 口语AI
"com.huawei.student"                // 华为教育中心
```

---

**让 AI 成为你手机的灵魂，而非另一个应用。** 🚀
