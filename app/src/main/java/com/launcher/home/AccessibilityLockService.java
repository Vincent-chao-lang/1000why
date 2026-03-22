package com.launcher.home;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.accessibility.AccessibilityEvent;

/**
 * 无障碍服务 - 锁定豆包APP，拦截返回键
 * 只有在自动启动启用时才拦截返回键
 */
public class AccessibilityLockService extends AccessibilityService {

    private static final String PREFS_NAME = "whitelist_prefs";
    private static final String KEY_AUTO_LAUNCH_ENABLED = "auto_launch_enabled";
    private static final String KEY_FIRST_TIME_SETUP = "first_time_setup";

    /**
     * 自动启动状态（静态变量，用于跨进程通信）
     * true = 启用自动启动，拦截返回键
     * false = 禁用自动启动，不拦截返回键
     */
    public static boolean autoLaunchEnabled = false;

    /**
     * 运行模式（静态变量，用于跨进程通信）
     * true = 默认桌面模式
     * false = Kiosk 沉浸式模式
     */
    public static boolean isDefaultLauncherMode = true;

    @Override
    public void onCreate() {
        super.onCreate();
        // 服务启动时读取自动启动状态
        reloadAutoLaunchState();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 监听应用变化，如果是豆包，确保不退出
    }

    @Override
    public void onInterrupt() {
        // 服务中断时调用
    }

    @Override
    protected boolean onGesture(int gestureId) {
        return super.onGesture(gestureId);
    }

    /**
     * 拦截系统按键 - Kiosk 模式下拦截更多按键
     * API 16+ 支持
     */
    @Override
    protected boolean onKeyEvent(android.view.KeyEvent event) {
        // Kiosk 模式（非默认桌面模式）：拦截所有系统导航按键
        if (!isDefaultLauncherMode && autoLaunchEnabled) {
            int keyCode = event.getKeyCode();

            // 拦截返回键
            if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
                return true; // 消费掉返回事件
            }

            // 拦截 Home 键（部分设备有效，取决于系统实现）
            if (keyCode == android.view.KeyEvent.KEYCODE_HOME) {
                return true;
            }

            // 拦截多任务键（部分设备有效）
            if (keyCode == android.view.KeyEvent.KEYCODE_APP_SWITCH) {
                return true;
            }

            // 拦截菜单键
            if (keyCode == android.view.KeyEvent.KEYCODE_MENU) {
                return true;
            }

            // 拦截搜索键
            if (keyCode == android.view.KeyEvent.KEYCODE_SEARCH) {
                return true;
            }

            // 拦截语音助手键
            if (keyCode == android.view.KeyEvent.KEYCODE_ASSIST) {
                return true;
            }
        }

        // 默认桌面模式：只在启用自动启动时拦截返回键
        if (isDefaultLauncherMode && autoLaunchEnabled) {
            if (event.getKeyCode() == android.view.KeyEvent.KEYCODE_BACK &&
                    isTargetAppForeground()) {
                return true;
            }
        }

        return super.onKeyEvent(event);
    }

    /**
     * 重新加载自动启动状态
     */
    private void reloadAutoLaunchState() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean firstTimeSetup = prefs.getBoolean(KEY_FIRST_TIME_SETUP, false);

        if (firstTimeSetup) {
            // 已完成首次设置，读取保存的自动启动状态
            autoLaunchEnabled = prefs.getBoolean(KEY_AUTO_LAUNCH_ENABLED, true);
        } else {
            // 首次使用未完成，不启用自动启动
            autoLaunchEnabled = false;
        }
    }

    /**
     * 检查豆包是否在前台
     */
    private boolean isTargetAppForeground() {
        try {
            android.app.ActivityManager am =
                (android.app.ActivityManager) getSystemService(android.content.Context.ACTIVITY_SERVICE);
            if (am == null) return false;

            java.util.List<android.app.ActivityManager.RunningAppProcessInfo> processes =
                am.getRunningAppProcesses();
            if (processes == null) return false;

            for (android.app.ActivityManager.RunningAppProcessInfo process : processes) {
                if (process.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    if ("com.larus.nova".equals(process.processName)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return false;
    }
}
