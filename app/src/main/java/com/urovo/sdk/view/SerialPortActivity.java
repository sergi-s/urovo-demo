package com.urovo.sdk.view;

import java.util.Date;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.urovo.sdk.R;
import com.urovo.sdk.utils.BytesUtil;
import com.urovo.sdk.utils.DateUtil;
import com.urovo.smartpos.device.core.SerialPortDriverImpl;

public class SerialPortActivity extends BaseActivity implements OnClickListener {

    public static final String TAG = "SerialPortActivity";
    private EditText editText_cmd;
    private static final int MESSAGE_MSG = 0x01;
    private SerialPortDriverImpl mSerialPortDriverImpl;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_MSG:
                    String message = (String) msg.obj;
                    outputText("" + message);
                    Log.e(TAG, "" + message);
                    break;
            }
        }
    };

    private String PORT_PATH = "/dev/ttyGS0";
//    private String PORT_PATH = "/dev/ttyMSM1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serialport);
        initView();

        editText_cmd = (EditText) findViewById(R.id.editText_cmd);
        mSerialPortDriverImpl = SerialPortDriverImpl.getInstance();
        button_clearAPDU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_cmd.setText("");
            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        /**
         * @Description:
         * @author luoan
         * @version time: 2019-12-10 下午5:26:41
         */
        boolean status = false;
        try {
            switch (v.getId()) {
                case R.id.btnOpen:
                    sendMessage("open");
                    status = mSerialPortDriverImpl.open(PORT_PATH, 115200, 0, 8);
                    sendMessage("" + status);
                    break;
                case R.id.btnRead:
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                sendMessage("read: " + DateUtil.getDateTime(new Date()));
                                byte[] buffer = new byte[50];
                                int timeOut = 5 * 1000;
                                int retLen = mSerialPortDriverImpl.read(buffer, timeOut);
                                if (retLen <= 0) {
                                    sendMessage("read failed: " + retLen + "，" + DateUtil.getDateTime(new Date()));
                                } else {
                                    byte[] result = new byte[retLen];
                                    System.arraycopy(buffer, 0, result, 0, retLen);
                                    sendMessage("read success: " + DateUtil.getDateTime(new Date()));
                                    sendMessage("result length: " + DateUtil.getDateTime(new Date())
                                            + ", " + "\n" + retLen + "\n"
                                            + BytesUtil.bytes2HexString(result));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    break;
                case R.id.btnWrite:
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                byte[] writeBuff = "12345678".getBytes();
                                String cmd = editText_cmd.getText().toString().trim();
                                if (!TextUtils.isEmpty(cmd)) {
                                    writeBuff = BytesUtil.hexString2Bytes(cmd);
                                }
                                int timeout = 20 * 1000;
                                sendMessage("write: " + BytesUtil.bytes2HexString(writeBuff)
                                        + ", " + DateUtil.getDateTime(new Date()));
                                int writedLenth = mSerialPortDriverImpl.write(writeBuff, timeout);
                                if (writedLenth < 0) {
                                    sendMessage("write failed: " + writedLenth);
                                } else {
                                    byte[] writeBuff2 = new byte[writedLenth];
                                    System.arraycopy(writeBuff, 0, writeBuff2, 0, writedLenth);
                                    sendMessage("write success: " + DateUtil.getDateTime(new Date()));
                                    sendMessage("write length： " + writedLenth + "\n"
                                            + new String(writeBuff2));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                ;
                            }
                        }
                    }.start();
                    break;
                case R.id.btnIsBufferIsEmpty:
                    sendMessage("isBufferEmpty(in)");
                    status = mSerialPortDriverImpl.isBufferEmpty(true);
                    sendMessage("" + status);
                    sendMessage("isBufferEmpty(out)");
                    status = mSerialPortDriverImpl.isBufferEmpty(false);
                    sendMessage("" + status);
                    break;
                case R.id.btnClearInputBuffer:
                    sendMessage("clearInputBuffer");
                    boolean status2 = mSerialPortDriverImpl.clearInputBuffer();
                    sendMessage("" + status2);
                    break;
                case R.id.btnClose:
                    sendMessage("close");
                    boolean status3 = mSerialPortDriverImpl.close();
                    sendMessage("" + status3);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public void sendMessage(String str) {

        Message message = new Message();
        message.what = MESSAGE_MSG;
        message.obj = "" + str;
        mHandler.sendMessage(message);
    }

}
