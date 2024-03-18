package com.magicianguo.settingtools.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;

import com.magicianguo.settingtools.App;
import com.magicianguo.settingtools.constant.PackageName;
import com.magicianguo.settingtoolsaidl.ISystemSetting;
import com.magicianguo.settingtoolsaidl.ISystemSettingCallback;

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

    private static final ISystemSettingCallback SERVER_CALLBACK = new ISystemSettingCallback.Stub() {
        @Override
        public void goWriteSettingPage() {
            Context context = App.getApp();
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + PackageName.PLUGIN));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
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

    public static void putString(String name, String value) {
        runWhenConnected(() -> {
            try {
                Log.d(TAG, "putString: name = " + name + " , value = " + value);
                iSystemSetting.putString(name, value, SERVER_CALLBACK);
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
