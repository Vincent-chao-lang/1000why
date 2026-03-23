---
name: setup-android-ci
description: 配置 Android 项目的 GitHub Actions 自动构建，支持 Debug/Release APK 和 AAB 格式
---

# Android CI/CD 配置助手

我会帮你配置 Android 项目的 GitHub Actions 自动构建。

## 需要的信息

1. 项目包名（如：com.example.app）
2. 最低 SDK 版本（如：26）
7. 目标 SDK 版本（如：35）
4. 应用名称（如：My App）

配置后我将：
1. 创建 `.github/workflows/build.yml` 工作流
2. 配置签名生成（在 runner 上动态生成）
3. 同时构建 APK 和 AAB 格式
4. 自动上传到 Artifacts 和 Releases

是否继续？
