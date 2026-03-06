package com.launcher.home;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Home Launcher - 主Activity
 * 声明为HOME应用，用户可选择作为默认桌面
 */
public class MainActivity extends Activity {

    private GridView appGrid;
    private List<AppInfo> appList;
    private AppAdapter appAdapter;

    // 退出锁定计数器
    private int unlockPressCount = 0;
    private static final int UNLOCK_PRESS_COUNT = 3;  // 连续点击3次解锁

    // 音量键解锁
    private boolean volumeUpPressed = false;
    private boolean volumeDownPressed = false;
    private long lastVolumePressTime = 0;

    // ========== AI 助手配置 ==========
    // 豆包 APP 包名（需要在设备上确认实际包名）
    private static final String AI_ASSISTANT_PACKAGE = "com.larus.nova";
    // 开机自动启动 AI 助手
    private static final boolean AUTO_LAUNCH_AI = true;  // 启用 AI 锁定
    // ===========================================

    // 白名单管理器
    private WhitelistManager whitelistManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化白名单管理器
        whitelistManager = new WhitelistManager(this);

        initViews();
        loadApps();

        // 如果首次使用，不自动启动 AI，让用户先设置
        // 如果已完成设置，则自动启动用户选择的默认应用
        if (!whitelistManager.isFirstTimeSetup() && AUTO_LAUNCH_AI) {
            launchAIAssistant();
        }
    }

    private void initViews() {
        appGrid = findViewById(R.id.app_grid);
        appList = new ArrayList<>();
        appAdapter = new AppAdapter(this, appList);
        appGrid.setAdapter(appAdapter);

        // 点击应用启动
        appGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchApp(position);
            }
        });

        // 长按显示应用信息
        appGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showAppInfo(position);
                return true;
            }
        });

        // 设置按钮 - 打开白名单设置
        final TextView settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhitelistSettings();
            }
        });

        // 在根布局添加点击屏幕计数器用于解锁（监听整个屏幕）
        View rootLayout = findViewById(R.id.root_layout);
        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            private long lastClickTime = 0;

            @Override
            public boolean onTouch(View v, android.view.MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    // 检查触摸点是否在设置按钮区域内
                    int[] location = new int[2];
                    settingsButton.getLocationOnScreen(location);
                    float x = event.getRawX();
                    float y = event.getRawY();

                    boolean isInSettingsButton = x >= location[0] && x <= location[0] + settingsButton.getWidth()
                            && y >= location[1] && y <= location[1] + settingsButton.getHeight();

                    if (isInSettingsButton) {
                        // 点击在设置按钮上，让按钮处理事件
                        return false;
                    }

                    // 点击在其他区域，处理解锁逻辑
                    long currentTime = System.currentTimeMillis();
                    // 2秒内的连续点击才计数
                    if (currentTime - lastClickTime < 2000) {
                        unlockPressCount++;
                        if (unlockPressCount >= UNLOCK_PRESS_COUNT) {
                            unlockLauncher();
                            unlockPressCount = 0;
                        } else {
                            int remaining = UNLOCK_PRESS_COUNT - unlockPressCount;
                            Toast.makeText(MainActivity.this,
                                "再点击 " + remaining + " 次解锁",
                                Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        unlockPressCount = 1;
                        Toast.makeText(MainActivity.this,
                            "再点击 " + (UNLOCK_PRESS_COUNT - 1) + " 次解锁",
                            Toast.LENGTH_SHORT).show();
                    }
                    lastClickTime = currentTime;
                }
                return false;
            }
        });
    }

    /**
     * 解锁 Launcher - 暂时禁用 AI 自动启动
     */
    private void unlockLauncher() {
        Toast.makeText(this, "已解锁！可以正常使用桌面了", Toast.LENGTH_LONG).show();
        // 标记为已解锁，onResume 不会自动启动豆包
        unlockPressCount = -999;
    }

    /**
     * 启动 AI 助手（用户设置的默认应用）- 直接进入语音模式
     */
    private void launchAIAssistant() {
        // 获取用户设置的默认启动应用
        final String targetPackage = whitelistManager.getDefaultLaunchApp();

        // 延迟500ms启动，确保Launcher完全加载
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PackageManager pm = getPackageManager();

                // 如果是豆包，尝试使用 Deep Link 启动语音
                if (targetPackage.equals("com.larus.nova")) {
                    // 方式1: 尝试 Deep Link 启动语音（常见的 voice scheme）
                    try {
                        Intent voiceIntent = new Intent(Intent.ACTION_VIEW);
                        voiceIntent.setData(android.net.Uri.parse("doubao://voice"));
                        voiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(voiceIntent);
                        return;
                    } catch (Exception e) {
                        // Deep Link 失败，继续尝试其他方式
                    }

                    // 方式2: 尝试其他可能的 scheme
                    String[] schemes = {
                        "snssdk1128://voice",      // 抖音相关
                        "volcano://voice",          // 火山引擎
                        "bytedance://voice",        // 字节跳动
                    };

                    for (String scheme : schemes) {
                        try {
                            Intent schemeIntent = new Intent(Intent.ACTION_VIEW);
                            schemeIntent.setData(android.net.Uri.parse(scheme));
                            schemeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(schemeIntent);
                            return;
                        } catch (Exception e) {
                            // 继续尝试下一个
                        }
                    }
                }

                // 方式3: 普通启动目标应用
                Intent intent = pm.getLaunchIntentForPackage(targetPackage);
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // 如果是豆包，尝试传递语音参数
                    if (targetPackage.equals("com.larus.nova")) {
                        intent.putExtra("enter_voice", true);
                        intent.putExtra("auto_voice", true);
                    }
                    startActivity(intent);
                } else {
                    // 应用未安装，提示用户
                    Toast.makeText(MainActivity.this,
                            "默认启动应用未安装，请先安装或重新设置",
                            Toast.LENGTH_LONG).show();
                }
            }
        }, 500);
    }

    /**
     * 加载所有已安装的应用
     */
    private void loadApps() {
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);

        // 检查是否启用白名单
        boolean whitelistEnabled = whitelistManager.isWhitelistEnabled();
        java.util.Set<String> whitelist = whitelistManager.getWhitelist();

        for (ResolveInfo resolveInfo : resolveInfos) {
            String packageName = resolveInfo.activityInfo.packageName;

            // 排除自己
            if (packageName.equals(getPackageName())) {
                continue;
            }

            // 白名单过滤：如果启用了白名单，只显示白名单中的应用
            if (whitelistEnabled && !whitelist.isEmpty()) {
                if (!whitelist.contains(packageName)) {
                    continue; // 不在白名单中，跳过
                }
            }

            AppInfo appInfo = new AppInfo();
            appInfo.setTitle(resolveInfo.loadLabel(pm).toString());
            appInfo.setPackageName(packageName);
            appInfo.setIcon(resolveInfo.activityInfo.loadIcon(pm));

            appList.add(appInfo);
        }

        // 按名称排序
        Collections.sort(appList, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo a, AppInfo b) {
                return a.getTitle().compareToIgnoreCase(b.getTitle());
            }
        });

        appAdapter.notifyDataSetChanged();
    }

    /**
     * 启动选中的应用
     */
    private void launchApp(int position) {
        AppInfo appInfo = appList.get(position);
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackageName());
        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "无法启动 " + appInfo.getTitle(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 显示应用信息
     */
    private void showAppInfo(int position) {
        AppInfo appInfo = appList.get(position);
        Toast.makeText(this,
                "包名: " + appInfo.getPackageName(),
                Toast.LENGTH_LONG).show();
    }

    /**
     * 打开白名单设置界面
     */
    private void openWhitelistSettings() {
        Intent intent = new Intent(this, WhitelistSettingsActivity.class);
        startActivity(intent);
    }

    /**
     * 应用信息类
     */
    static class AppInfo {
        private String title;
        private String packageName;
        private Drawable icon;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public Drawable getIcon() {
            return icon;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }
    }

    /**
     * 应用列表适配器
     */
    static class AppAdapter extends BaseAdapter {
        private Context context;
        private List<AppInfo> apps;

        public AppAdapter(Context context, List<AppInfo> apps) {
            this.context = context;
            this.apps = apps;
        }

        @Override
        public int getCount() {
            return apps.size();
        }

        @Override
        public Object getItem(int position) {
            return apps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.app_item, parent, false);
                holder = new ViewHolder();
                holder.icon = convertView.findViewById(R.id.app_icon);
                holder.title = convertView.findViewById(R.id.app_title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            AppInfo appInfo = apps.get(position);
            holder.icon.setImageDrawable(appInfo.getIcon());
            holder.title.setText(appInfo.getTitle());

            return convertView;
        }

        static class ViewHolder {
            ImageView icon;
            TextView title;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 如果首次使用，不自动启动应用，让用户先设置
        // 如果已完成设置 且 未解锁，从任何应用返回都自动重新启动默认应用
        if (!whitelistManager.isFirstTimeSetup() && AUTO_LAUNCH_AI && unlockPressCount != -999) {
            launchAIAssistant();
            return; // 不显示 Launcher 界面
        }

        // 每次返回时重新加载应用列表
        if (appAdapter != null) {
            appList.clear();
            loadApps();
        }
    }

    @Override
    public void onBackPressed() {
        // Home应用不应该退出，按返回键不做任何事
        // 或者可以显示最近任务
        // 这里我们什么都不做
    }

    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        // 音量键解锁：同时按音量+和音量-（或快速交替按）
        if (keyCode == android.view.KeyEvent.KEYCODE_VOLUME_UP) {
            volumeUpPressed = true;
            checkVolumeUnlock();
            return true;
        }
        if (keyCode == android.view.KeyEvent.KEYCODE_VOLUME_DOWN) {
            volumeDownPressed = true;
            checkVolumeUnlock();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_VOLUME_UP) {
            volumeUpPressed = false;
            return true;
        }
        if (keyCode == android.view.KeyEvent.KEYCODE_VOLUME_DOWN) {
            volumeDownPressed = false;
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 检查音量键解锁条件
     */
    private void checkVolumeUnlock() {
        long currentTime = System.currentTimeMillis();

        // 方式1: 同时按住音量+和音量-
        if (volumeUpPressed && volumeDownPressed) {
            unlockLauncher();
            return;
        }

        // 方式2: 3秒内快速交替按音量键3次
        if (currentTime - lastVolumePressTime < 3000) {
            unlockPressCount++;
            if (unlockPressCount >= UNLOCK_PRESS_COUNT) {
                unlockLauncher();
                unlockPressCount = 0;
            } else {
                int remaining = UNLOCK_PRESS_COUNT - unlockPressCount;
                Toast.makeText(this, "再按音量键 " + remaining + " 次", Toast.LENGTH_SHORT).show();
            }
        } else {
            unlockPressCount = 1;
        }
        lastVolumePressTime = currentTime;
    }
}
