package com.magicianguo.settingtoolsaidl;

import com.magicianguo.settingtoolsaidl.ISystemSettingCallback;

interface ISystemSetting {
    void putString(String name, String value, ISystemSettingCallback callback);
}