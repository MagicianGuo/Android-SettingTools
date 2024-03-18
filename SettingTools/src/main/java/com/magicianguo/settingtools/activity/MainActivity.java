package com.magicianguo.settingtools.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.magicianguo.settingtools.databinding.ActivityMainBinding;
import com.magicianguo.settingtools.util.SystemSettingServiceManager;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.btnBind.setOnClickListener(v -> {
            Log.d("TAG", "initView: btn bind");
            SystemSettingServiceManager.testString();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}