/**
 * 创建 Android Launcher 应用技能
 * 创建一个开机自动启动指定应用的 Launcher
 */

const fs = require('fs-extra');
const path = require('path');

module.exports = {
  name: 'create-launcher-app',
  description: '创建一个 Android Launcher 应用，开机自动启动指定应用',
  category: 'android',

  /**
   * 执行技能
   * @param {object} context - 上下文
   * @param {string} context.projectPath - 项目路径
   * @param {string} context.targetPackage - 要启动的应用包名
   * @param {string} context.appName - 应用名称
   * @param {string} context.packageName - 项目包名
   */
  async execute(context) {
    const {
      projectPath = './AI手机助手',
      targetPackage = 'com.larus.nova',
      appName = 'AI手机助手',
      packageName = 'com.launcher.home'
    } = context;

    // 创建项目结构
    const appPath = path.join(projectPath, 'app', 'src', 'main');
    await fs.ensureDir(path.join(appPath, 'java/com/launcher/home'));
    await fs.ensureDir(path.join(appPath, 'res/values'));

    // 创建 MainActivity.java
    const mainActivity = `package com.launcher.home;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.GridView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String AI_ASSISTANT_PACKAGE = "${targetPackage}";
    private static final boolean AUTO_LAUNCH_AI = true;
    private static final String[] WHITELIST_PACKAGES = {
        AI_ASSISTANT_PACKAGE
    };

    private int unlockPressCount = 0;
    private boolean volumeUpPressed = false;
    private boolean volumeDownPressed = false;
    private int touchCount = 0;
    private boolean isUnlocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createGridView());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (AUTO_LAUNCH_AI && !isUnlocked) {
            launchAIAssistant();
        }
    }

    private GridView createGridView() {
        GridView grid = new GridView(this);
        grid.setNumColumns(3);
        return grid;
    }

    private void launchAIAssistant() {
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage(AI_ASSISTANT_PACKAGE);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isUnlocked) {
            return super.onKeyDown(keyCode, event);
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            volumeUpPressed = true;
            checkVolumeUnlock();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            volumeDownPressed = true;
            checkVolumeUnlock();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            volumeUpPressed = false;
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            volumeDownPressed = false;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void checkVolumeUnlock() {
        if (volumeUpPressed && volumeDownPressed) {
            unlock();
        } else if (volumeUpPressed || volumeDownPressed) {
            unlockPressCount++;
            if (unlockPressCount >= 3) {
                unlock();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && !isUnlocked) {
            touchCount++;
            if (touchCount >= 5) {
                unlock();
            }
        }
        return super.onTouchEvent(event);
    }

    private void unlock() {
        isUnlocked = true;
        unlockPressCount = 0;
        touchCount = 0;
        Toast.makeText(this, "已解锁，可以正常使用", Toast.LENGTH_LONG).show();
    }
}
`;

    await fs.writeFile(path.join(appPath, 'java/com/launcher/home/MainActivity.java'), mainActivity);

    // 创建 strings.xml
    const stringsXml = `<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">${appName}</string>
</resources>
`;

    await fs.writeFile(path.join(appPath, 'res/values/strings.xml'), stringsXml);

    // 创建 AndroidManifest.xml
    const manifestXml = `<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="${packageName}">

    <uses-permission android:name="android.permission.BIND_ACCESSIBILITY_SERVICE" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.AppCompat.NoActionBar">

        <activity
            android:name="com.launcher.home.MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.HOME" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <service
            android:name="com.launcher.home.AccessibilityLockService"
            android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE"
            android:exported="true">
            <intent-filter>
                <action android:name="android.accessibilityservice.AccessibilityService" />
            </intent-filter>
        </service>
    </application>
</manifest>
`;

    await fs.writeFile(path.join(projectPath, 'app/src/main/AndroidManifest.xml'), manifestXml);

    return `✅ Android Launcher 应用创建完成！\n\n项目路径：${projectPath}\n包名：${packageName}\n目标应用：${targetPackage}`;
  }
};
