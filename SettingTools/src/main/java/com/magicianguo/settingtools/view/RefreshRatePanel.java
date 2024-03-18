package com.magicianguo.settingtools.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.magicianguo.settingtools.util.PackageUtils;

public class RefreshRatePanel extends LinearLayout {
    private boolean mIntercept = false;

    public RefreshRatePanel(Context context) {
        super(context);
    }

    public RefreshRatePanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshRatePanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mIntercept = !PackageUtils.isPluginInstalled();
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean isIntercept() {
        return mIntercept;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mIntercept;
    }

}
