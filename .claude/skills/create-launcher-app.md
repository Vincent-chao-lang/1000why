---
name: create-launcher-app
description: 创建一个 Android Launcher 应用，开机自动启动指定应用
---

# 创建 Android Launcher 应用

我会帮你创建一个 Android Launcher 应用，实现：

## 核心功能

1. **声明为 HOME 应用** - 可设为默认启动器
2. **开机自动启动** - 设备启动后自动打开指定应用
3. **应用锁定** - 防止用户退出指定应用
4. **解锁机制** - 音量键或触摸解锁

## 需要的信息

1. 要启动的应用包名（如：com.larus.nova 豆包）
2. 应用名称（如：AI手机助手）
3. 项目包名（如：com.launcher.home）
4. 最小 SDK 版本（默认 26）

配置后我将：
- 创建完整的 Android 项目结构
- 配置 AndroidManifest.xml
- 实现 MainActivity 核心逻辑
- 实现无障碍服务（应用锁定）
- 添加解锁功能

是否继续？
