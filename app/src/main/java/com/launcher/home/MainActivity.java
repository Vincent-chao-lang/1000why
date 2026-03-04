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

    // ========== AI 助手配置 ==========
    // 豆包 APP 包名（需要在设备上确认实际包名）
    private static final String AI_ASSISTANT_PACKAGE = "com.larus.nova";
    // 开机自动启动 AI 助手
    private static final boolean AUTO_LAUNCH_AI = true;
    // ===========================================

    // ========== 白名单配置：只显示这些应用 ==========
    // 留空则显示所有应用（除了自己）
    private static final String[] APP_WHITELIST = {
        // 常用应用示例（取消注释并修改为实际包名）
        // "com.tencent.mm",              // 微信
        // "com.tencent.mobileqq",        // QQ
        // "com.taobao.taobao",           // 淘宝
        // "com.sdu.didi.psnger",         // 滴滴
        // "com.ss.android.ugc.aweme",    // 抖音
        "com.android.settings",
        "com.android.chrome",
        "com.larus.nova",


    };
    // ==============================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        loadApps();

        // 开机自动启动 AI 助手
        if (AUTO_LAUNCH_AI) {
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
    }

    /**
     * 启动 AI 助手（豆包）- 直接进入语音模式
     */
    private void launchAIAssistant() {
        // 延迟500ms启动，确保Launcher完全加载
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PackageManager pm = getPackageManager();

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

                // 方式3: 普通启动豆包 APP
                Intent intent = pm.getLaunchIntentForPackage(AI_ASSISTANT_PACKAGE);
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // 尝试传递语音参数
                    intent.putExtra("enter_voice", true);
                    intent.putExtra("auto_voice", true);
                    startActivity(intent);
                } else {
                    // 豆包未安装，提示用户
                    Toast.makeText(MainActivity.this,
                        "豆包 APP 未安装，请先安装豆包",
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

        for (ResolveInfo resolveInfo : resolveInfos) {
            String packageName = resolveInfo.activityInfo.packageName;

            // 排除自己
            if (packageName.equals(getPackageName())) {
                continue;
            }

            // 白名单过滤：如果设置了白名单，只显示白名单中的应用
            if (APP_WHITELIST.length > 0) {
                boolean inWhitelist = false;
                for (String whitePkg : APP_WHITELIST) {
                    if (packageName.equals(whitePkg)) {
                        inWhitelist = true;
                        break;
                    }
                }
                if (!inWhitelist) {
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

        // 如果启用 AI 助手锁定，从任何应用返回都自动重新启动豆包
        if (AUTO_LAUNCH_AI) {
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
}
