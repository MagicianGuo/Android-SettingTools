package com.magicianguo.settingtools.view;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.magicianguo.settingtools.util.PackageUtils;

import rikka.shizuku.Shizuku;

public class SamsungRefreshRateModePanel extends RelativeLayout {
    private boolean mIntercept = false;

    public SamsungRefreshRateModePanel(Context context) {
        super(context);
    }

    public SamsungRefreshRateModePanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SamsungRefreshRateModePanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mIntercept = !(PackageUtils.isShizukuAvailable() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED);
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
