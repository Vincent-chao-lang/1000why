#!/bin/bash

# Android Release Signing 配置脚本

echo "Home Launcher - 签名配置向导"
echo "================================"
echo ""

# 检查 keystore 文件是否存在
if [ ! -f "app/release-keystore.jks" ]; then
    echo "❌ 未找到 keystore 文件"
    echo ""
    echo "请先生成 keystore 文件："
    echo ""
    echo "keytool -genkey -v \\"
    echo "  -keystore app/release-keystore.jks \\"
    echo "  -keyalg RSA \\"
    echo "  -keysize 2048 \\"
    echo "  -validity 10000 \\"
    echo "  -alias release \\"
    echo "  -dname \"CN=Home Launcher, OU=Development, O=Your Name, L=Your City, ST=Your State, C=CN\""
    echo ""
    exit 1
fi

echo "✅ 找到 keystore 文件"
echo ""

# 检查是否已配置
if [ -f "app/keystore.properties" ]; then
    echo "⚠️  签名配置文件已存在"
    echo ""
    read -p "是否重新配置? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "取消配置"
        exit 0
    fi
    echo ""
fi

echo "请输入 keystore 信息"
echo "-------------------"
echo ""

# 输入密码
read -sp "请输入 keystore 密码: " STORE_PASSWORD
echo ""
read -sp "请确认 keystore 密码: " STORE_PASSWORD_CONFIRM
echo ""

if [ "$STORE_PASSWORD" != "$STORE_PASSWORD_CONFIRM" ]; then
    echo "❌ 两次输入的密码不一致"
    exit 1
fi

# 输入 key 别名
read -p "请输入 key 别名 [默认: release]: " KEY_ALIAS
KEY_ALIAS=${KEY_ALIAS:-release}

# 输入 key 密码
read -sp "请输入 key 密码 [与 keystore 密码相同]: " KEY_PASSWORD
KEY_PASSWORD=${KEY_PASSWORD:-$STORE_PASSWORD}
echo ""

# 创建配置文件
cat > app/keystore.properties <<EOF
RELEASE_KEYSTORE_FILE=release-keystore.jks
RELEASE_KEYSTORE_PASSWORD=$STORE_PASSWORD
RELEASE_KEY_ALIAS=$KEY_ALIAS
RELEASE_KEY_PASSWORD=$KEY_PASSWORD
EOF

echo ""
echo "✅ 签名配置已保存到 app/keystore.properties"
echo ""
echo "⚠️  重要提醒："
echo "  1. 请妥善保管密码和 keystore 文件"
echo "  2. keystore.properties 包含密码，不要提交到 Git"
echo ""

# 验证 Git 是否正确忽略
if grep -q "keystore.properties" .gitignore; then
    echo "✅ keystore.properties 已在 .gitignore 中"
else
    echo "⚠️  请确保将以下文件添加到 .gitignore:"
    echo "  - app/release-keystore.jks"
    echo "  - app/keystore.properties"
fi

echo ""
echo "================================"
echo "🎉 签名配置完成！"
echo ""
echo "下一步:"
echo "  ./gradlew assembleRelease"
echo ""
