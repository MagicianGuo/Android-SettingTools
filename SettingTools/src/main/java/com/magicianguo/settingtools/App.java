package com.magicianguo.settingtools;

import android.app.Application;

public class App extends Application {
    private static App sApp;

    public static App getApp() {
        return sApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
    }
}
