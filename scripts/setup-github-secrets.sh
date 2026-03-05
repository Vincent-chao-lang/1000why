#!/bin/bash

# GitHub Secrets 配置助手脚本

echo "Home Launcher - GitHub Secrets 配置助手"
echo "=========================================="
echo ""

# 检查 keystore 文件
if [ ! -f "app/release-keystore.jks" ]; then
    echo "❌ 未找到 keystore 文件"
    echo "请先运行: ./scripts/setup-signing-config.sh"
    exit 1
fi

echo "✅ 找到 keystore 文件"
echo ""

# 生成 keystore base64
echo "正在生成 keystore Base64 编码..."
base64 -i app/release-keystore.jks > .keystore_base64.tmp

echo ""
echo "=========================================="
echo "GitHub Secrets 配置信息"
echo "=========================================="
echo ""
echo "请访问以下页面配置 Secrets:"
echo "https://github.com/Vincent-chao-lang/1000why/settings/secrets/actions"
echo ""
echo "-------------------------------------------"
echo "Secret 1: KEYSTORE_FILE"
echo "-------------------------------------------"
echo "将以下内容复制粘贴到 secret 值中:"
echo ""
cat .keystore_base64.tmp
echo ""
echo "-------------------------------------------"
echo "Secret 2: KEYSTORE_PASSWORD"
echo "-------------------------------------------"
echo "值: changeme"
echo ""
echo "-------------------------------------------"
echo "Secret 3: KEY_ALIAS"
echo "-------------------------------------------"
echo "值: release"
echo ""
echo "-------------------------------------------"
echo "Secret 4: KEY_PASSWORD"
echo "-------------------------------------------"
echo "值: changeme"
echo ""
echo "=========================================="
echo ""

# 清理临时文件
rm -f .keystore_base64.tmp

# 检查 keystore.properties
if [ -f "app/keystore.properties" ]; then
    echo "📋 当前配置的密码:"
    echo ""
    grep "PASSWORD" app/keystore.properties
    echo ""
fi

echo "✅ 配置完成后，推送代码将自动构建 Debug 和 Release APK"
echo ""
echo "查看构建状态:"
echo "https://github.com/Vincent-chao-lang/1000why/actions"
