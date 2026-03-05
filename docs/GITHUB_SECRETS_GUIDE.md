# GitHub Secrets 配置指南

## 为什么需要配置 Secrets？

为了在 GitHub Actions 中构建正式签名的 Release APK，需要将签名信息安全地存储在 GitHub Secrets 中。

## 需要配置的 Secrets

访问: https://github.com/Vincent-chao-lang/1000why/settings/secrets/actions

点击 "New repository secret" 添加以下 secrets：

| Secret 名称 | 值 | 说明 |
|-------------|-----|------|
| `KEYSTORE_FILE` | Keystore 文件的 Base64 内容 | 见下方生成方法 |
| `KEYSTORE_PASSWORD` | `changeme` | Keystore 密码 |
| `KEY_ALIAS` | `release` | Key 别名 |
| `KEY_PASSWORD` | `changeme` | Key 密码 |

---

## 生成 KEYSTORE_FILE Secret

### 方法一：使用命令行生成

在项目根目录运行：

```bash
# 生成 keystore 的 base64 编码
base64 -i app/release-keystore.jks > keystore.base64

# 复制内容
cat keystore.base64
```

复制输出的全部内容，粘贴到 `KEYSTORE_FILE` secret 的值中。

### 方法二：使用脚本

运行以下脚本自动生成并复制：

```bash
./scripts/setup-github-secrets.sh
```

---

## 配置步骤

1. **访问 Secrets 页面**
   - 访问: https://github.com/Vincent-chao-lang/1000why/settings/secrets/actions

2. **添加 KESTORE_FILE**
   - 点击 "New repository secret"
   - Name: `KEYSTORE_FILE`
   - Value: （粘贴 base64 内容）
   - 点击 "Add secret"

3. **添加其他 secrets**
   - `KEYSTORE_PASSWORD`: `changeme`
   - `KEY_ALIAS`: `release`
   - `KEY_PASSWORD`: `changeme`

4. **验证配置**
   - 确保添加了 4 个 secrets
   - 名称完全匹配（区分大小写）

---

## 安全提示

### ⚠️ 密码安全

默认密码是 `changeme`，生产环境建议修改。

### 修改密码步骤

1. 重新生成 keystore（使用新密码）
2. 更新 `app/keystore.properties`
3. 更新 GitHub Secrets 中的密码

### 密码强度建议

- 至少 12 个字符
- 包含大小写字母、数字、特殊字符
- 不要使用常见单词

---

## 构建完成后

GitHub Actions 会自动：

1. ✅ 构建 Debug APK（使用 debug 签名）
2. ✅ 构建 Release APK（使用正式签名）
3. ✅ 上传到 Artifacts
4. ✅ 发布到 Releases

## 验证签名

下载 Release APK 后，验证签名：

```bash
keytool -printcert -jarfile app-release.apk
```

应该看到：
- 发布者: CN=Home Launcher
- 有效期: 10000 天

---

**配置完成后，每次推送代码都会自动构建 Debug 和 Release 两个版本！**
