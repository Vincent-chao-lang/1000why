package com.launcher.home;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whitelist_settings);

        whitelistManager = new WhitelistManager(this);

        initViews();
        loadApps();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.apps_recycler_view);
        appList = new ArrayList<>();

        // 设置 RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppSelectorAdapter(appList);
        recyclerView.setAdapter(adapter);

        // 返回按钮
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
     * 加载所有已安装的应用
     */
    private void loadApps() {
        PackageManager pm = getPackageManager();
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_MAIN, null);
        intent.addCategory(android.content.Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        Set<String> currentWhitelist = whitelistManager.getWhitelist();

        // 如果白名单为空（首次使用），使用默认白名单来预勾选应用
        Set<String> displayWhitelist = currentWhitelist;
        if (displayWhitelist.isEmpty()) {
            displayWhitelist = new HashSet<>();
            for (String pkg : WhitelistManager.getDefaultWhitelist()) {
                displayWhitelist.add(pkg);
            }
        }

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
            // 如果当前白名单为空，默认勾选推荐应用；否则使用实际白名单
            item.setChecked(displayWhitelist.contains(packageName));

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

        // 如果白名单不为空，启用白名单功能
        whitelistManager.setWhitelistEnabled(!whitelist.isEmpty());

        Toast.makeText(this, "已保存 " + whitelist.size() + " 个应用", Toast.LENGTH_SHORT).show();

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
    }
}
