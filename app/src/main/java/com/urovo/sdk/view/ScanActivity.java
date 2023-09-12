package com.urovo.sdk.view;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.urovo.sdk.R;
import com.urovo.sdk.scanner.InnerScannerImpl;
import com.urovo.sdk.scanner.listener.ScannerListener;
import com.urovo.sdk.scanner.utils.Constant;
import com.urovo.sdk.utils.DateUtil;

import java.util.Date;

public class ScanActivity extends BaseActivity implements View.OnClickListener {

    public InnerScannerImpl mInnerScanner = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mInnerScanner = InnerScannerImpl.getInstance(ScanActivity.this);
    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.btnFrontScan:
                    outputText("startScan,please scan the qr code in 30 seconds: " + DateUtil.getDateTime(new Date()));
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.Scankey.upPromptString, "upPromptString");
                    bundle.putString(Constant.Scankey.downPromptString, "downPromptString");
                    bundle.putString(Constant.Scankey.title, "title");
                    mInnerScanner.startScan(ScanActivity.this, bundle, Constant.CameraID.FRONT, 30, new ScannerListener() {
                        @Override
                        public void onSuccess(String barcode) {
                            outputText("onSuccess: " + barcode);
                        }

                        @Override
                        public void onError(int error, String message) {
                            outputText("onError: \n" + error + "\n" + message);
                        }

                        @Override
                        public void onTimeout() {
                            outputText("onTimeout");
                        }

                        @Override
                        public void onCancel() {
                            outputText("onCancel");
                        }
                    });
                    break;
                case R.id.btnBackScan:
                    outputText("startScan,please scan the qr code in 30 seconds: " + DateUtil.getDateTime(new Date()));
                    bundle = new Bundle();
                    bundle.putString(Constant.Scankey.upPromptString, "upPromptString");
                    bundle.putString(Constant.Scankey.downPromptString, "downPromptString");
                    bundle.putString(Constant.Scankey.title, "title");
                    mInnerScanner.startScan(ScanActivity.this, bundle, Constant.CameraID.BACK, 30, new ScannerListener() {
                        @Override
                        public void onSuccess(String barcode) {
                            outputText("onSuccess: " + barcode);
                        }

                        @Override
                        public void onError(int error, String message) {
                            outputText("onError: \n" + error + "\n" + message);
                        }

                        @Override
                        public void onTimeout() {
                            outputText("onTimeout");
                        }

                        @Override
                        public void onCancel() {
                            outputText("onCancel");
                        }
                    });
                    break;
                case R.id.btnCloseScan:
                    outputText("stopScan");
                    mInnerScanner.stopScan();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
