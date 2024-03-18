package com.magicianguo.settingtools.util;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.IPackageInstaller;
import android.content.pm.IPackageInstallerSession;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.magicianguo.settingtools.App;
import com.magicianguo.settingtools.constant.PackageName;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

import rikka.shizuku.Shizuku;
import rikka.shizuku.ShizukuBinderWrapper;
import rikka.shizuku.demo.util.IIntentSenderAdaptor;
import rikka.shizuku.demo.util.IntentSenderUtils;
import rikka.shizuku.demo.util.PackageInstallerUtils;
import rikka.shizuku.demo.util.ShizukuSystemServerApi;

public class PackageUtils {
    private static final String TAG = "PackageUtils";
    private static final PackageManager PACKAGE_MANAGER = App.getApp().getPackageManager();

    public static boolean isShizukuAvailable() {
        return isInstalled(PackageName.SHIZUKU) && Shizuku.pingBinder();
    }

    public static boolean isPluginInstalled() {
        return isInstalled(PackageName.PLUGIN);
    }

    private static boolean isInstalled(String pkg) {
        try {
            PACKAGE_MANAGER.getPackageInfo(pkg, 0);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return false;
    }

    public static boolean installApkByShizuku(Uri uri) {
        PackageInstaller.Session session = null;
        ContentResolver cr = App.getApp().getContentResolver();

        try {
            IPackageInstaller _packageInstaller = ShizukuSystemServerApi.PackageManager_getPackageInstaller();

            // the reason for use "com.android.shell" as installer package under adb is that getMySessions will check installer package's owner
            String installerPackageName = "com.android.shell";
            String installerAttributionTag = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                installerAttributionTag = App.getApp().getAttributionTag();
            }
            int userId = 0;
            PackageInstaller packageInstaller = PackageInstallerUtils.createPackageInstaller(_packageInstaller, installerPackageName, installerAttributionTag, userId);
            int sessionId;

            PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
            int installFlags = PackageInstallerUtils.getInstallFlags(params);
            installFlags |= 0x00000004/*PackageManager.INSTALL_ALLOW_TEST*/
                    | 0x00000002/*PackageManager.INSTALL_REPLACE_EXISTING*/
                    | 0x01000000/*PackageManager.INSTALL_BYPASS_LOW_TARGET_SDK_BLOCK*/;
            PackageInstallerUtils.setInstallFlags(params, installFlags);

            sessionId = packageInstaller.createSession(params);

            IPackageInstallerSession _session = IPackageInstallerSession.Stub.asInterface(new ShizukuBinderWrapper(_packageInstaller.openSession(sessionId).asBinder()));
            session = PackageInstallerUtils.createSession(_session);


            String name = "0.apk";
            InputStream is = cr.openInputStream(uri);
            OutputStream os = session.openWrite(name, 0, -1);
            byte[] buf = new byte[8192];
            int len;
            try {
                while ((len = is.read(buf)) > 0) {
                    os.write(buf, 0, len);
                    os.flush();
                    session.fsync(os);
                }
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            Intent[] results = new Intent[]{null};
            CountDownLatch countDownLatch = new CountDownLatch(1);
            IntentSender intentSender = IntentSenderUtils.newInstance(new IIntentSenderAdaptor() {
                @Override
                public void send(Intent intent) {
                    results[0] = intent;
                    countDownLatch.countDown();
                }
            });
            session.commit(intentSender);

            countDownLatch.await();
            Intent result = results[0];
            int status = result.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE);
            String message = result.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE);
            Log.d(TAG, "installApkByShizuku: status = " + status + " , message = " + message);
            return status == 0;
        } catch (Throwable tr) {
            tr.printStackTrace();
        } finally {
            if (session != null) {
                try {
                    session.close();

                } catch (Throwable tr) {
                }
            }
        }
        return false;
    }
}
