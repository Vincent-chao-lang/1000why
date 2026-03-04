# 应用图标和资源制作指南

## 应用图标规格

### 需要的图标尺寸

| 尺寸 | 用途 | 位置 |
|------|------|------|
| 512 x 512 | 应用商店图标 | Google Play、华为等 |
| 192 x 192 | xxxhdpi 图标 | `mipmap-xxxhdpi/` |
| 144 x 144 | xxhdpi 图标 | `mipmap-xxhdpi/` |
| 96 x 96 | xhdpi 图标 | `mipmap-xhdpi/` |
| 72 x 72 | hdpi 图标 | `mipmap-hdpi/` |
| 48 x 48 | mdpi 图标 | `mipmap-mdpi/` |
| 108 x 108 | 自适应图标前景 | `mipmap-anydpi-v26/` |

---

## 图标设计建议

### 设计元素

**推荐方案: AI + 语音主题**

```
┌─────────────────┐
│  ╭─────╮        │
│  │  🎤  │        │  中心: 麦克风图标
│  ╰─────╯        │  (象征语音交互)
│   ──── ✦        │
│                 │  背景: 深色渐变
│   HOME AI       │  (深蓝到紫色)
│                 │  底部: "HOME AI"
└─────────────────┘
```

### 颜色方案

| 元素 | 颜色 | HEX |
|------|------|-----|
| 背景 | 深蓝色 | #1a1a2e |
| 渐变 | 紫色 | #6200EE |
| 图标 | 白色 | #FFFFFF |
| 文字 | 白色/浅灰 | #CCCCCC |

---

## 快速制作图标

### 方法一: 在线工具

1. **Canva** (https://www.canva.com)
   - 搜索 "Android App Icon"
   - 自定义设计
   - 导出多尺寸

2. **Figma** (https://www.figma.com)
   - 使用 Android Icon 模板
   - 批量导出

3. **AppIconGenerator** (https://appicon.co)
   - 上传 512x512 源图
   - 自动生成所有尺寸

### 方法二: Android Studio

1. **Image Asset Studio**
   - 右键 `res` 目录 → New → Image Asset
   - 选择 Icon Type: Launcher Icons
   - 上传源图
   - 自动生成所有尺寸

### 方法三: 命令行工具

```bash
# 使用 ImageMagick 批量生成
convert icon_512.png -resize 192x192 ic_launcher_xxxhdpi.png
convert icon_512.png -resize 144x144 ic_launcher_xxhdpi.png
convert icon_512.png -resize 96x96 ic_launcher_xhdpi.png
convert icon_512.png -resize 72x72 ic_launcher_hdpi.png
convert icon_512.png -resize 48x48 ic_launcher_mdpi.png
```

---

## 当前图标状态

### 已有图标

```
✅ 自适应图标 (mipmap-anydpi-v26/)
   - ic_launcher.xml
   - ic_launcher_round.xml
   - ic_launcher_foreground.xml
```

### 需要补充

```
❌ 传统尺寸图标
   - mdpi (48x48)
   - hdpi (72x72)
   - xhdpi (96x96)
   - xxhdpi (144x144)
   - xxxhdpi (192x192)
```

---

## 宣传图设计

### Feature Graphic (1024 x 500)

**用途**: Google Play 商店顶部横幅

**布局建议**:

```
┌──────────────────────────────────────────────────────┐
│                                                      │
│  [LOGO]     让手机回归 AI                             │
│             开机即对话，语音即操作                     │
│                                                      │
│  ┌─────────────┐    ┌─────────────┐                  │
│  │  主界面     │    │  豆包语音   │                  │
│  │  [图示]     │    │  [图示]     │                  │
│  └─────────────┘    └─────────────┘                  │
│                                                      │
│  ┌─────────────┐    ┌─────────────┐                  │
│  │  一键锁定   │    │  音量解锁   │                  │
│  │  [图示]     │    │  [图示]     │                  │
│  └─────────────┘    └─────────────┘                  │
│                                                      │
└──────────────────────────────────────────────────────┘
```

**颜色**:
- 背景: 深蓝色渐变 (#1a1a2e → #6200EE)
- 文字: 白色
- 分隔线: 半透明白色

---

## 其他资源需求

### 启动画面 (可选)

```
规格: 1080 x 1920 或 1280 x 1920
内容: 应用 Logo + 简短标语
动画: 可选（中心放大或渐入）
```

### 通知图标 (可选)

```
规格: 24 x 24 dp (白色透明)
用途: 系统通知栏显示
```

### 横幅图标 (可选)

```
规格: 各种宽度的版本
用途: Android TV 或可穿戴设备
```

---

## 设计资源下载

### 免费图标库

| 网站 | 网址 | 说明 |
|------|------|------|
| Material Icons | https://fonts.google.com/icons | Google 官方 |
| Flaticon | https://www.flaticon.com | 丰富图标 |
| Icons8 | https://icons8.com | 风格多样 |
| Noun Project | https://thenounproject.com | 极简风格 |

### 麦克风图标搜索关键词

```
- microphone
- voice assistant
- ai voice
- speech
- audio
```

---

## 快速开始

### 最小化方案 (5分钟)

如果时间紧张，可以先用在线工具快速生成：

1. 访问 https://www.figma.com
2. 使用 "App Icon" 模板
3. 添加一个简单的麦克风图标
4. 设置深色背景
5. 导出 PNG 格式
6. 使用 https://appicon.co 生成所有尺寸

---

## 添加到项目

生成图标后，放置到对应目录：

```bash
# 复制图标到项目
cp ic_launcher_mdpi.png app/src/main/res/mipmap-mdpi/ic_launcher.png
cp ic_launcher_hdpi.png app/src/main/res/mipmap-hdpi/ic_launcher.png
cp ic_launcher_xhdpi.png app/src/main/res/mipmap-xhdpi/ic_launcher.png
cp ic_launcher_xxhdpi.png app/src/main/res/mipmap-xxhdpi/ic_launcher.png
cp ic_launcher_xxxhdpi.png app/src/main/res/mipmap-xxxhdpi/ic_launcher.png

# 同时复制 round 版本
cp ic_launcher_mdpi.png app/src/main/res/mipmap-mdpi/ic_launcher_round.png
# ... 重复其他尺寸
```

---

## 质量检查清单

图标完成后检查:

- [ ] 所有尺寸都已生成
- [ ] 在深色和浅色背景下都清晰
- [ ] 缩小到 48x48 仍然可识别
- [ ] 无锯齿或模糊
- [ ] 符合品牌风格
- [ ] 文件大小合理 (< 500KB)

---

**准备好资源后，放在项目的 `assets/` 目录下！**
