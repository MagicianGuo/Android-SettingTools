package com.magicianguo.settingtools.util;

import java.io.OutputStream;
import java.lang.reflect.Method;

import rikka.shizuku.Shizuku;
import rikka.shizuku.ShizukuRemoteProcess;

public class CmdUtils {
    public static synchronized void exec(String cmd, Runnable runnable) {
        try {
            Method newProcess = Shizuku.class.getDeclaredMethod("newProcess", String[].class, String[].class, String.class);
            newProcess.setAccessible(true);
            ShizukuRemoteProcess process = (ShizukuRemoteProcess) newProcess.invoke(null, new String[]{"sh"}, null, null);
            OutputStream outputStream = process.getOutputStream();
            outputStream.write((cmd + "\nexit\n").getBytes());
            outputStream.flush();
            outputStream.close();
            process.waitFor();
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
