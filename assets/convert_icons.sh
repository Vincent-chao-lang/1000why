#!/bin/bash

# SVG 转 PNG 图标转换脚本
# 需要安装 librsvg: brew install librsvg (macOS)

echo "Home Launcher - 图标转换脚本"
echo "================================"

# 检查 rsvg-convert 是否安装
if ! command -v rsvg-convert &> /dev/null; then
    echo "❌ 错误: 未找到 rsvg-convert"
    echo ""
    echo "请先安装 librsvg:"
    echo "  macOS: brew install librsvg"
    echo "  Ubuntu: sudo apt-get install librsvg2-bin"
    exit 1
fi

# 创建输出目录
mkdir -p icons_output

echo "📁 创建输出目录: icons_output"
echo ""

# 应用图标尺寸
SIZES=(512 192 144 96 72 48)

echo "🎨 开始生成应用图标..."
echo ""

for size in "${SIZES[@]}"
do
    echo "  生成 ${size}x${size} 图标..."
    rsvg-convert -w ${size} -h ${size} assets/icon_launcher.svg -o icons_output/ic_launcher_${size}.png

    # 同时生成 round 版本
    cp icons_output/ic_launcher_${size}.png icons_output/ic_launcher_round_${size}.png
done

echo ""
echo "✅ 应用图标生成完成!"
echo ""

# 生成自适应图标
echo "🎨 生成自适应图标..."
rsvg-convert -w 108 -h 108 assets/icon_launcher.svg -o icons_output/ic_launcher_foreground.png
echo "  ✓ ic_launcher_foreground.png (108x108)"
echo ""

# 生成宣传图
echo "🎨 生成宣传图..."
rsvg-convert -w 1024 -h 500 assets/feature_graphic.svg -o icons_output/feature_graphic.png
echo "  ✓ feature_graphic.png (1024x500)"
echo ""

# 复制到 mipmap 目录
echo "📱 复制到 Android 项目目录..."
mkdir -p app/src/main/res/mipmap-mdpi
mkdir -p app/src/main/res/mipmap-hdpi
mkdir -p app/src/main/res/mipmap-xhdpi
mkdir -p app/src/main/res/mipmap-xxhdpi
mkdir -p app/src/main/res/mipmap-xxxhdpi

cp icons_output/ic_launcher_48.png app/src/main/res/mipmap-mdpi/ic_launcher.png
cp icons_output/ic_launcher_72.png app/src/main/res/mipmap-hdpi/ic_launcher.png
cp icons_output/ic_launcher_96.png app/src/main/res/mipmap-xhdpi/ic_launcher.png
cp icons_output/ic_launcher_144.png app/src/main/res/mipmap-xxhdpi/ic_launcher.png
cp icons_output/ic_launcher_192.png app/src/main/res/mipmap-xxxhdpi/ic_launcher.png

cp icons_output/ic_launcher_round_48.png app/src/main/res/mipmap-mdpi/ic_launcher_round.png
cp icons_output/ic_launcher_round_72.png app/src/main/res/mipmap-hdpi/ic_launcher_round.png
cp icons_output/ic_launcher_round_96.png app/src/main/res/mipmap-xhdpi/ic_launcher_round.png
cp icons_output/ic_launcher_round_144.png app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png
cp icons_output/ic_launcher_round_192.png app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png

echo "  ✓ 已复制到 mipmap 目录"
echo ""

echo "================================"
echo "🎉 全部完成!"
echo ""
echo "生成的文件位于: icons_output/"
echo "Android 图标已更新到项目目录"
echo ""
echo "下一步:"
echo "  1. 检查生成的图标"
echo "  2. 提交到 Git"
echo "  3. 构建新的 APK"
