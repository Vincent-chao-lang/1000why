/**
 * Android CI/CD 配置技能
 * 为 Android 项目配置 GitHub Actions 自动构建
 */

const fs = require('fs-extra');
const path = require('path');

module.exports = {
  name: 'setup-android-ci',
  description: '配置 Android 项目的 GitHub Actions 自动构建，支持 Debug/Release APK 和 AAB 格式',
  category: 'android',

  /**
   * 执行技能
   * @param {object} context - 上下文
   * @param {string} context.projectPath - 项目路径
   * @param {string} context.packageName - 包名
   * @param {number} context.minSdk - 最低 SDK 版本
   * @param {number} context.targetSdk - 目标 SDK 版本
   * @param {string} context.appName - 应用名称
   */
  async execute(context) {
    const { projectPath = '.', packageName = 'com.example.app', minSdk = 26, targetSdk = 35, appName = 'My App' } = context;

    const workflowsDir = path.join(projectPath, '.github', 'workflows');
    await fs.ensureDir(workflowsDir);

    const workflowContent = `name: Build Android APK and Bundle

on:
  push:
    branches: [ main, master ]
  workflow_dispatch:

permissions:
  contents: write

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Setup Gradle
      uses: gradle/gradle-build-action@v3
      with:
        gradle-version: wrapper

    - name: Build Debug APK
      run: gradle assembleDebug --no-daemon

    - name: Generate Release Keystore
      run: |
        STOREPASS="\${{ secrets.KEYSTORE_PASSWORD }}"
        KEYPASS="\${{ secrets.KEY_PASSWORD }}"
        STOREPASS="\${{STOREPASS:-changeme}}"
        KEYPASS="\${{KEYPASS:-changeme}}"

        keytool -genkeypair \\
          -keystore app/release-keystore.jks \\
          -alias release \\
          -keyalg RSA \\
          -keysize 2048 \\
          -validity 10000 \\
          -dname "CN=${appName}" \\
          -storepass "$STOREPASS" \\
          -keypass "$KEYPASS"

    - name: Create keystore properties
      run: |
        STOREPASS="\${{ secrets.KEYSTORE_PASSWORD }}"
        KEYPASS="\${{ secrets.KEY_PASSWORD }}"
        KEYALIAS="\${{ secrets.KEY_ALIAS }}"
        STOREPASS="\${{STOREPASS:-changeme}}"
        KEYPASS="\${{KEYPASS:-changeme}}"
        KEYALIAS="\${{KEYALIAS:-release}}"

        echo "RELEASE_KEYSTORE_FILE=release-keystore.jks" > app/keystore.properties
        echo "RELEASE_KEYSTORE_PASSWORD=$STOREPASS" >> app/keystore.properties
        echo "RELEASE_KEY_ALIAS=$KEYALIAS" >> app/keystore.properties
        echo "RELEASE_KEY_PASSWORD=$KEYPASS" >> app/keystore.properties

    - name: Build Release APK
      run: gradle assembleRelease --no-daemon

    - name: Build Release Bundle (AAB)
      run: gradle bundleRelease --no-daemon

    - name: Upload Debug APK
      uses: actions/upload-artifact@v4
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk

    - name: Upload Release APK
      uses: actions/upload-artifact@v4
      with:
        name: app-release
        path: app/build/outputs/apk/release/app-release.apk

    - name: Upload Release Bundle (AAB)
      uses: actions/upload-artifact@v4
      with:
        name: app-release-bundle
        path: app/build/outputs/bundle/release/app-release.aab

    - name: Release to GitHub
      if: github.event_name == 'push' && github.ref == 'refs/heads/main'
      uses: softprops/action-gh-release@v1
      with:
        files: |
          app/build/outputs/apk/debug/app-debug.apk
          app/build/outputs/apk/release/app-release.apk
          app/build/outputs/bundle/release/app-release.aab
        tag_name: v\${{ github.run_number }}
        name: Release Build \${{ github.run_number }}
`;

    await fs.writeFile(path.join(workflowsDir, 'build.yml'), workflowContent);

    // 更新 build.gradle
    const buildGradlePath = path.join(projectPath, 'app', 'build.gradle');
    if (await fs.pathExists(buildGradlePath)) {
      let buildGradle = await fs.readFile(buildGradlePath, 'utf8');

      // 添加签名配置
      const signingConfig = `
    signingConfigs {
        release {
            def keystorePropertiesFile = rootProject.file("app/keystore.properties")
            def keystoreProperties = new Properties()
            if (keystorePropertiesFile.exists()) {
                keystoreProperties.load(new FileInputStream(keystorePropertiesFile))
            }

            storeFile file(keystoreProperties['RELEASE_KEYSTORE_FILE'])
            storePassword keystoreProperties['RELEASE_KEYSTORE_PASSWORD']
            keyAlias keystoreProperties['RELEASE_KEY_ALIAS']
            keyPassword keystoreProperties['RELEASE_KEY_PASSWORD']
        }
    }

    buildTypes {
        debug {
            signingConfig signingConfigs.debug
        }
        release {
            signingConfig signingConfigs.release
            minifyEnabled false
        }
    }

    bundle {
        language {
            enableSplit = false
        }
        density {
            enableSplit = false
        }
        abi {
            enableSplit = false
        }
    }`;

      await fs.writeFile(buildGradlePath, buildGradle + signingConfig);
    }

    return '✅ GitHub Actions CI/CD 配置完成！\n\n下一步：\n1. 提交代码到 GitHub\n2. 配置 GitHub Secrets（可选）\n3. 推送代码触发构建';
  }
};
