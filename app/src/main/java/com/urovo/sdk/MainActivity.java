package com.urovo.sdk;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.device.SEManager;
import android.os.Build;
import android.os.Bundle;
import android.system.StructUtsname;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.urovo.sdk.aid.UpdateAIDActivity;
import com.urovo.sdk.emv.EmvActivity;
import com.urovo.sdk.utils.RuntimePermissionManager;
import com.urovo.sdk.utils.SystemProperties;
import com.urovo.sdk.view.BaseActivity;
import com.urovo.sdk.view.BeeperActivity;
import com.urovo.sdk.view.EmvTestActivity;
import com.urovo.sdk.view.ICReaderActivity;
import com.urovo.sdk.view.LedActivity;
import com.urovo.sdk.view.MagCardReaderActivity;
import com.urovo.sdk.view.PinpadActivity;
import com.urovo.sdk.view.PrintActivity;
import com.urovo.sdk.view.RFReaderActivity;
import com.urovo.sdk.view.BeamActivity;
import com.urovo.sdk.view.BeamActivity2;
import com.urovo.sdk.view.ScanActivity;
import com.urovo.sdk.view.ScanCustomActivity;
import com.urovo.sdk.view.SerialPortActivity;
import com.urovo.sdk.view.SilentInstallActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "model:" + Build.MODEL);
        requestPermission();
    }

    private RuntimePermissionManager mRuntimePermissionManager = null;

    private void requestPermission() {
        mRuntimePermissionManager = new RuntimePermissionManager(this);
        String[] pres = new String[] {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
        };
        executeRequestPermissionTask(pres, new RuntimePermissionManager.RequestPermissionCallback() {
            @Override
            public void onCallback(String[] permisssions, int[] grantResults, boolean allGranted) {
                Log.e(TAG, "allGranted:" + allGranted);
                if (!allGranted) { // 权限申请全部被允许
                    showMessage("Permission denied");
                }
            }
        });
    }

    /**
     * 申请一组权限，无显示说明提醒
     * 检查权限->请求权限->回调
     *
     * @param permissions 所需权限 android.Manifest.permission.
     * @param callback    需要权限的回调
     */
    public void executeRequestPermissionTask(String[] permissions,
            RuntimePermissionManager.RequestPermissionCallback callback) {
        mRuntimePermissionManager.executeRequestPermissionTask(permissions, null, null, callback);
    }

    /**
     * 权限申请结果转交给runtimePermissionManager处理，最后的结果在调用executeRequestPermissionTask()的任务中返回.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mRuntimePermissionManager.handleReuqestPermissionResult(requestCode, permissions, grantResults);
    }

    /**
     * 判断某一Service是否正在运行
     *
     * @param context     上下文
     * @param serviceName Service的全路径： 包名 + service的类名
     * @return true 表示正在运行，false 表示没有运行
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        if (context == null || TextUtils.isEmpty(serviceName)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return false;
        }
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(200);
        if (runningServiceInfos == null || runningServiceInfos.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo serviceInfo : runningServiceInfos) {
            Log.e(TAG, "" + serviceInfo.service.getClassName());
            if (serviceInfo != null && serviceInfo.service != null
                    && !TextUtils.isEmpty(serviceInfo.service.getClassName())
                    && serviceInfo.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Beeper:
                startActivity(BeeperActivity.class);
                break;
            case R.id.btn_MagCardReader:
                startActivity(MagCardReaderActivity.class);
                break;
            case R.id.btn_InsertCardReader:
                startActivity(ICReaderActivity.class);
                break;
            case R.id.btn_RFCardReader:
                startActivity(RFReaderActivity.class);
                break;
            case R.id.btn_RFCardReaderBeam:
                startActivity(BeamActivity.class);
                break;
            case R.id.btn_RFCardReaderBeam2:
                startActivity(BeamActivity2.class);
                break;
            case R.id.btn_pinpad:
                startActivity(PinpadActivity.class);
                break;
            case R.id.btn_Led:
                startActivity(LedActivity.class);
                break;
            case R.id.btn_print:
                startActivity(PrintActivity.class);
                break;
            case R.id.btn_Scanner:
                startActivity(ScanActivity.class);
                break;
            case R.id.btn_Scanner_custom:
                startActivity(ScanCustomActivity.class);
                break;
            case R.id.btn_serialport:
                startActivity(SerialPortActivity.class);
                break;
            case R.id.btn_emvnfc:
                startActivity(EmvActivity.class);
                break;
            case R.id.btn_emvTest:
                startActivity(EmvTestActivity.class);
                break;
            case R.id.btn_updateaid:
                startActivity(UpdateAIDActivity.class);
                break;
            case R.id.btn_install:
                startActivity(SilentInstallActivity.class);
                break;
        }

    }

    public void getDeviceInfo() {
        byte[] rspData = new byte[64];
        byte[] rspLen = new byte[2];
        String seversion = "";
        try {
            int iRet = new SEManager().getFirmwareVersion(rspData, rspLen);
            byte[] rspBuff = new byte[rspLen[0]];
            System.arraycopy(rspData, 0, rspBuff, 0, rspBuff.length);
            seversion = new String(rspBuff, "GBK").toUpperCase();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.e(TAG, "SE version:" + seversion);

        String buildNumber = SystemProperties.getSystemProperty("ro.vendor.build.id", "").toUpperCase();
        Log.e(TAG, "Build number:" + buildNumber);

        String hwversion = SystemProperties.getSystemProperty("ro.ufs.hw.version",
                buildNumber.contains("SQ29WR") ? "V2.0.0"
                        : SystemProperties.getSystemProperty("persist.sys.hw.version", "Unknown"));
        if (seversion.contains("SQ29G")) {
            hwversion = SystemProperties.getSystemProperty("ro.ufs.hw.version",
                    buildNumber.contains("SQ29WR") ? "V2.1.0"
                            : SystemProperties.getSystemProperty("persist.sys.hw.version", "Unknown"));
        }
        Log.e(TAG, "Hardware version:" + hwversion);

        String fwversion = SystemProperties.getSystemProperty("ro.ufs.sw", buildNumber.contains("SQ29WR") ? "V2.0.0"
                : SystemProperties.getSystemProperty("ro.build.sw", "V2.0.0"));
        if (seversion.contains("SQ29G")) {
            fwversion = SystemProperties.getSystemProperty("ro.ufs.sw", buildNumber.contains("SQ29WR") ? "V2.1.0"
                    : SystemProperties.getSystemProperty("ro.build.sw", "V2.0.0"));
        }
        Log.e(TAG, "Firmware version:" + fwversion);

        String ufsCustom = SystemProperties.getSystemProperty("ro.ufs.custom",
                SystemProperties.getSystemProperty("pwv.custom.custom", "unknown"));
        String ufsAttatch = SystemProperties.getSystemProperty("ro.ufs.custom.attach",
                SystemProperties.getSystemProperty("pwv.custom.custom.attach", "unknown"));
        String ufsVersion = SystemProperties.getSystemProperty("ro.ufs.build.version", "");
        String ufsDate = SystemProperties.getSystemProperty("ro.ufs.build.date.utc", "");
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append(ufsCustom);
        if (!TextUtils.equals(ufsAttatch.toUpperCase(), "XX"))
            stringBuilder.append("_" + ufsAttatch);
        if (!TextUtils.isEmpty(ufsVersion))
            stringBuilder.append("_" + ufsVersion);
        if (!TextUtils.isEmpty(ufsDate)) {
            String format = "yyyyMMdd";
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            stringBuilder.append("_" + sdf.format(new Date(Long.valueOf(ufsDate))));
        }
        Log.e(TAG, "Custom version:" + stringBuilder.toString().toUpperCase());
    }

    static String formatKernelVersion(Context context, StructUtsname uname) {
        if (uname == null) {
            return "Unavailable";
        }
        // Example:
        // 4.9.29-g958411d
        // #1 SMP PREEMPT Wed Jun 7 00:06:03 CST 2017
        final String VERSION_REGEX = "(#\\d+) " + /* group 1: "#1" */
                "(?:.*?)?" + /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
                "((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)"; /* group 2: "Thu Jun 28 11:02:39 PDT 2012" */
        Matcher m = Pattern.compile(VERSION_REGEX).matcher(uname.version);
        if (!m.matches()) {
            return "Unavailable";
        }

        // Example output:
        // 4.9.29-g958411d
        // #1 Wed Jun 7 00:06:03 CST 2017
        return new StringBuilder().append(uname.release)
                .append("\n")
                .append(m.group(1))
                .append(" ")
                .append(m.group(2)).toString();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e(TAG, "onKeyDown: keyCode=" + keyCode + ", event=" + event.getKeyCode());
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.e(TAG, "dispatchKeyEvent: keyCode=" + event.getKeyCode() + ", event=" + event.getAction());
        // if (event.getKeyCode() == KeyEvent.KEYCODE_MENU && event.getAction() ==
        // KeyEvent.ACTION_UP) {
        // showMessage("Menu button");
        // } else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction()
        // == KeyEvent.ACTION_UP) {
        // showMessage("Back button");
        // }
        return super.dispatchKeyEvent(event);
        // return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void startActivity(Class<?> clsName) {
        Intent intent = new Intent(this, clsName);
        startActivity(intent);
        overridePendingTransition(R.anim.dync_in_from_right, R.anim.dync_out_to_left);
    }

}
