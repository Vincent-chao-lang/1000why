package com.launcher.home;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 白名单管理器 - 负责保存和读取用户自定义的白名单
 */
public class WhitelistManager {

    private static final String PREFS_NAME = "whitelist_prefs";
    private static final String KEY_WHITELIST = "whitelist_apps";
    private static final String KEY_WHITELIST_ENABLED = "whitelist_enabled";

    private final SharedPreferences prefs;
    private final Context context;

    public WhitelistManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 获取白名单应用列表
     * 首次使用时自动初始化默认白名单
     */
    public Set<String> getWhitelist() {
        // 检查是否已经初始化过
        boolean initialized = prefs.getBoolean(KEY_WHITELIST + "_init", false);

        if (!initialized) {
            // 首次使用，设置默认白名单（不在白名单中的行为）
            Set<String> defaultList = new HashSet<>();
            for (String pkg : getDefaultWhitelist()) {
                defaultList.add(pkg);
            }
            saveWhitelist(defaultList);

            // 标记为已初始化
            prefs.edit().putBoolean(KEY_WHITELIST + "_init", true).apply();

            return defaultList;
        }

        // 已初始化，读取保存的白名单
        Set<String> whitelist = prefs.getStringSet(KEY_WHITELIST, new HashSet<>());
        return whitelist != null ? whitelist : new HashSet<>();
    }

    /**
     * 保存白名单应用列表
     */
    public void saveWhitelist(Set<String> whitelist) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(KEY_WHITELIST, whitelist);
        editor.apply();
    }

    /**
     * 添加应用到白名单
     */
    public void addToWhitelist(String packageName) {
        Set<String> whitelist = new HashSet<>(getWhitelist());
        whitelist.add(packageName);
        saveWhitelist(whitelist);
    }

    /**
     * 从白名单移除应用
     */
    public void removeFromWhitelist(String packageName) {
        Set<String> whitelist = new HashSet<>(getWhitelist());
        whitelist.remove(packageName);
        saveWhitelist(whitelist);
    }

    /**
     * 检查应用是否在白名单中
     */
    public boolean isInWhitelist(String packageName) {
        return getWhitelist().contains(packageName);
    }

    /**
     * 获取白名单是否启用
     */
    public boolean isWhitelistEnabled() {
        return prefs.getBoolean(KEY_WHITELIST_ENABLED, false);
    }

    /**
     * 设置白名单是否启用
     */
    public void setWhitelistEnabled(boolean enabled) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_WHITELIST_ENABLED, enabled);
        editor.apply();
    }

    /**
     * 清空白名单
     */
    public void clearWhitelist() {
        saveWhitelist(new HashSet<>());
    }

    /**
     * 获取默认白名单（首次使用时的推荐应用）
     */
    public static String[] getDefaultWhitelist() {
        return new String[]{
            "com.android.settings",
            "com.android.chrome",
            "com.larus.nova"  // 豆包 AI
        };
    }
}
