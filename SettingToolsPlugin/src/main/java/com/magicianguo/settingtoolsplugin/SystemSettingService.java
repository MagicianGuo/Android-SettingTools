package com.magicianguo.settingtoolsplugin;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;

import com.magicianguo.settingtoolsaidl.ISystemSetting;
import com.magicianguo.settingtoolsaidl.ISystemSettingCallback;

public class SystemSettingService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return BINDER;
    }

    private final Binder BINDER = new ISystemSetting.Stub() {
        @Override
        public void putString(String name, String value, ISystemSettingCallback callback) throws RemoteException {
            Log.d("TAG", "putString: name = " + name + " , value = " + value);
            if (!Settings.System.canWrite(SystemSettingService.this)) {
                callback.goWriteSettingPage();
                return;
            }
            Settings.System.putString(getContentResolver(), name, value);
        }
    };
}