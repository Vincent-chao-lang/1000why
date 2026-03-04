package com.launcher.home;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

/**
 * 无障碍服务 - 锁定豆包APP，拦截返回键
 */
public class AccessibilityLockService extends AccessibilityService {

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
     * 拦截返回键 - 这是关键方法
     * API 16+ 支持
     */
    @Override
    protected boolean onKeyEvent(android.view.KeyEvent event) {
        // 如果是返回键且当前在豆包APP
        if (event.getKeyCode() == android.view.KeyEvent.KEYCODE_BACK &&
                isTargetAppForeground()) {
            // 消费掉返回事件，不传递给系统
            return true;  // 返回true表示事件被消费
        }

        return super.onKeyEvent(event);
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
