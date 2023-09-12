package com.urovo.sdk.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.urovo.sdk.R;
import com.urovo.sdk.insertcard.InsertCardHandlerImpl;
import com.urovo.sdk.insertcard.utils.Constant;
import com.urovo.sdk.utils.BytesUtil;

public class ICReaderActivity extends BaseActivity implements View.OnClickListener {

    private InsertCardHandlerImpl icReader;
    private EditText editText_apdu;
    private byte cardType = Constant.Mode.MODE_USER;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icreader);
        initView();
        icReader = InsertCardHandlerImpl.getInstance();
        editText_apdu = (EditText) findViewById(R.id.editText_apdu);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radio1:
                        cardType = 0;
                        break;
                    case R.id.radio2:
                        cardType = 1;
                        break;
                    case R.id.radio3:
                        cardType = 2;
                        break;
                }
            }
        });
        button_clearAPDU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_apdu.setText("");
            }
        });
    }

    @Override
    public void onClick(View view) {
        try {
            boolean status = false;
            switch (view.getId()) {
                case R.id.btnpowerUp:
                    outputText("powerUp");
                    byte[] atrData = new byte[64];
                    int ret = icReader.powerUp(cardType, atrData);
                    outputText("result:" + (ret > 0));
                    if (ret > 0) {
                        byte[] dataOut = new byte[ret];
                        System.arraycopy(atrData, 0, dataOut, 0, ret);
                        outputText("ATR:" + BytesUtil.bytes2HexString(dataOut));
                    }
                    break;
                case R.id.btnpowerDown:
                    outputText("powerDown");
                    status = icReader.powerDown(cardType);
                    outputText("" + status);
                    break;
                case R.id.btnIsCardIn:
                    outputText("isCardIn");
                    status = icReader.isCardIn();
                    outputText("" + status);
                    break;
                case R.id.btnIsPsamCardIn:
                    outputText("isPSAMCardExists");
                    status = icReader.isPSAMCardExists(cardType);
                    outputText("" + status);
                    break;
                case R.id.btnExchangeApdu:
                    outputText("exchangeApdu");
                    String apduStr = editText_apdu.getText().toString();
                    byte[] cmdHead = new byte[]{0x00, (byte) 0xa4, 0x04, 0x00, 0x0e};
                    byte[] fileName = "1PAY.SYS.DDF01".getBytes();
                    byte[] apdu = new byte[cmdHead.length + fileName.length];
                    System.arraycopy(cmdHead, 0, apdu, 0, cmdHead.length);
                    System.arraycopy(fileName, 0, apdu, cmdHead.length, fileName.length);
                    if (cardType != Constant.Mode.MODE_USER) {
                        apdu = BytesUtil.hexString2Bytes("0084000004");
                    }
                    if (!TextUtils.isEmpty(apduStr)) {
                        apdu = BytesUtil.hexString2Bytes(apduStr);
                    }
                    long startTime = System.currentTimeMillis();
                    byte[] rspData = icReader.exchangeApdu(cardType, apdu);
                    long endTime = System.currentTimeMillis();
                    outputText("time:" + (endTime - startTime) + "\nAPDU result:" + BytesUtil.bytes2HexString(rspData));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
