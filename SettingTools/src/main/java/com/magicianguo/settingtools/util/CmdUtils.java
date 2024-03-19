package com.magicianguo.settingtools.util;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.util.Log;

import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import moe.shizuku.server.IRemoteProcess;
import rikka.shizuku.Shizuku;
import rikka.shizuku.ShizukuRemoteProcess;

public class CmdUtils {
    private static final String TAG = "CmdUtils";

    /**
     * @deprecated {@link Shizuku#newProcess(String[], String[], String)} 方法即将废弃，推荐使用方法 {@link #execWithBinder(String, Runnable)}
     */
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

    public static synchronized void execWithBinder(String cmd, Runnable runnable) {
        Parcel obtain = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("moe.shizuku.server.IShizukuService");
            obtain.writeStringArray(new String[]{"sh"});
            obtain.writeStringArray(null);
            obtain.writeString(null);

            ShizukuImpl.getService().asBinder().transact(/* IShizukuService.TRANSACTION_newProcess */8, obtain, reply, 0);
            reply.readException();
            IBinder readStrongBinder = reply.readStrongBinder();
            IRemoteProcess queryLocalInterface;
            IInterface iInterface = readStrongBinder.queryLocalInterface("moe.shizuku.server.IRemoteProcess");
            Log.d(TAG, "execWithBinder: iInterface = " + iInterface);
            if (iInterface instanceof IRemoteProcess) {
                queryLocalInterface = (IRemoteProcess) iInterface;
            } else {
                Class<?> cls = Class.forName("moe.shizuku.server.IRemoteProcess$Stub$Proxy");
                Constructor<?> constructor = cls.getDeclaredConstructor(IBinder.class);
                constructor.setAccessible(true);
                queryLocalInterface = (IRemoteProcess) constructor.newInstance(readStrongBinder);
            }

            Constructor<ShizukuRemoteProcess> constructor1 = ShizukuRemoteProcess.class.getDeclaredConstructor(IRemoteProcess.class);
            constructor1.setAccessible(true);
            ShizukuRemoteProcess process = constructor1.newInstance(queryLocalInterface);

            OutputStream outputStream = process.getOutputStream();
            outputStream.write((cmd + "\nexit\n").getBytes());
            outputStream.flush();
            outputStream.close();
            process.waitFor();

            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            obtain.recycle();
            reply.recycle();
        }
    }
}
