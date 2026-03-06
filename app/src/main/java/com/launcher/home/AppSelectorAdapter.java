package com.launcher.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * 应用选择器适配器
 */
public class AppSelectorAdapter extends RecyclerView.Adapter<AppSelectorAdapter.ViewHolder> {

    private List<WhitelistSettingsActivity.AppSelectorItem> items;

    public AppSelectorAdapter(List<WhitelistSettingsActivity.AppSelectorItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app_selector, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WhitelistSettingsActivity.AppSelectorItem item = items.get(position);

        holder.icon.setImageDrawable(item.getIcon());
        holder.nameText.setText(item.getAppName());
        holder.packageText.setText(item.getPackageName());
        holder.checkbox.setChecked(item.isChecked());

        // 设置复选框监听器
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setChecked(isChecked);
            }
        });

        // 整个 item 点击时切换复选框状态
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newState = !item.isChecked();
                item.setChecked(newState);
                holder.checkbox.setChecked(newState);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView nameText;
        TextView packageText;
        CheckBox checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.app_icon);
            nameText = itemView.findViewById(R.id.app_name);
            packageText = itemView.findViewById(R.id.app_package);
            checkbox = itemView.findViewById(R.id.app_checkbox);
        }
    }
}
