package com.urovo.sdk.emv;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import static com.urovo.sdk.emv.EmvActivity.REQUEST_ONLINE;
import static com.urovo.sdk.emv.EmvActivity.SHOW_ERR;
import static com.urovo.sdk.emv.EmvActivity.SHOW_INFO;
import static com.urovo.sdk.emv.EmvActivity.mHandler;

public class MyHandler extends Handler {

    public final static int iOnReturnCheckCardResultMag = 4 ;
    public final static int  iOnReturnTransactionResult = 5 ;
    public final static int  ionShowMsg = 6;
    public final static int  ionProcessICC = 7;
    public final static int  ionProcessNFC = 8;
    public final static int manyApp=11;



    public MyHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        Log.i("applog", "handleMessage msg.what:"+msg.what);
        switch (msg.what) {
            case iOnReturnCheckCardResultMag:
                String strResult = (String)  msg.obj;
                strResult = (String)  msg.obj;
                Toast.makeText(EmvActivity.mContext , strResult,
                        Toast.LENGTH_SHORT).show();
                break;
            case iOnReturnTransactionResult:
                int i = Log.i("applog", "iOnReturnTransactionResult");
                strResult = (String)  msg.obj;
                Toast.makeText(EmvActivity.mContext , strResult,
                        Toast.LENGTH_SHORT).show();
                break;
            case ionShowMsg:
                strResult = (String)  msg.obj;
                //Toast.makeText(EmvActivity.mContext , strResult,Toast.LENGTH_SHORT).show();
                Log.i("applog", "ionShowMsg");
                EmvActivity.text1.append("  "+strResult+"  ");

                break;


            case REQUEST_ONLINE:

                mHandler.removeMessages(REQUEST_ONLINE);
                Toast.makeText(EmvActivity.mContext, "  request online", Toast.LENGTH_SHORT).show();

                break;
            case SHOW_INFO:
                mHandler.removeMessages(SHOW_INFO);
                Bundle bundle = new Bundle();
                bundle = msg.getData();
                String strMsg = bundle.getString("msg"); //msg.getData().toString();
                EmvActivity.text1.append("  "+strMsg+"  ");
                //Toast.makeText(EmvActivity.mContext, strMsg, Toast.LENGTH_SHORT).show();
                //LogUtils.d(strMsg);
                Log.d("applog", "handleMessage 1 "+ strMsg);

                //Toast.makeText(EmvActivity.mContext, strMsg ,Toast.LENGTH_SHORT).show();
                break;
            case SHOW_ERR:
                //mHandler.removeMessages(SHOW_ERR);
                strMsg = msg.getData().toString();
                //Toast.makeText(EmvActivity.mContext, strMsg ,Toast.LENGTH_SHORT).show();
                EmvActivity.text1.append(strMsg+"  ");
                break;
            case manyApp:
                break;
            default:break;
        }
    }

}
