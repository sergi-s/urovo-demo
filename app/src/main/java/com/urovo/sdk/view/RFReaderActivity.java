package com.urovo.sdk.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.urovo.sdk.R;
import com.urovo.sdk.rfcard.RFCardHandlerImpl;
import com.urovo.sdk.rfcard.listener.RFSearchListener;
import com.urovo.sdk.rfcard.utils.Constant;
import com.urovo.sdk.utils.BytesUtil;

import android.nfc.NfcAdapter;
import android.nfc.tech.MifareUltralight;
import android.nfc.Tag;
import android.nfc.tech.NfcA;

import android.os.Parcelable;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.app.PendingIntent;
import android.os.RemoteException;

import java.nio.charset.Charset;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RFReaderActivity extends BaseActivity implements View.OnClickListener {

    private String driver = "";
    private String content = "";
    private EditText editText_block;
    private EditText editText_apdu;
    private RadioGroup radioGroup;

    private int block = 1;
    private int M1KeyType = 0; // 0:KEY_A, 1:KEY_B
    public RFCardHandlerImpl rfReader;

    private PendingIntent pendingIntent;
    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfreader);
        initView();
        rfReader = RFCardHandlerImpl.getInstance();
        editText_block = (EditText) findViewById(R.id.editText_block);
        editText_apdu = (EditText) findViewById(R.id.editText_apdu);

        button_clearAPDU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_apdu.setText("");
            }
        });
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radio1:
                        M1KeyType = 0;
                        break;
                    case R.id.radio2:
                        M1KeyType = 1;
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        boolean status = false;
        int iRet = -1;
        try {
            switch (view.getId()) {
                case R.id.btnsearchCard:
                    outputText("searchCard");
                    startSearchCard();
                    break;
                case R.id.btnstopCard:
                    outputText("stopSearch");
                    rfReader.stopSearch();
                    break;
                case R.id.btnactivate:
                    outputText("activate");
                    byte[] responseData = new byte[20];
                    int ret = rfReader.activate(driver, responseData);
                    outputText("result:" + (ret > 0));
                    if (ret > 0) {
                        byte[] dataOut = new byte[ret];
                        System.arraycopy(responseData, 0, dataOut, 0, ret);
                        outputText("ATR: " + BytesUtil.bytes2HexString(dataOut));
                    }
                    break;
                case R.id.btnhalt:
                    outputText("halt");
                    rfReader.halt();
                    break;
                case R.id.btnIsExist:
                    outputText("isExist");
                    status = rfReader.isExist();
                    outputText("" + status);
                    break;
                case R.id.btnexchangeApdu:
                    apduComm();
                    break;
                case R.id.btnauthBlock:
                    if (TextUtils.isEmpty(editText_block.getText().toString())) {
                        showMessage("enter block no first");
                        return;
                    }
                    block = Integer.parseInt(editText_block.getText().toString());
                    outputText("authBlock,key type: " + (M1KeyType == 0 ? "key_A(0)" : "key_B(1)"));
                    byte[] key = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                            (byte) 0xFF };
                    String passwordStr = editText_apdu.getText().toString().trim();
                    if (!TextUtils.isEmpty(passwordStr)) {
                        key = BytesUtil.hexString2Bytes(passwordStr);
                    }
                    int ret2 = rfReader.authBlock(block,
                            M1KeyType == 0 ? Constant.KeyType.KEY_A : Constant.KeyType.KEY_B, key);
                    if (ret2 == 0) {
                        outputText("auth success: " + ret2);
                    } else {
                        outputText("auth failed: " + ret2);
                    }
                    break;
                case R.id.btnreadBlock:
                    if (TextUtils.isEmpty(editText_block.getText().toString())) {
                        showMessage("enter block no first");
                        return;
                    }
                    block = Integer.parseInt(editText_block.getText().toString());
                    outputText("readBlock");
                    byte[] response = new byte[16];
                    int ret3 = rfReader.readBlock(block, response);
                    if (ret3 < 0) {
                        outputText("readBlock failed: " + ret3);
                    } else {
                        byte[] result = new byte[ret3];
                        System.arraycopy(response, 0, result, 0, ret3);
                        // outputText("\response: " + response);
                        // outputText("readBlock success: " + ret3 + ",\nresult: " + BytesUtil.bytes2HexString(result));
                        outputText("readBlock success: " + ret3 + ",\nresult: " + new String(response, StandardCharsets.UTF_8));
                    }
                    break;
                case R.id.btnreadAllBlocks:
                    readAllBlocks();
                    break;
                case R.id.btnwriteBlock:
                    if (TextUtils.isEmpty(editText_block.getText().toString())) {
                        showMessage("enter sector no first!");
                        return;
                    }
                    block = Integer.parseInt(editText_block.getText().toString());
                    byte[] writeData = BytesUtil.hexString2Bytes("9999");
                    String apduStr = editText_apdu.getText().toString().trim();
                    if (!TextUtils.isEmpty(apduStr)) {
                        writeData = BytesUtil
                                .hexString2Bytes(apduStr.length() > 32 ? apduStr.substring(0, 32) : apduStr);
                    }
                    outputText("write data: " + BytesUtil.bytes2HexString(writeData));
                    int ret4 = rfReader.writeBlock(block, writeData);
                    if (ret4 != 0) {
                        outputText("writeBlock failed: " + ret4);
                    } else {
                        outputText("writeBlock success: " + ret4);
                    }
                    break;
                case R.id.btnM1Init:
                    if (TextUtils.isEmpty(editText_block.getText().toString())) {
                        showMessage("enter block no first");
                        return;
                    }
                    block = Integer.parseInt(editText_block.getText().toString());
                    outputText("m1_amount_init");
                    iRet = rfReader.m1_amount_init(block, 0);
                    outputText("result: " + iRet);
                    break;
                case R.id.btnM1Amount:
                    if (TextUtils.isEmpty(editText_block.getText().toString())) {
                        showMessage("enter block no first");
                        return;
                    }
                    block = Integer.parseInt(editText_block.getText().toString());
                    outputText("m1_amount_read");
                    iRet = rfReader.m1_amount_read(block);
                    outputText("result: " + iRet);
                    break;
                case R.id.btnrestore:
                    if (TextUtils.isEmpty(editText_block.getText().toString())) {
                        showMessage("enter block no first");
                        return;
                    }
                    block = Integer.parseInt(editText_block.getText().toString());
                    outputText("m1_amount_restore");
                    iRet = rfReader.m1_amount_restore(block);
                    outputText("result: " + iRet);
                    break;
                case R.id.btntransfer:
                    if (TextUtils.isEmpty(editText_block.getText().toString())) {
                        showMessage("enter block no first");
                        return;
                    }
                    block = Integer.parseInt(editText_block.getText().toString());
                    outputText("m1_amount_transfer");
                    iRet = rfReader.m1_amount_transfer(block);
                    outputText("result: " + iRet);
                    break;
                case R.id.btnincreaseValue:
                    if (TextUtils.isEmpty(editText_block.getText().toString())) {
                        showMessage("enter block no first");
                        return;
                    }
                    block = Integer.parseInt(editText_block.getText().toString());
                    outputText("increaseValue,value: 1");
                    iRet = rfReader.increaseValue(block, 1);
                    outputText("result: " + iRet);
                    break;
                case R.id.btndecreaseValue:
                    if (TextUtils.isEmpty(editText_block.getText().toString())) {
                        showMessage("enter block no first");
                        return;
                    }
                    int value = 1;
                    block = Integer.parseInt(editText_block.getText().toString());
                    outputText("decreaseValue, value: 1");
                    iRet = rfReader.decreaseValue(block, 1);
                    outputText("result: " + iRet);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startSearchCard() {
        try {
            rfReader.searchCard(new RFSearchListener() {
                /**
                 * 检测到卡
                 *
                 * @param cardType - 卡类型
                 *                 <ul>
                 *                 <li>S50_CARD(0x00) - S50卡</li>
                 *                 <li>S70_CARD(0x01) - S70卡</li>
                 *                 <li>PRO_CARD(0x02) - PRO卡</li>
                 *                 <li>S50_PRO_CARD(0x03) - 支持S50驱动与PRO驱动的PRO卡</li>
                 *                 <li>S70_PRO_CARD(0x04) - 支持S70驱动与PRO驱动的PRO卡</li>
                 *                 <li>CPU_CARD(0x05) - CPU卡</li>
                 *                 </ul>
                 * @param UID
                 */
                @Override
                public void onCardPass(int cardType, byte[] UID) {
                    outputText("onCardPass, cardType " + cardType);
                    driver = "CPU";
                    if (cardType == Constant.CardType.PRO_CARD || cardType == Constant.CardType.S50_PRO_CARD
                            || cardType == Constant.CardType.S70_PRO_CARD) {
                        driver = "PRO";
                    } else if (cardType == Constant.CardType.S50_CARD) {
                        driver = "S50";
                    } else if (cardType == Constant.CardType.S70_CARD) {
                        driver = "S70";
                    }
                    outputText("" + driver);
                    outputText("UID: " + BytesUtil.bytes2HexString(UID));
                }

                @Override
                public void onFail(int error, String message) {
                    outputText("onFail,error: " + error + ",message: " + message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void apduComm() {
        try {
            String apduStr = editText_apdu.getText().toString().trim();
            byte[] cmd = null;
            if (!TextUtils.isEmpty(apduStr)) {
                cmd = BytesUtil.hexString2Bytes(apduStr);
            } else {
                String APDUStr = "00A404000E315041592E5359532E444446303100";
                cmd = BytesUtil.hexString2Bytes(APDUStr);
            }
            outputText("APDU: " + BytesUtil.bytes2HexString(cmd));
            long startTime = System.currentTimeMillis();
            byte[] rspData = rfReader.exchangeApdu(cmd);
            long endTime = System.currentTimeMillis();
            if (rspData != null && rspData.length >= 0) {
                outputText("APDU result: " + BytesUtil.bytes2HexString(rspData) + ", \n time:" + (endTime - startTime));
            } else {
                outputText("APDU failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readAllBlocks() {

        // int blockNumber = Integer.parseInt(editText_block.getText().toString());
        StringBuilder resultMessage = new StringBuilder();

        for (int block = 0; block <= 64; block++) {
            // outputText("Reading Block " + block);
            byte[] response = new byte[16];

            try {
                int ret3 = rfReader.readBlock(block, response);

                if (ret3 < 0) {
                    // outputText("Read Block " + block + " failed: " + ret3);
                } else {
                    byte[] result = new byte[ret3];
                    System.out.println(response);
                    System.arraycopy(response, 0, result, 0, ret3);
                    content += (result);
                    // resultMessage.append("Block ").append(block).append(":\n");
                    // resultMessage.append("Read success: ").append(ret3).append(",\n");
                    resultMessage.append("=> " + block).append(new String(response, StandardCharsets.UTF_8))
                            .append("\n");
                    // System.out.println(result);
                    // System.out.println(BytesUtil.bytes2HexString(result));
                    // resultMessage.append(BytesUtil.bytes2HexString(result)).append("\n");
                }
            } catch (RemoteException e) {
                // Handle the RemoteException, e.g., log it or show an error message
                e.printStackTrace();
            }
        }
        System.out.println("asdasdasdasdasdasda");
        System.out.println("content=> " + content);

        // Display the result message in your UI or output it as needed
        outputText(resultMessage.toString());
    }

}
