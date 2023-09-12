package com.urovo.sdk.view;

import android.device.DeviceManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.urovo.sdk.R;
import com.urovo.sdk.install.InstallManagerImpl;
import com.urovo.sdk.install.listener.InstallApkListener;

public class SilentInstallActivity extends BaseActivity implements View.OnClickListener {

    private EditText editText_apkpath;
    private EditText editText_pachkageName;
    private Button button_install;
    private Button button_uninstall;
    private InstallManagerImpl mInstallManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install);
        initView();

        editText_apkpath = (EditText) findViewById(R.id.editText_path);
        editText_pachkageName = (EditText) findViewById(R.id.editText_packagename);
        button_install = (Button) findViewById(R.id.btn_install);
        button_uninstall = (Button) findViewById(R.id.btn_uninstall);

        mInstallManager = InstallManagerImpl.getInstance(SilentInstallActivity.this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_install:
                outputColorText(TextColor.BLUE, "Install APK");
                String path = editText_apkpath.getText().toString().trim();
                if (TextUtils.isEmpty(path)) {
                    showMessage("Input APK Path");
                    outputColorText(TextColor.BLUE, "Input APK Path");
                    return;
                }
                try {
                    mInstallManager.install(path, new InstallApkListener() {
                        @Override
                        public void onInstallFinished(String packageName, int returnCode, String returnMsg) {
                            Log.e("MainActivity", "onInstallFinished, packageName:" + packageName + ", returnCode:" + returnCode + ", returnMsg:" + returnMsg);
                            outputColorText(TextColor.BLUE, "onInstallFinished, packageName:" + packageName + ", returnCode:" + returnCode + ", returnMsg:" + returnMsg);
                        }

                        @Override
                        public void onUnInstallFinished(String packageName, int returnCode, String returnMsg) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_uninstall:
                outputColorText(TextColor.BLUE, "UnInstall APK");
                String name = editText_pachkageName.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    showMessage("Input APK Package Name");
                    outputColorText(TextColor.BLUE, "Input APK Package Name");
                    return;
                }
                try {
                    InstallManagerImpl.getInstance(SilentInstallActivity.this).uninstall(name, new InstallApkListener() {
                        @Override
                        public void onInstallFinished(String packageName, int returnCode, String returnMsg) {

                        }

                        @Override
                        public void onUnInstallFinished(String packageName, int returnCode, String returnMsg) {
                            Log.e("MainActivity", "onUnInstallFinished, packageName:" + packageName + ", returnCode:" + returnCode + ", returnMsg:" + returnMsg);
                            outputColorText(TextColor.BLUE, "onUnInstallFinished, packageName:" + packageName + ", returnCode:" + returnCode + ", returnMsg:" + returnMsg);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

}