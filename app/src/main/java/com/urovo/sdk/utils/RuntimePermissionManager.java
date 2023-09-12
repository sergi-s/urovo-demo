package com.urovo.sdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.SparseArray;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;

/**
 * Created by zje on 2018/2/2.
 */

public class RuntimePermissionManager implements Destroyable {
    //为防止内存溢出，将寄主置为弱引用
    protected WeakReference<Activity> activity;

    // 原子整型，读个数， AtomicInteger可以在并发情况下达到原子化更新，避免使用了synchronized。
    private AtomicInteger mPermissionRequestCode = new AtomicInteger(0);
    //权限任务池
    private SparseArray<RequestPermissionCallback> mCallbackPool = new SparseArray<>();

    public RuntimePermissionManager(Activity activity){
        if (activity == null){
            throw new RuntimeException("initialize RuntimePermissionManager activity is null");
        }
        this.activity = new WeakReference<>(activity);
    }

    public void executeRequestPermissionTask(final String[] permissions, String attentiveTitle, String attentiveContent, final RequestPermissionCallback callback){
        final Activity activity = this.activity.get();
        if(activity == null){
            onDestroy();
            return;
        }
        if(permissions == null || permissions.length <= 0){
            throw new RuntimeException("permissions is null");
        }
        //判断全部权限是否开启
        boolean allGranted = true;
        for(String permission : permissions){
            if(checkPermission(activity, permission) != PackageManager.PERMISSION_GRANTED){
                allGranted = false;
                break;
            }
        }

        if(allGranted){
            //权限已开启则直接回调任务
            int[] grantResults = new int[permissions.length];
            for (int i = 0 ; i < grantResults.length ; i++){
                grantResults[i] = PackageManager.PERMISSION_GRANTED;
            }
            callback.onCallback(permissions, grantResults, true);
        }else{
            //若有权限未开启则申请权限
            boolean shouldShowRationale = false;
            for (String permission : permissions){
                if (shouldShowRequestPermissionRationale(activity, permission)){
                    shouldShowRationale = true;
                    break;
                }
            }
            if((!TextUtils.isEmpty(attentiveTitle) || !TextUtils.isEmpty(attentiveContent)) && shouldShowRationale){
                /* AlertDialogUtils.showCustomAlertDialog(activity, attentiveTitle, attentiveContent, "确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestPermissions(activity, permissions, callback);
                    }
                });*/
            }else{
                requestPermissions(activity, permissions, callback);
            }
        }
    }

    private void requestPermissions(Activity activity, String[] permissions, RequestPermissionCallback callback){
        final int requestCode = mPermissionRequestCode.incrementAndGet() + 1000;
        mCallbackPool.put(requestCode, callback);
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    private boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            return activity.shouldShowRequestPermissionRationale(permission);
        }
        return false;
    }

    private int checkPermission(Context context, String permission){
        if(permission == null){
            throw new IllegalArgumentException("checkPermission permission is null");
        }
        return context.checkPermission(permission, Process.myPid(), Process.myUid());
    }

    /**
     * 处理权限申请结果（拦截Activity onRequestPermissionsResult()）
     *
     * @param requestCode   请求码（onRequestPermissionsResult传进）
     * @param permissions   权限（onRequestPermissionsResult传进）
     * @param grantResults  返回码（onRequestPermissionsResult传进）
     */
    public void handleReuqestPermissionResult(int requestCode, String[] permissions, int[] grantResults){
        final Activity activity = this.activity.get();
        if (activity == null){
            onDestroy();
            return;
        }
        //判断权限是否全部允许
        boolean allGranted = true;
        if(grantResults == null || grantResults.length <= 0){
            allGranted = false;
        }else {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
        }

        //从池子里取出对应的任务
        RequestPermissionCallback callback = mCallbackPool.get(requestCode);
        if(callback != null){
            //从任务池中移除
            mCallbackPool.remove(requestCode);
            callback.onCallback(permissions, grantResults, allGranted);
        }
    }

    /**
     * 清空任务池
     */
    public void clearPool(){
        if (mCallbackPool != null){
            mCallbackPool.clear();
        }
    }

    public void onDestroy() {
        this.activity.clear();
    }

    @Override
    public void destroy() throws DestroyFailedException {

    }

    @Override
    public boolean isDestroyed() {
        return false;
    }


    /**
     * 请求权限结果回调
     */
    public interface RequestPermissionCallback{

        /**
         * 请求权限结果回调方法
         *
         * @param permisssions  请求的权限列表
         * @param grantResults  结果列表
         * @param allGranted    是否所有的权限都被允许
         */
        void onCallback(String[] permisssions, int[] grantResults, boolean allGranted);
    }

}
