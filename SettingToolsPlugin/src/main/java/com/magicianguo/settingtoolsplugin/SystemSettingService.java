package com.magicianguo.settingtoolsplugin;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.magicianguo.settingtoolsaidl.ISystemSetting;

public class SystemSettingService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return BINDER;
    }

    private final Binder BINDER = new ISystemSetting.Stub() {
        @Override
        public String testString() {
            return "TEST STRING";
        }
    };
}