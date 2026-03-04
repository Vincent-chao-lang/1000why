# 应用图标资源

## 已创建的图标

### SVG 源文件

| 文件 | 尺寸 | 用途 |
|------|------|------|
| `icon_launcher.svg` | 512x512 | 应用图标（可缩放）|
| `feature_graphic.svg` | 1024x500 | 宣传横幅（可缩放）|

## 预览

### 应用图标 (icon_launcher.svg)

```
┌─────────────────────┐
│   ╔═════════╗       │
│   ║   🎤     ║       │  深蓝色渐变背景
│   ║  麦克风   ║       │  白色麦克风图标
│   ╚═════════╝ ✦     │  AI 光环效果
│                     │
│      HOME           │
│       AI            │
└─────────────────────┘
```

### 宣传图 (feature_graphic.svg)

```
┌──────────────────────────────────────────────┐
│  [图标]  Home Launcher   功能1  功能2        │
│          让手机回归 AI     功能3  功能4        │
│                                              │
│  开机即对话 · 语音即操作 · 让 AI 成为中心    │
└──────────────────────────────────────────────┘
```

## 转换为 PNG

### 方法一：使用转换脚本（推荐）

```bash
# 进入项目目录
cd /Users/qiupengchao/Downloads/1000why

# 运行转换脚本
./assets/convert_icons.sh
```

**脚本功能**:
- ✅ 自动生成所有尺寸的 PNG 图标
- ✅ 复制到 Android 项目目录
- ✅ 生成自适应图标和宣传图

### 方法二：手动转换

#### macOS / Linux

```bash
# 安装 ImageMagick (如果没有)
brew install imagemagick

# 转换为 512x512
convert assets/icon_launcher.svg -resize 512x512 icon_512.png

# 转换为其他尺寸
convert assets/icon_launcher.svg -resize 192x192 icon_192.png
convert assets/icon_launcher.svg -resize 144x144 icon_144.png
convert assets/icon_launcher.svg -resize 96x96 icon_96.png
```

#### 在线转换

访问以下网站，上传 SVG 文件，下载 PNG：

1. https://cloudconvert.com/svg-to-png
2. https://convertio.co/svg-png/
3. https://www.aconvert.com/image/svg-to-png/

#### Figma / Canva

1. 打开 Figma 或 Canva
2. 导入 `icon_launcher.svg`
3. 导出为 PNG，选择需要的尺寸

## 生成的文件

### 应用图标

运行转换脚本后，会生成：

| 尺寸 | 文件名 | 用途 |
|------|--------|------|
| 512x512 | `ic_launcher_512.png` | 应用商店图标 |
| 192x192 | `ic_launcher_192.png` | xxxhdpi (超高清)|
| 144x144 | `ic_launcher_144.png` | xxhdpi (超高)|
| 96x96 | `ic_launcher_96.png` | xhdpi (高清)|
| 72x72 | `ic_launcher_72.png` | hdpi (高)|
| 48x48 | `ic_launcher_48.png` | mdpi (中)|

### 其他资源

| 文件 | 尺寸 | 用途 |
|------|------|------|
| `ic_launcher_foreground.png` | 108x108 | 自适应图标前景 |
| `feature_graphic.png` | 1024x500 | 应用商店宣传图 |

## 输出目录

```
icons_output/
├── ic_launcher_512.png
├── ic_launcher_192.png
├── ic_launcher_144.png
├── ic_launcher_96.png
├── ic_launcher_72.png
├── ic_launcher_48.png
├── ic_launcher_foreground.png
└── feature_graphic.png

app/src/main/res/
├── mipmap-mdpi/
│   ├── ic_launcher.png (48x48)
│   └── ic_launcher_round.png
├── mipmap-hdpi/
│   ├── ic_launcher.png (72x72)
│   └── ic_launcher_round.png
├── mipmap-xhdpi/
│   ├── ic_launcher.png (96x96)
│   └── ic_launcher_round.png
├── mipmap-xxhdpi/
│   ├── ic_launcher.png (144x144)
│   └── ic_launcher_round.png
└── mipmap-xxxhdpi/
    ├── ic_launcher.png (192x192)
    └── ic_launcher_round.png
```

## 快速开始

### 一键生成所有图标

```bash
./assets/convert_icons.sh
```

### 提交到 Git

```bash
git add app/src/main/res/mipmap-*/
git add icons_output/
git commit -m "Add app icons in all sizes"
git push
```

### 构建新 APK

图标更新后，GitHub Actions 会自动构建新的 APK。

## 检查清单

生成图标后，请检查：

- [ ] 所有尺寸都已生成
- [ ] 图标在不同背景下清晰可见
- [ ] 圆角正常显示
- [ ] 文字清晰可读
- [ ] 已复制到正确的 mipmap 目录

## 故障排除

### ImageMagick 未找到

```bash
# macOS
brew install imagemagick

# Ubuntu/Debian
sudo apt-get install imagemagick

# Windows
# 下载安装: https://imagemagick.org/script/download.php
```

### SVG 转换失败

尝试使用在线转换工具：
- https://cloudconvert.com/svg-to-png
- https://convertio.co/svg-png/

### 图标显示不正确

检查 `ic_launcher_foreground.xml` 配置：
- 确保背景颜色匹配
- 检查自适应图标配置

---

**准备好后，运行 `./assets/convert_icons.sh` 生成所有图标！**
