package com.magicianguo.settingtools.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.magicianguo.settingtools.App;
import com.magicianguo.settingtoolsaidl.ISystemSetting;

public class SystemSettingServiceManager {
    private static final String TAG = "SystemSettingServiceManager";
    private static ISystemSetting iSystemSetting;
    private static Runnable mRunnable;
    private static final ServiceConnection SERVICE_CONNECTION = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            iSystemSetting = ISystemSetting.Stub.asInterface(service);
            if (mRunnable != null) {
                mRunnable.run();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
            iSystemSetting = null;
        }
    };

    private static void checkBindService() {
        if (iSystemSetting == null) {
            final Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.magicianguo.settingtoolsplugin",
                    "com.magicianguo.settingtoolsplugin.SystemSettingService"));
            App.getApp().bindService(intent, SERVICE_CONNECTION, Context.BIND_AUTO_CREATE);
        }
    }

    public static void testString() {
        runWhenConnected(() -> {
            try {
                Log.d(TAG, "test: str = " + iSystemSetting.testString());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        checkBindService();
    }

    private static void runWhenConnected(Runnable runnable) {
        if (iSystemSetting != null) {
            runnable.run();
            mRunnable = null;
        } else {
            mRunnable = runnable;
        }
    }
}
