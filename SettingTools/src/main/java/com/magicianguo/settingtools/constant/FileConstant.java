package com.magicianguo.settingtools.constant;

import com.magicianguo.settingtools.App;

public interface FileConstant {
    String PLUGIN_PATH = App.getApp().getExternalFilesDir(null).getAbsolutePath();
    String PLUGIN_NAME = "SettingToolsPlugin.apk";
    String AUTHORITY = App.getApp().getPackageName() + ".fileprovider";
}
