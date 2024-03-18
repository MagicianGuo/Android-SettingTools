package com.magicianguo.settingtools.util;

import android.widget.Toast;

import androidx.annotation.StringRes;

import com.magicianguo.settingtools.App;

public class ToastUtils {
    private static Toast mToast = null;

    public static void shortCall(@StringRes int resId) {
        shortCall(App.getApp().getString(resId));
    }

    public static void shortCall(String text) {
        cancel();
        mToast = Toast.makeText(App.getApp(), text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public static void longCall(@StringRes int resId) {
        longCall(App.getApp().getString(resId));
    }

    public static void longCall(String text) {
        cancel();
        mToast = Toast.makeText(App.getApp(), text, Toast.LENGTH_LONG);
        mToast.show();
    }

    private static void cancel() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
