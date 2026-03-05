# Android Release Keystore 生成指南

## 创建正式签名 Keystore

### 方法一：使用命令行（推荐）

在有 JDK 的环境中运行以下命令：

```bash
# 进入项目目录
cd /Users/qiupengchao/Downloads/1000why

# 生成 keystore
keytool -genkey -v \
  -keystore app/release-keystore.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias release \
  -dname "CN=Home Launcher, OU=Development, O=Vincent Chao, L=Shanghai, ST=Shanghai, C=CN"
```

**系统会提示输入密码**，请设置一个强密码并记住它。

### 方法二：使用 Android Studio

1. 打开 Android Studio
2. Build → Generate Signed Bundle / APK
3. 选择 APK
4. 点击 "Create new..."
5. 填写信息：
   - Key store path: 选择保存位置
   - Password: 设置密码
   - Key alias: `release`
   - Key password: 设置密码
   - Validity: `10000`（约27年）
   - Certificate: 填写你的信息

---

## 生成的文件

| 文件 | 位置 | 说明 |
|------|------|------|
| `release-keystore.jks` | `app/` | 正式签名文件 |

---

## 安全提示

### ⚠️ 重要安全规则

1. **永远不要提交 keystore 文件到 Git**
   - `release-keystore.jks` 已在 `.gitignore` 中

2. **妥善保管密码**
   - 密码丢失后无法恢复
   - 建议使用密码管理器保存

3. **备份 keystore 文件**
   - 应用更新必须使用相同签名
   - 丢失后无法更新应用

4. **分享限制**
   - 只分享给可信的开发者
   - 不要在公开代码仓库中上传

---

## 配置签名

生成 keystore 后，运行配置脚本：

```bash
./scripts/setup-signing-config.sh
```

或手动配置 `app/keystore.properties` 文件：

```properties
RELEASE_KEYSTORE_FILE=release-keystore.jks
RELEASE_KEYSTORE_PASSWORD=your_password
RELEASE_KEY_ALIAS=release
RELEASE_KEY_PASSWORD=your_password
```

---

## 验证签名

生成 keystore 后，验证信息：

```bash
keytool -list -v -keystore app/release-keystore.jks -alias release
```

输出应显示：
- 创建日期
- 有效期（10000天）
- 证书指纹（MD5, SHA1, SHA256）

---

## 故障排除

### keytool 命令找不到

**macOS**:
```bash
# 安装 JDK
brew install openjdk@17

# 或使用 Android Studio 自带的 JDK
# /Applications/Android Studio.app/Contents/jbr/Contents/Home/bin/keytool
```

**Windows**:
```cmd
# 下载安装 JDK
https://www.oracle.com/java/technologies/downloads/

# 或使用 Android Studio 自带的 JDK
keytool 位于: C:\Program Files\Android\Android Studio\jbr\bin\
```

**Linux**:
```bash
sudo apt-get install openjdk-17-jdk
```

### 密码忘记

如果忘记 keystore 密码：
- ❌ 无法恢复
- ✅ 只能重新生成 keystore
- ⚠️ 重新签名后应用无法作为更新安装

---

## 下一步

1. 生成 keystore 文件
2. 运行 `./scripts/setup-signing-config.sh` 配置签名
3. 构建正式版本 APK

---

**准备好后，继续下一步配置！**
