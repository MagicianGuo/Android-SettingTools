package com.magicianguo.settingtools.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.magicianguo.settingtools.R;
import com.magicianguo.settingtools.constant.FileConstant;
import com.magicianguo.settingtools.constant.SettingsSecure;
import com.magicianguo.settingtools.constant.SettingsSystem;
import com.magicianguo.settingtools.databinding.ActivityMainBinding;
import com.magicianguo.settingtools.util.PackageUtils;
import com.magicianguo.settingtools.util.SystemSettingServiceManager;
import com.magicianguo.settingtools.util.TaskPool;
import com.magicianguo.settingtools.util.ToastUtils;

import java.io.File;

import rikka.shizuku.Shizuku;
import rikka.shizuku.demo.util.ShizukuSystemServerApi;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SELECT_APK = 0;
    private static final int REQUEST_CODE_INSTALL_PLUGIN = 1;
    private static final int REQUEST_CODE_SAMSUNG_REFRESH_RATE_MODE = 2;
    private float mMinRate = 60;
    private float mMaxRate = 60;
    private ActivityMainBinding binding;
    private final Shizuku.OnRequestPermissionResultListener SHIZUKU_PERMISSION_LISTENER = (requestCode, grantResult) -> {
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_CODE_SELECT_APK) {
                doSelectApk();
            } else if (requestCode == REQUEST_CODE_INSTALL_PLUGIN) {
                installPluginByShizuku();
            }
        } else {
            ToastUtils.shortCall(R.string.toast_shizuku_permission_denied);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        Shizuku.addRequestPermissionResultListener(SHIZUKU_PERMISSION_LISTENER);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        ContentResolver cr = getContentResolver();
        binding.btnSelectApk.setOnClickListener(v ->
                shizukuOperation(REQUEST_CODE_SELECT_APK, this::doSelectApk)
        );
        // 三星屏幕刷新模式
        if (Build.BRAND.equals("samsung")) {
            try {
                int refreshRateMode = Settings.Secure.getInt(cr, SettingsSecure.REFRESH_RATE_MODE);
                int id;
                switch (refreshRateMode) {
                    case 0:
                        id = R.id.rb_samsung_rate_mode_standard;
                        break;
                    case 1:
                        id = R.id.rb_samsung_rate_mode_auto;
                        break;
                    default:
                        throw new RuntimeException("Unsupported refresh mode: " + refreshRateMode);
                }
                binding.rgSamsungRefreshRateMode.check(id);
            } catch (Settings.SettingNotFoundException e) {
                throw new RuntimeException(e);
            }
            binding.rlSamsungRefreshRateMode.setVisibility(View.VISIBLE);
            binding.rlSamsungRefreshRateMode.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP && binding.rlSamsungRefreshRateMode.isIntercept()) {
                    shizukuOperation(REQUEST_CODE_SAMSUNG_REFRESH_RATE_MODE, () -> {});
                }
                return true;
            });
            binding.rgSamsungRefreshRateMode.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.rb_samsung_rate_mode_standard) {
                    saveSamsungRateMode(0);
                } else if (checkedId == R.id.rb_samsung_rate_mode_auto) {
                    saveSamsungRateMode(1);
                }
            });
        }
        // 最低刷新率
        boolean enableMinRate = true;
        try {
            mMinRate = Float.parseFloat(Settings.System.getString(cr, SettingsSystem.MIN_REFRESH_RATE));
        } catch (Exception e) {
            enableMinRate = false;
            mMinRate = 60;
        }
        binding.swMinRefreshEnable.setChecked(enableMinRate);
        binding.sbMinRefreshRate.setEnabled(enableMinRate);
        binding.sbMinRefreshRate.setProgress((int) (mMinRate - 20));
        binding.tvMinRefreshRate.setText(getString(R.string.title_min_refresh_rate, (int) mMinRate));
        binding.swMinRefreshEnable.setOnCheckedChangeListener((cb, isChecked) -> {
            String minRateStr;
            if (isChecked) {
                minRateStr = "" + mMinRate;
            } else {
                minRateStr = null;
            }
            binding.sbMinRefreshRate.setEnabled(isChecked);
            SystemSettingServiceManager.putString(SettingsSystem.MIN_REFRESH_RATE, minRateStr);
        });
        binding.sbMinRefreshRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMinRate = progress + 20;
                    binding.tvMinRefreshRate.setText(getString(R.string.title_min_refresh_rate, (int) mMinRate));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SystemSettingServiceManager.putString(SettingsSystem.MIN_REFRESH_RATE, "" + mMinRate);
            }
        });
        // 最高刷新率
        boolean enableMaxRate = true;
        try {
            mMaxRate = Float.parseFloat(Settings.System.getString(cr, SettingsSystem.PEAK_REFRESH_RATE));
        } catch (Exception e) {
            enableMaxRate = false;
            mMaxRate = 60;
        }
        binding.swMaxRefreshEnable.setChecked(enableMaxRate);
        binding.sbMaxRefreshRate.setEnabled(enableMaxRate);
        binding.sbMaxRefreshRate.setProgress((int) (mMaxRate - 20));
        binding.tvMaxRefreshRate.setText(getString(R.string.title_max_refresh_rate, (int) mMaxRate));
        binding.swMaxRefreshEnable.setOnCheckedChangeListener((cb, isChecked) -> {
            String maxRateStr;
            if (isChecked) {
                maxRateStr = "" + mMaxRate;
            } else {
                maxRateStr = null;
            }
            binding.sbMaxRefreshRate.setEnabled(isChecked);
            SystemSettingServiceManager.putString(SettingsSystem.PEAK_REFRESH_RATE, maxRateStr);
        });
        binding.sbMaxRefreshRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMaxRate = progress + 20;
                    binding.tvMaxRefreshRate.setText(getString(R.string.title_max_refresh_rate, (int) mMaxRate));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SystemSettingServiceManager.putString(SettingsSystem.PEAK_REFRESH_RATE, "" + mMaxRate);
            }
        });
        binding.llRefreshRate.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && binding.llRefreshRate.isIntercept()) {
                showPluginInstallDialog();
            }
            return true;
        });
        // 修改分辨率
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        binding.etWidth.setText("" + dm.widthPixels);
        binding.etHeight.setText("" + dm.heightPixels);
        binding.etDensity.setText("" + dm.densityDpi);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Shizuku.removeRequestPermissionResultListener(SHIZUKU_PERMISSION_LISTENER);
        binding = null;
    }

    private void shizukuOperation(int requestCode, Runnable runnable) {
        if (PackageUtils.isShizukuAvailable()) {
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                runnable.run();
            } else {
                Shizuku.requestPermission(requestCode);
            }
        } else {
            ToastUtils.longCall(R.string.toast_please_install_and_launch_shizuku);
        }
    }

    private void doSelectApk() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/vnd.android.package-archive");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri initUri = Uri.parse("content://com.android.externalstorage.documents/document/primary:");
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, initUri);
        }
        startActivityForResult(intent, REQUEST_CODE_SELECT_APK);
    }

    private void saveSamsungRateMode(int mode) {
        grantWriteSecureSettings();
        shizukuOperation(REQUEST_CODE_SAMSUNG_REFRESH_RATE_MODE,
                () -> Settings.Secure.putInt(getContentResolver(), SettingsSecure.REFRESH_RATE_MODE, mode));
    }

    private void doInstallApkByShizuku(Uri uri) {
        ToastUtils.shortCall(R.string.installing);
        TaskPool.CACHE.execute(() -> {
            boolean result = PackageUtils.installApkByShizuku(uri);
            TaskPool.MAIN.post(() -> {
                ToastUtils.shortCall(result ? R.string.install_succeed : R.string.install_failed);
            });
        });
    }

    private void showPluginInstallDialog() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.dialog_install_plugin_message)
                .setPositiveButton(R.string.install, (dialog, which) -> installPlugin())
                .setNegativeButton(R.string.cancel, (dialog, which) -> {})
                .create().show();
    }

    private void installPlugin() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (PackageUtils.isShizukuAvailable()) {
                if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                    installPluginByShizuku();
                } else {
                    Shizuku.requestPermission(REQUEST_CODE_INSTALL_PLUGIN);
                }
            } else {
                ToastUtils.longCall(R.string.toast_need_shizuku_because_of_android_14);
            }
        } else {
            String filePath = FileConstant.PLUGIN_PATH + "/" + FileConstant.PLUGIN_NAME;
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            File file = new File(filePath);
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(this, FileConstant.AUTHORITY, file);
            } else {
                uri = Uri.fromFile(file);
            }
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        }
    }

    private void installPluginByShizuku() {
        ToastUtils.shortCall(R.string.installing);
        TaskPool.CACHE.execute(() -> {
            Uri uri = Uri.fromFile(new File(FileConstant.PLUGIN_PATH + "/" + FileConstant.PLUGIN_NAME));
            boolean result = PackageUtils.installApkByShizuku(uri);
            TaskPool.MAIN.post(() -> {
                ToastUtils.shortCall(result ? R.string.install_succeed : R.string.install_failed);
            });
        });
    }

    private void grantWriteSecureSettings() {
        String permission = Manifest.permission.WRITE_SECURE_SETTINGS;
        String pkg = getPackageName();
        int userId = 0;
        try {
            if (ShizukuSystemServerApi.PackageManager_checkPermission(permission, pkg, userId)
                    != PackageManager.PERMISSION_GRANTED) {
                ShizukuSystemServerApi.PackageManager_grantRuntimePermission(pkg, permission, userId);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_APK) {
            if (data != null && data.getData() != null) {
                doInstallApkByShizuku(data.getData());
            } else {
                ToastUtils.shortCall(R.string.apk_is_not_selected);
            }
        }
    }

}