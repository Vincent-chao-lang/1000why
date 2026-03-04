# Home Launcher 使用说明书

## 目录

1. [简介](#简介)
2. [安装方法](#安装方法)
3. [设置默认桌面](#设置默认桌面)
4. [功能说明](#功能说明)
5. [白名单配置](#白名单配置)
6. [常见问题](#常见问题)

---

## 简介

Home Launcher 是一款精简的 Android 桌面启动器，允许用户自定义显示哪些应用。

### 主要特性

- ✅ 声明为 HOME 应用，可设为默认桌面
- ✅ 显示已安装应用列表（网格布局）
- ✅ 支持白名单过滤，只显示指定应用
- ✅ 点击启动应用
- ✅ 长按显示应用包名
- ✅ 深色主题，保护眼睛

---

## 安装方法

### 方法一：下载 APK 安装

1. 访问项目主页：https://github.com/Vincent-chao-lang/1000why
2. 进入 **Releases** 页面
3. 下载最新的 `app-debug.apk`
4. 在手机上打开 APK 文件并安装

### 方法二：自行编译

```bash
# 克隆项目
git clone https://github.com/Vincent-chao-lang/1000why.git
cd 1000why

# 编译 APK
./gradlew assembleDebug

# APK 位置
ls app/build/outputs/apk/debug/app-debug.apk
```

---

## 设置默认桌面

### 第一次安装

1. 安装完成后，按手机的 **Home 键**
2. 系统会弹出选择器
3. 选择 **Home Launcher**
4. 勾选 **"始终"** 或 **"设为默认"**
5. 点击 **"确定"**

### 更改默认桌面

如果已经设置了其他默认桌面，需要先清除：

1. 进入 **设置** → **应用管理**
2. 找到当前的默认桌面
3. 进入 **"默认打开"** 或 **"设为默认应用"**
4. 点击 **"清除默认设置"**
5. 再次按 Home 键，选择 Home Launcher

---

## 功能说明

### 应用列表

- 所有已安装的应用以网格形式显示
- 应用按字母顺序排序
- 自己除外（避免重复显示）

### 点击启动

- **单击** 应用图标：启动该应用
- 返回时自动回到 Home Launcher

### 长按查看信息

- **长按** 应用图标：显示应用包名
- 用于配置白名单时获取包名

### 按返回键

- Home Launcher 中按返回键不会退出
- 确保始终保持在桌面上

---

## 白名单配置

### 什么是白名单

白名单允许你只显示指定的应用，隐藏其他所有应用。

### 配置步骤

1. **查看应用包名**
   - 长按应用图标获取包名
   - 或使用 ADB 命令：`adb shell pm list packages`

2. **修改源代码**
   ```java
   // 打开文件：app/src/main/java/com/launcher/home/MainActivity.java
   // 找到第 38-45 行，修改白名单配置

   private static final String[] APP_WHITELIST = {
       "com.tencent.mm",              // 微信
       "com.tencent.mobileqq",        // QQ
       "com.eg.android.AlipayGphone", // 支付宝
       // 添加更多...
   };
   ```

3. **重新编译安装**
   ```bash
   ./gradlew assembleDebug
   ```

### 白名单模式说明

| 白名单配置 | 显示效果 |
|-----------|---------|
| 留空 `[]` | 显示所有应用（推荐初次使用） |
| 有内容 | 只显示白名单中的应用 |

### 常用应用包名参考

| 应用 | 包名 |
|------|------|
| 微信 | `com.tencent.mm` |
| QQ | `com.tencent.mobileqq` |
| 支付宝 | `com.eg.android.AlipayGphone` |
| 淘宝 | `com.taobao.taobao` |
| 抖音 | `com.ss.android.ugc.aweme` |
| 快手 | `com.smile.gifmaker` |
| 网易云音乐 | `com.netease.cloudmusic` |
| 腾讯视频 | `com.tencent.qqlive` |
| 爱奇艺 | `com.qiyi.video` |
| 哔哩哔哩 | `tv.danmaku.bili` |

---

## 常见问题

### Q1: 安装后按 Home 键没有弹出选择？

**A:** 需要先清除当前默认桌面的默认设置：
- 设置 → 应用 → 当前默认桌面 → 清除默认设置

### Q2: 如何卸载 Home Launcher？

**A:** 需要先更换默认桌面，才能卸载：
1. 设置 → 应用 → Home Launcher → 清除默认设置
2. 按 Home 键选择其他桌面
3. 设置 → 应用 → Home Launcher → 卸载

### Q3: 应用列表显示不全？

**A:** 检查白名单配置，如果设置了白名单，只显示列表中的应用。

### Q4: 如何添加桌面小部件？

**A:** 当前版本专注于简洁的应用启动，暂不支持小部件功能。

### Q5: 应用图标显示不正确？

**A:** 这是应用自己的图标设计，Home Launcher 直接从系统获取。

### Q6: 如何恢复显示所有应用？

**A:** 将白名单配置为空数组：
```java
private static final String[] APP_WHITELIST = {};
```

### Q7: 支持自定义主题吗？

**A:** 当前版本使用预设的深色主题。如需修改，可编辑：
- `app/src/main/res/values/colors.xml`（颜色）
- `app/src/main/res/layout/activity_main.xml`（布局）

---

## 技术支持

- 项目地址：https://github.com/Vincent-chao-lang/1000why
- 问题反馈：提交 GitHub Issue

---

## 版本信息

- **当前版本**：1.0
- **最低系统要求**：Android 8.0 (API 26)
- **目标系统**：Android 14 (API 34)

---

## 许可证

本项目仅供学习和个人使用。
