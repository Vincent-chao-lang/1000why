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
     * 拦截返回键 - 只有在自动启动启用时才拦截
     * API 16+ 支持
     */
    @Override
    protected boolean onKeyEvent(android.view.KeyEvent event) {
        // 检查是否启用自动启动
        if (!autoLaunchEnabled) {
            // 自动启动未启用，不拦截任何按键
            return super.onKeyEvent(event);
        }

        // 自动启动启用中，拦截返回键以锁定在豆包APP
        if (event.getKeyCode() == android.view.KeyEvent.KEYCODE_BACK &&
                isTargetAppForeground()) {
            // 消费掉返回事件，不传递给系统
            return true;  // 返回true表示事件被消费
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
