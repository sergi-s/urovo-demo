package com.urovo.sdk.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.urovo.sdk.MainActivity;
import com.urovo.sdk.R;
import com.urovo.sdk.system.SystemProviderImpl;

import java.util.Locale;

public class SystemActivity extends BaseActivity implements View.OnClickListener {

    public SystemProviderImpl mSystemProvider = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);
        initView();
        mSystemProvider = SystemProviderImpl.getInstance(SystemActivity.this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_setDefaultDataSubId:
                outputText("setDefaultDataSubId");
                mSystemProvider.setDefaultDataSubId(0);
                break;
            case R.id.btn_setForceLockScreen:
                outputText("setForceLockScreen");
                mSystemProvider.setForceLockScreen(true);
                break;
            case R.id.btn_setLockSreenNon:
                outputText("setLockScreenNon");
                mSystemProvider.setLockScreenNon();
                break;
            case R.id.btn_setLanguage:
                outputText("setLanguage");
                mSystemProvider.setLanguage(new Locale("zh", "CN"));
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int keyAction = event.getAction();
        Log.e("MainActivity", "dispatchKeyEvent keyCode:" + keyCode + ", keyAction:" + keyAction);
        if (keyCode == 513 && keyAction == KeyEvent.ACTION_UP) { //1
            Toast.makeText(SystemActivity.this, "1", Toast.LENGTH_SHORT).show();
            return false;
        } else if (keyCode == 111 && keyAction == KeyEvent.ACTION_UP) { //2
            Toast.makeText(SystemActivity.this, "2", Toast.LENGTH_SHORT).show();
            return false;
        } else if (keyCode == 67 && keyAction == KeyEvent.ACTION_UP) { //3
            Toast.makeText(SystemActivity.this, "3", Toast.LENGTH_SHORT).show();
            return false;
        } else if (keyCode == 66 && keyAction == KeyEvent.ACTION_UP) { //4
            Toast.makeText(SystemActivity.this, "4", Toast.LENGTH_SHORT).show();
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    private void startActivity(Class<?> clsName) {
        Intent intent = new Intent(this, clsName);
        startActivity(intent);
        overridePendingTransition(R.anim.dync_in_from_right, R.anim.dync_out_to_left);
    }
}
