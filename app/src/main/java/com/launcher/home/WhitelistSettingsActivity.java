package com.launcher.home;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
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

        // 同步无障碍服务的状态
        AccessibilityLockService.autoLaunchEnabled = whitelistManager.isAutoLaunchEnabled();

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
        autoLaunchToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                whitelistManager.setAutoLaunchEnabled(isChecked);
                // 同步更新无障碍服务的状态
                AccessibilityLockService.autoLaunchEnabled = isChecked;
                String status = isChecked ? "已启用自动启动" : "已关闭自动启动";
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

        // 密码管理按钮
        Button passwordManageButton = findViewById(R.id.password_manage_button);
        passwordManageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordManageDialog();
            }
        });

        // 无障碍服务设置按钮
        Button accessibilitySettingsButton = findViewById(R.id.accessibility_settings_button);
        accessibilitySettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAccessibilitySettings();
            }
        });
    }

    /**
     * 打开无障碍服务设置
     */
    private void openAccessibilitySettings() {
        try {
            // 打开无障碍服务设置页面
            android.content.Intent intent = new android.content.Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, "请在设置中启用\"AI手机助手\"无障碍服务", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "无法打开设置页面，请手动前往：设置 → 辅助功能 → 无障碍", Toast.LENGTH_LONG).show();
        }
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

        // 首次保存时，自动启用自动启动功能
        if (whitelistManager.isFirstTimeSetup()) {
            whitelistManager.setAutoLaunchEnabled(true);
        }

        // 标记首次设置已完成
        whitelistManager.setFirstTimeSetupCompleted();

        // 检查是否需要设置密码
        boolean autoLaunchEnabled = whitelistManager.isAutoLaunchEnabled();
        boolean hasPassword = whitelistManager.hasUnlockPassword();

        if (autoLaunchEnabled && !hasPassword) {
            // 自动启动已启用但未设置密码，提示设置密码
            showPasswordSetupDialog(whitelist, defaultLaunchApp);
        } else {
            // 不需要设置密码，直接保存并返回
            finishSave(whitelist, defaultLaunchApp);
        }
    }

    /**
     * 显示密码设置对话框
     */
    private void showPasswordSetupDialog(Set<String> whitelist, String savedDefaultLaunchApp) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置解锁密码");
        builder.setMessage("自动启动已启用，建议设置密码保护解锁功能。\n\n解锁时需要输入密码才能关闭自动启动。");

        // 设置密码输入框
        final EditText passwordInput = new EditText(this);
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordInput.setHint("请输入密码（至少4位）");
        passwordInput.setPadding(50, 30, 50, 30);
        builder.setView(passwordInput);

        builder.setPositiveButton("设置密码", (dialog, which) -> {
            String password = passwordInput.getText().toString();
            if (password.length() >= 4) {
                whitelistManager.setUnlockPassword(password);
                Toast.makeText(this, "密码已设置", Toast.LENGTH_SHORT).show();
                finishSave(whitelist, savedDefaultLaunchApp);
            } else {
                Toast.makeText(this, "密码长度至少4位", Toast.LENGTH_SHORT).show();
                finishSave(whitelist, savedDefaultLaunchApp);
            }
        });

        builder.setNegativeButton("跳过", (dialog, which) -> {
            Toast.makeText(this, "未设置密码，解锁时无需验证", Toast.LENGTH_SHORT).show();
            finishSave(whitelist, savedDefaultLaunchApp);
        });

        builder.show();
    }

    /**
     * 完成保存并显示结果
     */
    private void finishSave(Set<String> whitelist, String savedDefaultLaunchApp) {
        String defaultAppName = "未设置";
        for (AppSelectorItem item : appList) {
            if (item.getPackageName().equals(savedDefaultLaunchApp)) {
                defaultAppName = item.getAppName();
                break;
            }
        }

        String autoLaunchStatus = whitelistManager.isAutoLaunchEnabled() ? "开启" : "关闭";
        String passwordStatus = whitelistManager.hasUnlockPassword() ? "（已设置密码）" : "";
        Toast.makeText(this, "已保存 " + whitelist.size() + " 个应用\n默认启动: " + defaultAppName + "\n自动启动: " + autoLaunchStatus + passwordStatus, Toast.LENGTH_SHORT).show();

        // 返回主界面
        finish();
    }

    /**
     * 显示密码管理对话框
     */
    private void showPasswordManageDialog() {
        boolean hasPassword = whitelistManager.hasUnlockPassword();

        if (hasPassword) {
            // 已设置密码，显示选项：修改密码、移除密码
            showPasswordOptionsDialog();
        } else {
            // 未设置密码，直接显示设置密码对话框
            showSetPasswordDialog();
        }
    }

    /**
     * 显示密码选项对话框（已设置密码时）
     */
    private void showPasswordOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("密码管理");
        builder.setMessage("密码已设置，请选择操作：");

        builder.setPositiveButton("修改密码", (dialog, which) -> {
            // 先验证旧密码
            showVerifyPasswordDialog(true);
        });

        builder.setNegativeButton("移除密码", (dialog, which) -> {
            // 先验证旧密码
            showVerifyPasswordDialog(false);
        });

        builder.setNeutralButton("取消", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }

    /**
     * 显示旧密码验证对话框
     * @param isChange true=修改密码, false=移除密码
     */
    private void showVerifyPasswordDialog(final boolean isChange) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("验证密码");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setHint("请输入当前密码");
        input.setPadding(50, 30, 50, 30);
        builder.setView(input);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String password = input.getText().toString();
            if (whitelistManager.verifyUnlockPassword(password)) {
                if (isChange) {
                    showSetPasswordDialog();
                } else {
                    // 移除密码
                    whitelistManager.clearUnlockPassword();
                    Toast.makeText(this, "密码已移除", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }

    /**
     * 显示设置密码对话框
     */
    private void showSetPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置密码");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setHint("请输入密码（至少4位）");
        input.setPadding(50, 30, 50, 30);
        builder.setView(input);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String password = input.getText().toString();
            if (password.length() >= 4) {
                whitelistManager.setUnlockPassword(password);
                Toast.makeText(this, "密码已设置", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "密码长度至少4位", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
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
