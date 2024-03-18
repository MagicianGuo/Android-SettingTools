package com.magicianguo.settingtools;

import android.app.Application;
import android.os.Build;

import com.magicianguo.settingtools.constant.FileConstant;
import com.magicianguo.settingtools.util.TaskPool;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class App extends Application {
    private static App sApp;

    public static App getApp() {
        return sApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        TaskPool.CACHE.execute(() -> {
            addHiddenApiExemptions();
            copyPluginApk();
        });
    }

    private void addHiddenApiExemptions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.addHiddenApiExemptions("L");
        }
    }

    private void copyPluginApk() {
        try {
            File file = new File(FileConstant.PLUGIN_PATH, FileConstant.PLUGIN_NAME);
            if (file.exists()) {
                file.delete();
            }
            InputStream is = getAssets().open(FileConstant.PLUGIN_NAME);
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            byte[] bytes = new byte[8192];
            int read;
            while ((read = is.read(bytes)) >= 0) {
                bos.write(bytes, 0, read);
            }
            is.close();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
