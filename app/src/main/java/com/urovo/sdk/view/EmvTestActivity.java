package com.urovo.sdk.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.urovo.i9000s.api.emv.ContantPara;
import com.urovo.i9000s.api.emv.EmvListener;
import com.urovo.i9000s.api.emv.EmvNfcKernelApi;
import com.urovo.sdk.R;

import java.util.ArrayList;
import java.util.Hashtable;

public class EmvTestActivity extends BaseActivity {

    public static final String TAG = "EmvTestActivity";
    private Button button_emv_start;
    public EmvNfcKernelApi mEmvNfcKernelApi = null;
    public static EmvListener mEmvListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emv_test);
        initView();

        mEmvNfcKernelApi = EmvNfcKernelApi.getInstance();
        mEmvListener = new MyEmvListener();
        mEmvNfcKernelApi.setListener(mEmvListener);
        mEmvNfcKernelApi.setContext(EmvTestActivity.this);
        //0-disable kernel log  1-enable kernel log
        mEmvNfcKernelApi.LogOutEnable(0);//contactless: adb pull /sdcard/UROPE/TraceCL.txt D:\log\   export log
        //eg: contact:adb pull /sdcard/UROPE/Trace.txt D:\log

        button_emv_start = (Button) findViewById(R.id.btn_emv_start);
        button_emv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readCardTime_End = 0;
                readCardTime = 0;
                PAN = "";
                outputText("\n读卡开始");
                readCardTime_Start = System.currentTimeMillis();
                StartKernel(ContantPara.CheckCardMode.TAP);
            }
        });
    }

    public void StartKernel(final ContantPara.CheckCardMode checkCardMode) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Hashtable<String, Object> data = new Hashtable<String, Object>();
                    data.put("checkCardMode", checkCardMode);//
                    data.put("currencyCode", "682");//682
                    data.put("emvOption", ContantPara.EmvOption.START);  // START_WITH_FORCE_ONLINE
                    data.put("amount", "0.01");
                    data.put("cashbackAmount", "0");
                    data.put("checkCardTimeout", "30");// Check Card time out .Second
                    data.put("transactionType", "00"); //00-goods 01-cash 09-cashback 20-refund
                    data.put("isEnterAmtAfterReadRecord", false);
                    data.put("FallbackSwitch", "0");//0- close fallback 1-open fallback
                    data.put("supportDRL", true); // support Visa DRL?
                    mEmvNfcKernelApi.startKernel(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public long readCardTime_Start = 0;
    public long readCardTime_End = 0;
    public long readCardTime = 0;
    public String PAN = "";
    public static final int MESSAGE_CARD_MSG = 0x02;
    public static final int MESSAGE_CARD_ICC = 0x03;
    public static final int MESSAGE_CARD_PICC = 0x04;
    public static final int MESSAGE_onRequestOnlineProcess = 0x14;
    public static final int MESSAGE_onReturnTransactionResult = 0x16;
    public static final int MESSAGE_onNFCrequestOnline = 0x22;
    public static final int MESSAGE_onNFCTransResult = 0x24;
    public static final int MESSAGE_onNFCErrorInfor = 0x25;
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_CARD_MSG:
                    readCardTime_End = System.currentTimeMillis();
                    readCardTime = readCardTime_End - readCardTime_Start;
                    outputText("读卡结束\n读卡时间：" + readCardTime + "ms");
                    Hashtable<String, String> hashtable = (Hashtable<String, String>) msg.obj;
                    PAN = hashtable.get("CardNo");
                    outputText("卡号：" + PAN);
                    break;
                case MESSAGE_onRequestOnlineProcess:
                case MESSAGE_onNFCrequestOnline:
                    readCardTime_End = System.currentTimeMillis();
                    readCardTime = readCardTime_End - readCardTime_Start;
                    outputText("读卡结束\n读卡时间：" + readCardTime + "ms");
                    String track2 = mEmvNfcKernelApi.getValByTag(0x57).toUpperCase();
                    if (!TextUtils.isEmpty(track2)) {
                        track2 = track2.replace("D", "=");
                        int index = track2.indexOf("=");
                        if (index != -1) {
                            PAN = track2.substring(0, index);
                        }
                    }
                    outputText("卡号：" + PAN);
                    break;
                case MESSAGE_onReturnTransactionResult:
                case MESSAGE_onNFCTransResult:
                case MESSAGE_onNFCErrorInfor:
                    String errMessage = (String) msg.obj;
                    outputText("读卡失败：" + errMessage);
                    break;
            }
        }
    };

    class MyEmvListener implements EmvListener {

        @Override
        public void onRequestSetAmount() {
//            Log.e(TAG, TAG + "===onRequestSetAmount");
            mEmvNfcKernelApi.setAmountEx(1L, 0L);
        }

        @Override
        public void onReturnCheckCardResult(ContantPara.CheckCardResult checkCardResult, Hashtable<String, String> hashtable) {
//            Log.e(TAG, TAG + "===onReturnCheckCardResult, checkCardResult:" + checkCardResult + ", hashtable:" + hashtable.toString());
//            Log.e(TAG, TAG + "===POS Entry Mode:" + hashtable.get("POSEntryMode"));
            if (checkCardResult == ContantPara.CheckCardResult.MSR) {
                sendHandlerMessage(MESSAGE_CARD_MSG, "");
            } else if (checkCardResult == ContantPara.CheckCardResult.INSERTED_CARD) {
                sendHandlerMessage(MESSAGE_CARD_ICC, "");
            } else if (checkCardResult == ContantPara.CheckCardResult.TAP_CARD_DETECTED) {
                sendHandlerMessage(MESSAGE_CARD_PICC, "");
            } else {
                sendHandlerMessage(MESSAGE_CARD_PICC, checkCardResult + "");
            }
        }

        @Override
        public void onRequestSelectApplication(ArrayList<String> arrayList) {
//            Log.e(TAG, TAG + "===onRequestSelectApplication, arrayList:" + arrayList.toString());
        }

        @Override
        public void onRequestPinEntry(ContantPara.PinEntrySource pinEntrySource) {
//            Log.e(TAG, TAG + "===onRequestPinEntry, pinEntrySource:" + pinEntrySource);
            mEmvNfcKernelApi.sendPinEntry();
        }

        @Override
        public void onRequestOfflinePinEntry(ContantPara.PinEntrySource pinEntrySource, int i) {
//            Log.e(TAG, TAG + "===onRequestOfflinePinEntry, pinEntrySource:" + pinEntrySource + ", i:" + i);
        }

        @Override
        public void onRequestConfirmCardno() {
//            Log.e(TAG, TAG + "===onRequestConfirmCardno");
            mEmvNfcKernelApi.sendConfirmCardnoResult(true);
        }

        @Override
        public void onRequestFinalConfirm() {
//            Log.e(TAG, TAG + "===onRequestFinalConfirm");
            mEmvNfcKernelApi.sendFinalConfirmResult(true);
        }

        @Override
        public void onRequestOnlineProcess(String cardTlvData, String dataKsn) {
//            Log.e(TAG, TAG + "===onRequestOnlineProcess, cardTlvData:" + cardTlvData + ", dataKsn:" + dataKsn);
            sendHandlerMessage(MESSAGE_onRequestOnlineProcess, "");
        }

        @Override
        public void onReturnBatchData(String s) {
//            Log.e(TAG, TAG + "===onReturnBatchData, s:" + s);
        }

        @Override
        public void onReturnTransactionResult(ContantPara.TransactionResult transactionResult) {
//            Log.e(TAG, TAG + "===onReturnTransactionResult, transactionResult:" + transactionResult);
            if (transactionResult == ContantPara.TransactionResult.ONLINE_APPROVAL
                    || transactionResult == ContantPara.TransactionResult.OFFLINE_APPROVAL
            ) {
                sendHandlerMessage(MESSAGE_onRequestOnlineProcess, "");
            } else {
                sendHandlerMessage(MESSAGE_onRequestOnlineProcess, "" + transactionResult);
            }
        }

        @Override
        public void onRequestDisplayText(ContantPara.DisplayText displayText) {
//            Log.e(TAG, TAG + "===onRequestDisplayText, displayText:" + displayText);
        }

        @Override
        public void onRequestOfflinePINVerify(ContantPara.PinEntrySource pinEntrySource, int pinEntryType, Bundle bundle) {
//            Log.e(TAG, TAG + "===onRequestOfflinePINVerify, pinEntrySource:" + pinEntrySource + ", pinEntryType:" + pinEntryType);
        }

        @Override
        public void onReturnIssuerScriptResult(ContantPara.IssuerScriptResult issuerScriptResult, String s) {
//            Log.e(TAG, TAG + "===onReturnIssuerScriptResult, issuerScriptResult:" + issuerScriptResult + ", s:" + s);
        }

        @Override
        public void onNFCrequestTipsConfirm(ContantPara.NfcTipMessageID nfcTipMessageID, String s) {
//            Log.e(TAG, TAG + "===onNFCrequestTipsConfirm, nfcTipMessageID:" + nfcTipMessageID + ", s:" + s);
        }

        @Override
        public void onReturnNfcCardData(Hashtable<String, String> hashtable) {
//            Log.e(TAG, TAG + "===onReturnNfcCardData, hashtable:" + hashtable.toString());
        }

        @Override
        public void onNFCrequestOnline() {
//            Log.e(TAG, TAG + "===onNFCrequestOnline");
            PAN = mEmvNfcKernelApi.getValByTag(0x57);
            sendHandlerMessage(MESSAGE_onNFCrequestOnline, "");
        }

        @Override
        public void onNFCrequestImportPin(int type, int lasttimeFlag, String amt) {
//            Log.e(TAG, TAG + "===onNFCrequestImportPin, type:" + type + ", lasttimeFlag:" + lasttimeFlag + ", amt:" + amt);
            mEmvNfcKernelApi.sendPinEntry();
        }

        @Override
        public void onNFCTransResult(ContantPara.NfcTransResult nfcTransResult) {
//            Log.e(TAG, TAG + "===onNFCTransResult, nfcTransResult:" + nfcTransResult);
            if (nfcTransResult == ContantPara.NfcTransResult.ONLINE_APPROVAL
                    || nfcTransResult == ContantPara.NfcTransResult.OFFLINE_APPROVAL) {
                PAN = mEmvNfcKernelApi.getValByTag(0x57);
                sendHandlerMessage(MESSAGE_onNFCrequestOnline, "");
            } else {
                sendHandlerMessage(MESSAGE_onNFCTransResult, "" + nfcTransResult);
            }
        }

        @Override
        public void onNFCErrorInfor(ContantPara.NfcErrMessageID nfcErrMessageID, String errorStr) {
//            Log.e(TAG, TAG + "===onNFCErrorInfor, nfcErrMessageID:" + nfcErrMessageID + ", errorStr:" + errorStr);
            sendHandlerMessage(MESSAGE_onNFCErrorInfor, "" + errorStr);
        }
    }

    public void sendHandlerMessage(int what, Object object) {
        Message message = mHandler.obtainMessage(what);
        message.obj = object;
        mHandler.sendMessage(message);
    }


}
