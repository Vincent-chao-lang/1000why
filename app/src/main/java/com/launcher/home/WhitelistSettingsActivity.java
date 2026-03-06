package com.launcher.home;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 白名单设置 Activity - 让用户选择要显示的应用
 */
public class WhitelistSettingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppSelectorAdapter adapter;
    private List<AppSelectorItem> appList;
    private WhitelistManager whitelistManager;
    private String defaultLaunchApp; // 当前选择的默认启动应用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whitelist_settings);

        whitelistManager = new WhitelistManager(this);
        defaultLaunchApp = whitelistManager.getDefaultLaunchApp();

        initViews();
        loadApps();
        updateDefaultLaunchHint();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.apps_recycler_view);
        appList = new ArrayList<>();

        // 设置 RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppSelectorAdapter(appList, new AppSelectorAdapter.OnDefaultLaunchChangeListener() {
            @Override
            public void onDefaultLaunchChange(String packageName) {
                setDefaultLaunchApp(packageName);
            }
        });
        recyclerView.setAdapter(adapter);

        // 返回按钮
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 自动启动开关
        ToggleButton autoLaunchToggle = findViewById(R.id.auto_launch_toggle);
        autoLaunchToggle.setChecked(whitelistManager.isAutoLaunchEnabled());
        autoLaunchToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleButton toggle = (ToggleButton) v;
                whitelistManager.setAutoLaunchEnabled(toggle.isChecked());
                String status = toggle.isChecked() ? "已启用自动启动" : "已关闭自动启动";
                Toast.makeText(WhitelistSettingsActivity.this, status, Toast.LENGTH_SHORT).show();
            }
        });

        // 保存按钮
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWhitelist();
            }
        });

        // 全选按钮
        Button selectAllButton = findViewById(R.id.select_all_button);
        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAll();
            }
        });

        // 清空按钮
        Button selectNoneButton = findViewById(R.id.select_none_button);
        selectNoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNone();
            }
        });
    }

    /**
     * 设置默认启动应用
     */
    private void setDefaultLaunchApp(String packageName) {
        defaultLaunchApp = packageName;

        // 更新所有应用的默认启动状态
        for (AppSelectorItem item : appList) {
            item.setDefaultLaunch(item.getPackageName().equals(packageName));
        }

        adapter.notifyDataSetChanged();
        updateDefaultLaunchHint();
    }

    /**
     * 更新默认启动应用提示
     */
    private void updateDefaultLaunchHint() {
        TextView hintView = findViewById(R.id.default_launch_hint);
        if (hintView != null) {
            // 找到默认启动应用的名称
            String appName = "未设置";
            for (AppSelectorItem item : appList) {
                if (item.getPackageName().equals(defaultLaunchApp)) {
                    appName = item.getAppName();
                    break;
                }
            }
            hintView.setText("当前默认启动: " + appName);
        }
    }

    /**
     * 加载所有已安装的应用
     */
    private void loadApps() {
        PackageManager pm = getPackageManager();
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_MAIN, null);
        intent.addCategory(android.content.Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        Set<String> currentWhitelist = whitelistManager.getWhitelist();

        for (ResolveInfo resolveInfo : resolveInfos) {
            String packageName = resolveInfo.activityInfo.packageName;

            // 排除自己
            if (packageName.equals(getPackageName())) {
                continue;
            }

            AppSelectorItem item = new AppSelectorItem();
            item.setPackageName(packageName);
            item.setAppName(resolveInfo.loadLabel(pm).toString());
            item.setIcon(resolveInfo.activityInfo.loadIcon(pm));
            item.setChecked(currentWhitelist.contains(packageName));
            item.setDefaultLaunch(packageName.equals(defaultLaunchApp));

            appList.add(item);
        }

        // 按应用名称排序
        Collections.sort(appList, new Comparator<AppSelectorItem>() {
            @Override
            public int compare(AppSelectorItem a, AppSelectorItem b) {
                return a.getAppName().compareToIgnoreCase(b.getAppName());
            }
        });

        adapter.notifyDataSetChanged();
    }

    /**
     * 保存白名单
     */
    private void saveWhitelist() {
        Set<String> whitelist = new HashSet<>();

        for (AppSelectorItem item : appList) {
            if (item.isChecked()) {
                whitelist.add(item.getPackageName());
            }
        }

        whitelistManager.saveWhitelist(whitelist);

        // 保存默认启动应用
        whitelistManager.setDefaultLaunchApp(defaultLaunchApp);

        // 白名单功能：如果有勾选应用就启用，否则禁用
        whitelistManager.setWhitelistEnabled(!whitelist.isEmpty());

        // 标记首次设置已完成
        whitelistManager.setFirstTimeSetupCompleted();

        String defaultAppName = "未设置";
        for (AppSelectorItem item : appList) {
            if (item.getPackageName().equals(defaultLaunchApp)) {
                defaultAppName = item.getAppName();
                break;
            }
        }

        String autoLaunchStatus = whitelistManager.isAutoLaunchEnabled() ? "开启" : "关闭";
        Toast.makeText(this, "已保存 " + whitelist.size() + " 个应用\n默认启动: " + defaultAppName + "\n自动启动: " + autoLaunchStatus, Toast.LENGTH_SHORT).show();

        // 返回主界面
        finish();
    }

    /**
     * 全选
     */
    private void selectAll() {
        for (AppSelectorItem item : appList) {
            item.setChecked(true);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 清空选择
     */
    private void selectNone() {
        for (AppSelectorItem item : appList) {
            item.setChecked(false);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 应用选择器项
     */
    public static class AppSelectorItem {
        private String packageName;
        private String appName;
        private android.graphics.drawable.Drawable icon;
        private boolean checked;
        private boolean defaultLaunch; // 是否是默认启动应用

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public android.graphics.drawable.Drawable getIcon() {
            return icon;
        }

        public void setIcon(android.graphics.drawable.Drawable icon) {
            this.icon = icon;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public boolean isDefaultLaunch() {
            return defaultLaunch;
        }

        public void setDefaultLaunch(boolean defaultLaunch) {
            this.defaultLaunch = defaultLaunch;
        }
    }
}
