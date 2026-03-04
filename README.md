# Home Launcher

一个声明为HOME应用的Android Launcher，用户安装后可选择作为默认桌面。

## 功能特性

- ✅ 声明为HOME应用，可设为默认桌面
- ✅ 显示已安装应用列表（GridView布局）
- ✅ 支持白名单过滤，只显示指定应用
- ✅ 点击启动应用
- ✅ 长按显示应用包名
- ✅ 深色主题

## 编译方法

### 方法一：使用 Android Studio

1. 用Android Studio打开项目目录
2. 等待Gradle同步完成
3. 点击 Run 按钮

### 方法二：使用命令行

```bash
./gradlew assembleDebug
```

生成的APK位于：`app/build/outputs/apk/debug/app-debug.apk`

## 配置白名单

在 `app/src/main/java/com/launcher/home/MainActivity.java` 中修改：

```java
private static final String[] APP_WHITELIST = {
    "com.tencent.mm",              // 微信
    "com.tencent.mobileqq",        // QQ
    // 添加更多包名...
};
```

- **留空** = 显示所有应用
- **填写包名** = 只显示指定应用

## 获取应用包名

长按应用图标可显示包名，或使用以下命令：

```bash
adb shell pm list packages
```

## 项目结构

```
app/src/main/
├── AndroidManifest.xml    # HOME intent声明
├── java/com/launcher/home/
│   └── MainActivity.java  # 主界面代码
└── res/
    ├── layout/            # 布局文件
    ├── values/            # 资源值
    └── mipmap-*/          # 应用图标
```
