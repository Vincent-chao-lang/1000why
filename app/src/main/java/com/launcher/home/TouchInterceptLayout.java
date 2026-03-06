package com.launcher.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * 可拦截触摸事件的 FrameLayout
 * 用于实现连续点击解锁功能
 */
public class TouchInterceptLayout extends FrameLayout {

    private OnMultiTouchListener listener;

    public interface OnMultiTouchListener {
        void onTouch(MotionEvent event);
    }

    public TouchInterceptLayout(Context context) {
        super(context);
    }

    public TouchInterceptLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchInterceptLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnMultiTouchListener(OnMultiTouchListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // 拦截触摸事件，传递给监听器
        if (listener != null && event.getAction() == MotionEvent.ACTION_DOWN) {
            listener.onTouch(event);
        }
        // 返回 false 让子视图也能处理事件（不影响点击 app）
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 也在这里处理触摸事件
        if (listener != null && event.getAction() == MotionEvent.ACTION_DOWN) {
            listener.onTouch(event);
        }
        return super.onTouchEvent(event);
    }
}
