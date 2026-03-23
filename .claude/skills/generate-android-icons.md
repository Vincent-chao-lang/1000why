---
name: generate-android-icons
description: 从 SVG 源文件生成 Android 应用所需的所有尺寸图标
---

# 生成 Android 应用图标

我会帮你从 SVG 源文件生成 Android 应用所需的所有图标尺寸。

## 功能

1. 生成所有密度的图标（mdpi 到 xxxhdpi）
2. 生成圆角图标（ic_launcher_round）
3. 生成 Google Play 商店图标（512x512）
4. 生成 Feature Graphic（1024x500）

## 工具

使用 `rsvg-convert` 工具（librsvg）

## 使用方法

1. 确保已安装 librsvg：
   ```bash
   brew install librsvg
   ```

2. 准备好 SVG 源文件

3. 告诉我 SVG 文件路径，我会：
   - 生成所有尺寸的 PNG 图标
   - 放置到正确的目录
   - 创建批量转换脚本

是否继续？
