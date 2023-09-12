package com.urovo.sdk.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.urovo.sdk.R;
import com.urovo.sdk.scanner.InnerScannerCustomImpl;
import com.urovo.sdk.scanner.listener.ScannerListener;
import com.urovo.sdk.scanner.utils.Constant;

public class ScanCustomActivity extends Activity {

    private LinearLayout llScan;
    private CheckBox checkbox_flash;
    public InnerScannerCustomImpl mInnerScanner = null;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_custom);
        checkbox_flash = (CheckBox) findViewById(R.id.checkbox);
        checkbox_flash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkbox_flash.setText(isChecked ? "Turn Off" : "Turn On");
                mInnerScanner.switchFlash(isChecked);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        llScan = (LinearLayout) findViewById(R.id.ll_scan);
        mInnerScanner = InnerScannerCustomImpl.getInstance(ScanCustomActivity.this);
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.Scankey.cameraId, Constant.CameraID.BACK);
        bundle.putInt(Constant.Scankey.timeOut, 30);
        String[] codeType_disable = {Constant.CodeType.QR_CODE};
        bundle.putStringArray(Constant.Scankey.codeType_disable, codeType_disable);
        mInnerScanner.startScan(ScanCustomActivity.this, llScan, bundle, new ScannerListener() {
            @Override
            public void onSuccess(String barcode) {
                showMessage(barcode);
            }

            @Override
            public void onError(int error, String message) {
                showMessage("onError: \n" + error + "\n" + message);
            }

            @Override
            public void onTimeout() {
                showMessage("onTimeout");
            }

            @Override
            public void onCancel() {
                showMessage("onCancel");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mInnerScanner.stopScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void showMessage(final String message) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ScanCustomActivity.this, message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

}
