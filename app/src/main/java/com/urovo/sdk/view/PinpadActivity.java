package com.urovo.sdk.view;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.urovo.sdk.MainActivity;
import com.urovo.sdk.R;
import com.urovo.sdk.pinpad.PinPadProviderImpl;
import com.urovo.sdk.pinpad.listener.PinInputListener;
import com.urovo.sdk.pinpad.utils.Constant;
import com.urovo.sdk.pinpad.utils._3DES;
import com.urovo.sdk.utils.BytesUtil;
import com.urovo.sdk.utils.PinpadUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class PinpadActivity extends BaseActivity implements OnClickListener {

    public final static int INDEX_TEK = 9;
    public final static int INDEX_MK = 10;
    public final static int INDEX_WK = 10;
    public final static int INDEX_DUKPT_MSR = 1;
    public final static int INDEX_DUKPT_EMV = 2;
    public final static int INDEX_DUKPT_PIN = 3;
    public final static int INDEX_DUKPT_MAC = 4;

    private String macStr =
            "1200721405D820C0820116986009010120744800000000000001000020211104115855211104115855241200000101100020015065999211101001379860090101207448D24122011374015900000012733370041988888888028602869F2608FF852238242376749F2701809F10120114A74003020000000000000000000000FF9F370478D842739F360201C7950500800080009A032111049C01009F02060000000100005F2A020860820239009F1A0208609F03060000000000009F3303E0F0C89F34034403029F3501229F1E0830303030303030308407A08600010000019F090200209F41040000000443D0964F00000000";
    private String TEKeyData =
            "00000000000000000000000000000000";
    private String mainKeyData =
            "11111111111111111111111111111111";
    private String mainKeyEncData =
            "89B07B35A1B3F47E89B07B35A1B3F47E";
    private String mainKeyCheckValue =
            "82E13665B4624DF5";
    private String pinKeyData =
            "950973182317F80B950973182317F80B";//clear key: 22222222222222222222222222222222
    private String pinKeyCheckValue =
            "00962B60AA556E65";
    private String macKeyData =
            "F679786E2411E3DEA0C45C59F1E549BB";//33333333333333334444444444444444
    private String macKeyCheckValue =
            "E18DE25ECBCF1591";
    private String tdKeyData =
            "A0C45C59F1E549BBA0C45C59F1E549BB";//clear key: 44444444444444444444444444444444
    private String tdkKeyCheckValue =
            "E2F2434039AA34AC";

    static String pan = "6214837803398183";
    static String pinKey = "22222222222222222222222222222222";

    private PinPadProviderImpl pinpad;

    String model = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinpad);
        initView();
        pinpad = PinPadProviderImpl.getInstance();
        model = Build.MODEL.toUpperCase();
        Log.e(MainActivity.TAG, "model:" + model);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        try {
            boolean succ = false;
            int ret = -1;
            switch (v.getId()) {
                case R.id.button_deleteKey:
                    ret = pinpad.deleteKey(Constant.KeyType.MAIN_KEY, INDEX_TEK);
                    outputColorText(TextColor.BLUE, "deleteKey(TEK): " + ret);
                    ret = pinpad.deleteKey(Constant.KeyType.MAIN_KEY, INDEX_MK);
                    outputColorText(TextColor.BLUE, "deleteKey(MK): " + ret);
                    ret = pinpad.deleteKey(Constant.KeyType.PIN_KEY, INDEX_WK);
                    outputColorText(TextColor.BLUE, "deleteKey(PIN): " + ret);
                    ret = pinpad.deleteKey(Constant.KeyType.MAC_KEY, INDEX_WK);
                    outputColorText(TextColor.BLUE, "deleteKey(MAC): " + ret);
                    ret = pinpad.deleteKey(Constant.KeyType.ENCDEC_KEY, INDEX_WK);
                    outputColorText(TextColor.BLUE, "deleteKey(ENC): " + ret);
                    break;
                case R.id.button_isKeyExist:
                    outputColorText(TextColor.BLUE, "------------------------------------------------");
                    succ = pinpad.isKeyExist(Constant.KeyType.MAIN_KEY, 20);
                    outputColorText(TextColor.BLUE, "isKeyExist(TEK)(keyIndex=20): " + succ);
                    succ = pinpad.isKeyExist(Constant.KeyType.MAIN_KEY, 20);
                    outputColorText(TextColor.BLUE, "isKeyExist(MK)(keyIndex=20): " + succ);
                    succ = pinpad.isKeyExist(Constant.KeyType.MAC_KEY, 20);
                    outputColorText(TextColor.BLUE, "isKeyExist(MAC)(keyIndex=20): " + succ);
                    succ = pinpad.isKeyExist(Constant.KeyType.PIN_KEY, 20);
                    outputColorText(TextColor.BLUE, "isKeyExist(PIN)(keyIndex=20): " + succ);
                    succ = pinpad.isKeyExist(Constant.KeyType.ENCDEC_KEY, 20);
                    outputColorText(TextColor.BLUE, "isKeyExist(ENC)(keyIndex=20): " + succ);

                    outputColorText(TextColor.BLUE, "------------------------------------------------");

                    succ = pinpad.isKeyExist(Constant.KeyType.MAIN_KEY, INDEX_TEK);
                    outputColorText(TextColor.BLUE, "isKeyExist(TEK)(keyIndex=9): " + succ);
                    succ = pinpad.isKeyExist(Constant.KeyType.MAIN_KEY, INDEX_MK);
                    outputColorText(TextColor.BLUE, "isKeyExist(MK)(keyIndex=10): " + succ);
                    succ = pinpad.isKeyExist(Constant.KeyType.MAC_KEY, INDEX_WK);
                    outputColorText(TextColor.BLUE, "isKeyExist(MAC)(keyIndex=10): " + succ);
                    succ = pinpad.isKeyExist(Constant.KeyType.PIN_KEY, INDEX_WK);
                    outputColorText(TextColor.BLUE, "isKeyExist(PIN)(keyIndex=10): " + succ);
                    succ = pinpad.isKeyExist(Constant.KeyType.ENCDEC_KEY, INDEX_WK);
                    outputColorText(TextColor.BLUE, "isKeyExist(ENC)(keyIndex=10): " + succ);
                    break;
                case R.id.button_loadTEK:
                    succ = pinpad.loadTEK(INDEX_TEK, BytesUtil.hexString2Bytes(TEKeyData), null);
                    outputColorText(TextColor.BLUE, "loadTEK: " + succ);
                    break;
                case R.id.button_loadMainKeyEnc:
                    succ = pinpad.loadEncryptMainKey(INDEX_TEK, INDEX_MK, BytesUtil.hexString2Bytes(mainKeyEncData), BytesUtil.hexString2Bytes(mainKeyCheckValue));
                    outputColorText(TextColor.BLUE, "loadEncryptMainKey(KCV IS NOT NULL): " + succ);
                    break;
                case R.id.button_loadMainKey:
                    succ = pinpad.loadMainKey(INDEX_MK, BytesUtil.hexString2Bytes(mainKeyData), BytesUtil.hexString2Bytes(mainKeyCheckValue));
                    outputColorText(TextColor.BLUE, "loadMainKey(KCV IS NULL): " + succ);
                    break;
                case R.id.button_loadWorkKey:
                    succ = pinpad.loadWorkKey(Constant.KeyType.MAC_KEY, INDEX_MK, INDEX_WK, BytesUtil.hexString2Bytes(macKeyData), BytesUtil.hexString2Bytes(macKeyCheckValue));
                    outputColorText(TextColor.BLUE, "load MAC Key(KCV IS NOT NULL): " + succ);
                    succ = pinpad.loadWorkKey(Constant.KeyType.PIN_KEY, INDEX_MK, INDEX_WK, BytesUtil.hexString2Bytes(pinKeyData), BytesUtil.hexString2Bytes(pinKeyCheckValue));
                    outputColorText(TextColor.BLUE, "load PIN Key(KCV IS NOT NULL): " + succ);
                    succ = pinpad.loadWorkKey(Constant.KeyType.ENCDEC_KEY, INDEX_MK, INDEX_WK, BytesUtil.hexString2Bytes(tdKeyData), BytesUtil.hexString2Bytes(tdkKeyCheckValue));
                    outputColorText(TextColor.BLUE, "load ENC Key(KCV IS NOT NULL): " + succ);
                    break;
                case R.id.button_calcMAC:
                    byte[] macSource = BytesUtil.hexString2Bytes(macStr);
                    byte[] out = pinpad.calcMAC(INDEX_WK, macSource, 0x10);
                    outputColorText(TextColor.BLUE, "calcMAC X9.9: " + BytesUtil.bytes2HexString(out) + "\n"
                            + "expected result: 502A20C53785A8FB");

                    out = pinpad.calcMAC(INDEX_WK, macSource, 0x11);
                    outputColorText(TextColor.BLUE, "\ncalcMAC X9.19: " + BytesUtil.bytes2HexString(out) + "\n"
                            + "expected result: DD0106290E3A4B08");
                    break;
                case R.id.button_encryptData:
                    outputColorText(TextColor.RED, "ECB");
                    outputColorText(TextColor.RED, "encryption");
                    byte[] input = "621996044447640027D0506101152641".getBytes();//4193980301050011d270922110000933001f
                    byte[] out2 = new byte[input.length];
                    ret = pinpad.calculateDes(Constant.DesMode.ENC, Constant.Algorithm.ECB, Constant.KeyType.ENCDEC_KEY, INDEX_WK, input, out2);
                    outputColorText(TextColor.RED, "ret:" + ret);
                    if (ret == 0) {
                        outputText("response result: " + BytesUtil.bytes2HexString(out2) + "\n"
                                + "expected result: \n1F2570DB45E40261D323ADA0FB83DB870787547AACCFEBB7257C76AF088733DF");
                    }
                    outputColorText(TextColor.RED, "decryption");
                    byte[] encryptionData = BytesUtil.hexString2Bytes("1F2570DB45E40261D323ADA0FB83DB870787547AACCFEBB7257C76AF088733DF");
                    ret = pinpad.calculateDes(Constant.DesMode.DEC, Constant.Algorithm.ECB, Constant.KeyType.ENCDEC_KEY, INDEX_WK, encryptionData, out2);
                    outputColorText(TextColor.RED, "ret:" + ret);
                    if (ret == 0) {
                        outputText("response result: " + BytesUtil.bytes2HexString(out2) + "\n"
                                + "expected result: \n3632313939363034343434373634303032374430353036313031313532363431");
                    }

                    outputColorText(TextColor.RED, "CBC");
                    outputColorText(TextColor.RED, "encryption");
                    out2 = new byte[input.length];
                    ret = pinpad.calculateDes(Constant.DesMode.ENC, Constant.Algorithm.CBC, Constant.KeyType.ENCDEC_KEY, INDEX_WK, input, out2);
                    outputColorText(TextColor.RED, "ret:" + ret);
                    if (ret == 0) {
                        outputText("response result: " + BytesUtil.bytes2HexString(out2) + "\n"
                                + "expected result: \n1F2570DB45E4026190C86844CB06EAA0A5F0C6845C0E0873DA31C229AE3D5FE4");
                    }
                    outputColorText(TextColor.RED, "decryption");
                    encryptionData = BytesUtil.hexString2Bytes("1F2570DB45E4026190C86844CB06EAA0A5F0C6845C0E0873DA31C229AE3D5FE4");
                    ret = pinpad.calculateDes(Constant.DesMode.DEC, Constant.Algorithm.CBC, Constant.KeyType.ENCDEC_KEY, INDEX_WK, encryptionData, out2);
                    outputColorText(TextColor.RED, "ret:" + ret);
                    if (ret == 0) {
                        outputText("response result: " + BytesUtil.bytes2HexString(out2) + "\n"
                                + "expected result: \n3632313939363034343434373634303032374430353036313031313532363431");
                    }
                    break;
                case R.id.button_startPinInput:
                    startInputPin(null, true, INDEX_WK, pinKey, false);
                    break;
                case R.id.button_startPinInput2:
                    startInputPin(null, false, INDEX_WK, pinKey, false);
                    break;
                case R.id.button_diversifiedKey:
                    int status = pinpad.diversifiedKey(INDEX_MK, INDEX_WK, INDEX_WK + 1, pinKey);
                    final String newPinKey = "0F8ADFFB11DC27840F8ADFFB11DC2784";
                    outputText("diversifiedKey: " + status);
                    startInputPin(null, true, INDEX_WK + 1, newPinKey, false);
                    break;
                case R.id.button_genKeyHashValue:
                    byte[] inputData = BytesUtil.hexString2Bytes("18956198561290728915719572156891565189658916589165259681256193565794");
                    byte[] respData = new byte[64];
                    byte[] respLen = new byte[1];
                    ret = pinpad.genKeyHashValue(Constant.KeyType.PIN_KEY, INDEX_WK, inputData, inputData.length, respData, respLen);
                    outputText("genKeyHashValue: " + ret);
                    if (ret == 0) {
                        byte[] result = new byte[respLen[0]];
                        System.arraycopy(respData, 0, result, 0, result.length);
                        outputText("response result: " + BytesUtil.bytes2HexString(result) + "\n"
                                + "expected result: \n" + BytesUtil.generateHash256Value(inputData, pinKey));
                    }
                    break;
                case R.id.downloadKeyDukpt:
                    downloadKeyDukpt();
                    break;
                case R.id.DukptGetKsn:
                    DukptGetKsn();
                    break;
                case R.id.calculateMACOfDUKPTExtend:
                    calculateMACOfDUKPTExtend();
                    break;
                case R.id.button_getRSAPublicKeyModel:
                    byte[] publicKey = new byte[512];
                    int[] publicKeyLen = new int[2];
                    int[] exponent = new int[2];
                    try {
                        succ = pinpad.getRSAPublicKeyModel(publicKey, publicKeyLen, exponent);
                        outputText("getRSAPublicKeyModel, status: " + succ);
                        outputText("getRSAPublicKeyModel, publicKey: " + BytesUtil.bytes2HexString(publicKey));
                        if (succ) {
                            outputText("publicKeyLen[0]: " + publicKeyLen[0] + ", publicKeyLen[1]: " + publicKeyLen[1]);
                            byte[] result = new byte[publicKeyLen[0]];
                            System.arraycopy(publicKey, 0, result, 0, publicKeyLen[0]);
                            outputText("getRSAPublicKeyModel, result: " + BytesUtil.bytes2HexString(result));
                            outputText("exponent[0]: " + exponent[0] + ", exponent[1]: " + exponent[1]);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.button_encryptWithPEK:
                    encryptWithPEK(INDEX_DUKPT_EMV);
                    break;
                case R.id.button_DukptEncryptDataIV:
                    DukptEncryptDataIV(INDEX_DUKPT_MSR);
                    DukptEncryptDataIV_MAC5(INDEX_DUKPT_MAC);
                    break;
                case R.id.GetDukptPinBlock:
                    GetDukptPinBlock();
                    break;
                case R.id.button_customKeyboard1:
                    customKeyBoard1();
                    break;
                case R.id.button_customKeyboard3:
                    if (TextUtils.equals("I2000", model)) {
                        cancelBitmap = getImageFromAssetsFile(PinpadActivity.this, "pinpad_cancel_2.jpg");
                        delBitmap = getImageFromAssetsFile(PinpadActivity.this, "pinpad_del_2.jpg");
                        okBitemap = getImageFromAssetsFile(PinpadActivity.this, "pinpad_ok_2.jpg");
                        backspaceBitemap = getImageFromAssetsFile(PinpadActivity.this, "pinpad_backspace_white.png");
                        strJson = getJson("json_custom3_480x800.json", PinpadActivity.this);
                    } else {
                        cancelBitmap = getImageFromAssetsFile(PinpadActivity.this, "pinpad_cancel_2.jpg");
                        delBitmap = getImageFromAssetsFile(PinpadActivity.this, "pinpad_del_2.jpg");
                        okBitemap = getImageFromAssetsFile(PinpadActivity.this, "pinpad_ok_2.jpg");
                        backspaceBitemap = getImageFromAssetsFile(PinpadActivity.this, "pinpad_backspace_white.png");
                        strJson = getJson("json_custom3_720x1280.json", PinpadActivity.this);
                    }
                    customKeyBoard3JSON();
                    break;
                case R.id.button_customKeyboard4:
                    if (TextUtils.equals("I2000", model)) {
                        cancelBitmap = getImageFromAssetsFile(PinpadActivity.this, "cancel_butt_off_s.png");
                        delBitmap = getImageFromAssetsFile(PinpadActivity.this, "delete_butt_off_s.png");
                        okBitemap = getImageFromAssetsFile(PinpadActivity.this, "ok_butt_off_s.png");
                        backspaceBitemap = getImageFromAssetsFile(PinpadActivity.this, "back_white.png");
                        imageViewBitmap = getImageFromAssetsFile(PinpadActivity.this, "lock_art.png");
                        bodyBitmap = getImageFromAssetsFile(PinpadActivity.this, "bg_480x800.png");
                        keyBitmap = getImageFromAssetsFile(PinpadActivity.this, "bg_keyboard.png");
                        strJson = getJson("json_custom4_480x800.json", PinpadActivity.this);
                    } else {
                        cancelBitmap = getImageFromAssetsFile(PinpadActivity.this, "cancel_butt_off.png");
                        delBitmap = getImageFromAssetsFile(PinpadActivity.this, "delete_butt_off.png");
                        okBitemap = getImageFromAssetsFile(PinpadActivity.this, "ok_butt_off.png");
                        backspaceBitemap = getImageFromAssetsFile(PinpadActivity.this, "back_white.png");
                        imageViewBitmap = getImageFromAssetsFile(PinpadActivity.this, "lock_art.png");
                        bodyBitmap = getImageFromAssetsFile(PinpadActivity.this, "bg_720x1280.png");
                        keyBitmap = getImageFromAssetsFile(PinpadActivity.this, "bg_keyboard.png");
                        strJson = getJson("json_custom4_720x1280.json", PinpadActivity.this);
                    }
                    customKeyBoard4JSON();
                    break;
                case R.id.button_customKeyboard5:
                    cancelBitmap = getImageFromAssetsFile(PinpadActivity.this, "button_cancel_5.png");
                    delBitmap = getImageFromAssetsFile(PinpadActivity.this, "button_delete_5.png");
                    okBitemap = getImageFromAssetsFile(PinpadActivity.this, "button_confirm_5.png");
                    backspaceBitemap = getImageFromAssetsFile(PinpadActivity.this, "back_black.png");
                    strJson = getJson("json_custom5_720x1280.json", PinpadActivity.this);
                    customKeyBoard5JSON();
                    break;
                case R.id.button_customKeyboard6:
                    if (TextUtils.equals("I5000", model)) {
                        backspaceBitemap = getImageFromAssetsFile(PinpadActivity.this, "back_green.png");
                        //===========================
                        echoBitmap_0 = getImageFromAssetsFile(PinpadActivity.this, "input_gray.jpg");
                        echoBitmap_1 = getImageFromAssetsFile(PinpadActivity.this, "input_black.jpg");
                        echoBitmap_2 = getImageFromAssetsFile(PinpadActivity.this, "input_black.jpg");
                        //===========================

                        strJson = getJson("json_custom6_240x320.json", PinpadActivity.this);
                        customKeyBoard6JSON_I5000();
                    } else if (TextUtils.equals("I2000", model)) {
                        delBitmap = getImageFromAssetsFile(PinpadActivity.this, "button_delete_6_s.png");
                        okBitemap = getImageFromAssetsFile(PinpadActivity.this, "button_confirm_6_s.png");
                        backspaceBitemap = getImageFromAssetsFile(PinpadActivity.this, "back_green.png");
                        //===========================
//                        keyBitmap = getImageFromAssetsFile(PinpadActivity.this, "bg_keyboard_6_s.png");
//                        echoBitmap_0 = getImageFromAssetsFile(PinpadActivity.this, "input_white.png");
//                        echoBitmap_1 = getImageFromAssetsFile(PinpadActivity.this, "input_yellow.png");
//                        echoBitmap_2 = getImageFromAssetsFile(PinpadActivity.this, "input_green.png");
//                          strJson = getJson("json_custom6_480x800.json", PinpadActivity.this);
                        //===========================
                        strJson = getJson("json_custom6_480x800_2.json", PinpadActivity.this);
                        bodyBitmap = getImageFromAssetsFile(PinpadActivity.this, "bg_480x800_6.png");
                        //===========================
                        customKeyBoard6JSON();
                    } else {
                        delBitmap = getImageFromAssetsFile(PinpadActivity.this, "button_delete_6.png");
                        okBitemap = getImageFromAssetsFile(PinpadActivity.this, "button_confirm_6.png");
                        backspaceBitemap = getImageFromAssetsFile(PinpadActivity.this, "back_green.png");
                        //===========================
                        keyBitmap = getImageFromAssetsFile(PinpadActivity.this, "bg_keyboard_6.png");
                        echoBitmap_0 = getImageFromAssetsFile(PinpadActivity.this, "input_white.png");
                        echoBitmap_1 = getImageFromAssetsFile(PinpadActivity.this, "input_yellow.png");
                        echoBitmap_2 = getImageFromAssetsFile(PinpadActivity.this, "input_green.png");
                        strJson = getJson("json_custom6_720x1280.json", PinpadActivity.this);
                        //===========================
//                        strJson = getJson("json_custom6_720x1280_2.json", PinpadActivity.this);
//                        bodyBitmap = getImageFromAssetsFile(PinpadActivity.this, "bg_720x1280_6.png");
                        //===========================
                        customKeyBoard6JSON();
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public void downloadKeyDukpt() {
        try {
            byte[] bdkBuff = BytesUtil.hexString2Bytes("ffeeddccbbaa99887766554433221101");
            byte[] ksnBuff = BytesUtil.hexString2Bytes("11111746011BEDE00002");
            int ret = pinpad.downloadKeyDukpt(INDEX_DUKPT_MSR, bdkBuff, bdkBuff.length, ksnBuff, ksnBuff.length, null, 0);
            outputText("downloadKeyDukpt MSR ret: " + ret);
            if (ret == 0) {
                String ksn = BytesUtil.bytes2HexString(ksnBuff);
                outputText("downloadKeyDukpt KSN: " + ksn);
            }
            byte[] bdkBuff1 = BytesUtil.hexString2Bytes("ffeeddccbbaa99887766554433221101");
            byte[] ksnBuff1 = BytesUtil.hexString2Bytes("22222746011BEDE00002");
            ret = pinpad.downloadKeyDukpt(INDEX_DUKPT_EMV, bdkBuff1, bdkBuff1.length, ksnBuff1, ksnBuff1.length, null, 0);
            outputText("downloadKeyDukpt EMV ret: " + ret);
            if (ret == 0) {
                String ksn = BytesUtil.bytes2HexString(ksnBuff1);
                outputText("downloadKeyDukpt KSN: " + ksn);
            }
            byte[] bdkBuff2 = BytesUtil.hexString2Bytes("ffeeddccbbaa99887766554433221101");
            byte[] ksnBuff2 = BytesUtil.hexString2Bytes("33333746011BEDE00002");
            ret = pinpad.downloadKeyDukpt(INDEX_DUKPT_PIN, bdkBuff2, bdkBuff2.length, ksnBuff2, ksnBuff2.length, null, 0);
            outputText("downloadKeyDukpt PIN ret: " + ret);
            if (ret == 0) {
                String ksn = BytesUtil.bytes2HexString(ksnBuff2);
                outputText("downloadKeyDukpt KSN: " + ksn);
            }
            byte[] bdkBuff3 = BytesUtil.hexString2Bytes("ffeeddccbbaa99887766554433221101");
            byte[] ksnBuff3 = BytesUtil.hexString2Bytes("44444746011BEDE00002");
            ret = pinpad.downloadKeyDukpt(INDEX_DUKPT_MAC, bdkBuff3, bdkBuff3.length, ksnBuff3, ksnBuff3.length, null, 0);
            outputText("downloadKeyDukpt MAC ret: " + ret);
            if (ret == 0) {
                String ksn = BytesUtil.bytes2HexString(ksnBuff3);
                outputText("downloadKeyDukpt KSN: " + ksn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void DukptGetKsn() {
        byte[] ksnBuff = new byte[10];
        try {
            int ret = pinpad.DukptGetKsn(INDEX_DUKPT_MSR, ksnBuff);
            outputText("DukptGetKsn MSR ret: " + ret);
            outputText("DukptGetKsn ksn: " + BytesUtil.bytes2HexString(ksnBuff));

            ret = pinpad.DukptGetKsn(INDEX_DUKPT_EMV, ksnBuff);
            outputText("DukptGetKsn EMV ret: " + ret);
            outputText("DukptGetKsn ksn: " + BytesUtil.bytes2HexString(ksnBuff));

            ret = pinpad.DukptGetKsn(INDEX_DUKPT_PIN, ksnBuff);
            outputText("DukptGetKsn PIN ret: " + ret);
            outputText("DukptGetKsn ksn: " + BytesUtil.bytes2HexString(ksnBuff));

            ret = pinpad.DukptGetKsn(INDEX_DUKPT_MAC, ksnBuff);
            outputText("DukptGetKsn MAC ret: " + ret);
            outputText("DukptGetKsn ksn: " + BytesUtil.bytes2HexString(ksnBuff));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void calculateMACOfDUKPTExtend() {
        String str = "111231323334454637383132333445461112313233344546373831323334454622";
        byte[] rawData = BytesUtil.hexString2Bytes(str);
        byte[] outdata = new byte[rawData.length];
        int[] outlen = new int[2];
        byte[] ksnBuff = new byte[10];
        int[] KsnLen = new int[2];
        try {
            int ret = pinpad.calculateMACOfDUKPTExtend(INDEX_DUKPT_MAC, rawData, rawData.length, outdata, outlen, ksnBuff, KsnLen);
            outputText("calculateMACOfDUKPTExtend ret =: " + ret);
            if (ret == 0) {
                outputText("calculateMACOfDUKPTExtend KSN: " + BytesUtil.bytes2HexString(ksnBuff));
                outputText("MAC: " + BytesUtil.bytes2HexString(outdata));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void encryptWithPEK(int keyIndex) {
        byte[] rawData = BytesUtil.hexString2Bytes("9F260814E50F30268921459F2701409F1007060201039400029F3704A7D8F2329F36020601950500800000009A031901029B02E8009C0100");
        byte[] outdata = new byte[rawData.length];
        int[] outlen = new int[2];
        byte[] ksnBuff = new byte[10];
        int[] KsnLen = new int[2];
        try {
            int ret = pinpad.encryptWithPEK(0x03, keyIndex, rawData, rawData.length, outdata, outlen, ksnBuff, KsnLen);
            outputText("encryptWithPEK ret: " + ret);
            if (ret == 0) {
                outputText("KSN: " + BytesUtil.bytes2HexString(ksnBuff));
                outputText("Result: " + BytesUtil.bytes2HexString(outdata));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void DukptEncryptDataIV(int keyIndex) {
        byte[] rawData = BytesUtil.hexString2Bytes("9F260814E50F30268921459F2701409F1007060201039400029F3704A7D8F2329F36020601950500800000009A031901029B02E8009C0100");
        byte[] outdata = new byte[rawData.length];
        int[] outlen = new int[2];
        byte[] ksnBuff = new byte[10];
        int[] KsnLen = new int[2];
        byte[] ivData = new byte[8];
        int ivLen = 8;
        try {
            byte[] decOut = new byte[outdata.length];
            int[] decOutlen = new int[2];
            int ret = pinpad.DukptEncryptDataIV(0x03, keyIndex, 0x10, ivData, ivLen, outdata, outdata.length,
                    decOut, decOutlen, ksnBuff, KsnLen);
            outputText("DukptEncryptDataIV DEC ECB ret: " + ret);
            if (ret == 0) {
                outputText("KSN: " + BytesUtil.bytes2HexString(ksnBuff));
                outputText("Dec result: " + BytesUtil.bytes2HexString(decOut));
            }

            ret = pinpad.DukptEncryptDataIV(0x03, keyIndex, 0x01, ivData, ivLen, rawData, rawData.length,
                    outdata, outlen, ksnBuff, KsnLen);
            outputText("DukptEncryptDataIV ENC CBC ret: " + ret);
            if (ret == 0) {
                outputText("KSN: " + BytesUtil.bytes2HexString(ksnBuff));
                outputText("Enc result: " + BytesUtil.bytes2HexString(outdata));
            }

            decOut = new byte[outdata.length];
            decOutlen = new int[2];
            ret = pinpad.DukptEncryptDataIV(0x03, keyIndex, 0x11, ivData, ivLen, outdata, outdata.length,
                    decOut, decOutlen, ksnBuff, KsnLen);
            outputText("DukptEncryptDataIV DEC CBC ret: " + ret);
            if (ret == 0) {
                outputText("KSN: " + BytesUtil.bytes2HexString(ksnBuff));
                outputText("Dec result: " + BytesUtil.bytes2HexString(decOut));
            }

            outdata = new byte[8];
            ret = pinpad.DukptEncryptDataIV(0x04, 1, 0x54, ivData, ivLen, rawData, rawData.length,
                    outdata, outlen, ksnBuff, KsnLen);
            outputText("DukptEncryptDataIV CMAC1 ret: " + ret);
            if (ret == 0) {
                outputText("KSN: " + BytesUtil.bytes2HexString(ksnBuff));
                outputText("Enc result: " + BytesUtil.bytes2HexString(outdata));
            }

            ret = pinpad.DukptEncryptDataIV(0x04, 2, 0x54, ivData, ivLen, rawData, rawData.length,
                    outdata, outlen, ksnBuff, KsnLen);
            outputText("DukptEncryptDataIV CMAC2 ret: " + ret);
            if (ret == 0) {
                outputText("KSN: " + BytesUtil.bytes2HexString(ksnBuff));
                outputText("Enc result: " + BytesUtil.bytes2HexString(outdata));
            }

            ret = pinpad.DukptEncryptDataIV(0x04, 3, 0x54, ivData, ivLen, rawData, rawData.length,
                    outdata, outlen, ksnBuff, KsnLen);
            outputText("DukptEncryptDataIV CMAC3 ret: " + ret);
            if (ret == 0) {
                outputText("KSN: " + BytesUtil.bytes2HexString(ksnBuff));
                outputText("Enc result: " + BytesUtil.bytes2HexString(outdata));
            }
            ret = pinpad.DukptEncryptDataIV(0x04, 4, 0x54, ivData, ivLen, rawData, rawData.length,
                    outdata, outlen, ksnBuff, KsnLen);
            outputText("DukptEncryptDataIV CMAC4 ret: " + ret);
            if (ret == 0) {
                outputText("KSN: " + BytesUtil.bytes2HexString(ksnBuff));
                outputText("Enc result: " + BytesUtil.bytes2HexString(outdata));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void DukptEncryptDataIV_MAC5(int keyIndex) {
        try {
            byte[] rawData = BytesUtil.hexString2Bytes("9F260814E50F30268921459F2701409F1007060201039400029F3704A7D8F2329F36020601950500800000009A031901029B02E8009C0100");
            byte[] outdata = new byte[8];
            int[] outlen = new int[2];
            byte[] ksnBuff = new byte[10];
            int[] KsnLen = new int[2];
            byte[] ivData = new byte[8];
            int ivLen = 8;
            int ret = pinpad.DukptEncryptDataIV(0x04, keyIndex, 0x54, ivData, ivLen, rawData, rawData.length,
                    outdata, outlen, ksnBuff, KsnLen);
            if (ret == 0) {
                outputText("KSN: " + BytesUtil.bytes2HexString(ksnBuff));
                outputText("DukptEncryptDataIV_MAC5 result: " + BytesUtil.bytes2HexString(outdata));
            }

            rawData = BytesUtil.hexString2Bytes("9F260814E50F30268921459F2701409F1007060201039400029F3704A7D8F2329F36020601950500800000009A031901029B02E8009C");
            ret = pinpad.DukptEncryptDataIV(0x04, keyIndex, 0x54, ivData, ivLen, rawData, rawData.length,
                    outdata, outlen, ksnBuff, KsnLen);
            if (ret == 0) {
                outputText("KSN: " + BytesUtil.bytes2HexString(ksnBuff));
                outputText("DukptEncryptDataIV_MAC5(Padding) result: " + BytesUtil.bytes2HexString(outdata));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GetDukptPinBlock() {
        Bundle pinpadBundle = new Bundle();
        pinpadBundle.putString("cardNo", pan);
        pinpadBundle.putString("title", "Security Keyborad");
        pinpadBundle.putString("message", "Please enter your pin");
        pinpadBundle.putBoolean("sound", true);
        pinpadBundle.putBoolean("bypass", true);
        pinpadBundle.putString("supportPinLen", "0,4,5,6,7,8,9,10,11,12");
        pinpadBundle.putBoolean("FullScreen", true);
        pinpadBundle.putBoolean("onlinePin", true);
        pinpadBundle.putLong("timeOutMS", 30 * 1000);
        pinpadBundle.putInt("PINKeyNo", INDEX_DUKPT_PIN);
        pinpadBundle.putBoolean("randomKeyboard", false);
        pinpadBundle.putBoolean("FullScreen", true);

        try {
            pinpad.GetDukptPinBlock(pinpadBundle, new PinInputListener() {
                @Override
                public void onInput(int i, int i1) {

                }

                @Override
                public void onConfirm(byte[] bytes, boolean b) {

                }

                @Override
                public void onConfirm_dukpt(byte[] PinBlock, byte[] ksn) {
                    if (PinBlock == null) {
                        outputText("onConfirm_dukpt: " + "\n" +
                                "NonePin");
                        return;
                    }
                    if (ksn != null) {
                        outputText("GetDukptPinBlock KSN: " + BytesUtil.bytes2HexString(ksn));
                    }
                    if (PinBlock != null) {
                        outputText("PinBlock: " + BytesUtil.bytes2HexString(PinBlock));
                    }
                }

                @Override
                public void onCancel() {
                    outputText("onCancel");
                }

                @Override
                public void onTimeOut() {
                    outputText("onTimeOut");
                }

                @Override
                public void onError(int errorCode) {
                    outputText("onError: " + errorCode);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //    //each index(0-6):
    //    public static final int SECURITY_KEYBOARD_TITLE = 0;
    //    public static final int SECURITY_KEYBOARD_INFO = 1;
    //    public static final int SECURITY_KEYBOARD_PASSWORD = 2;
    //    public static final int SECURITY_KEYBOARD_KEY_NUMBER = 3;
    //    public static final int SECURITY_KEYBOARD_KEY_CANCEL = 4;
    //    public static final int SECURITY_KEYBOARD_KEY_DELETE = 5;
    //    public static final int SECURITY_KEYBOARD_KEY_OK = 6;

    public void startInputPin(Bundle pinpadBundle, final boolean isOnlinePin, final int keyIndex, final String plainKey, boolean randomLocation) {
        if (pinpadBundle == null || pinpadBundle.isEmpty()) {
            pinpadBundle = new Bundle();
            pinpadBundle.putString("cardNo", pan);
            pinpadBundle.putBoolean("sound", true);
            pinpadBundle.putBoolean("bypass", true);
            pinpadBundle.putString("supportPinLen", "0,4,5,6,7,8,9,10,11,12");
            pinpadBundle.putBoolean("FullScreen", true);
            pinpadBundle.putBoolean("onlinePin", isOnlinePin);
            pinpadBundle.putInt("PINKeyNo", keyIndex);
            pinpadBundle.putLong("timeOutMS", 30 * 1000);
            pinpadBundle.putBoolean("randomKeyboard", false);


            //		short [] textSize = {10, 10, 10, 10, 10, 10, 10 };   //  10-32  // 720*1280
            //		for(int i=0; i<7; i++){
            //			textSize[i] = 20;
            //		}
            //		textSize[SECURITY_KEYBOARD_KEY_CANCEL] = 15;
            //		textSize[SECURITY_KEYBOARD_KEY_DELETE] = 15;
            //		textSize[SECURITY_KEYBOARD_KEY_OK] = 15;
            //		pinpadBundle.putShortArray("textSize", textSize);  // text size
            //		String [] numberText = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
            //		for(int i=0; i<numberText.length; i++){
            //			numberText[i] = DataFormatUtil.getNumberEnglishToArab(numberText[i]);
            //		}
            //		pinpadBundle.putStringArray("numberText", numberText);

            pinpadBundle.putString("title", "Security Keyborad");
            pinpadBundle.putString("message", "Please enter your pin");
            pinpadBundle.putString("cancelText", "Cancel1");
            pinpadBundle.putString("deleteText", "Delete1");
            pinpadBundle.putString("okText", "Ok1");
        }

        try {
            pinpad.getPinBlockEx(pinpadBundle, new PinInputListener() {

                @Override
                public void onInput(int pinLen, int v) {
                    Log.e("pinpad", "onInput:pinLen = " + pinLen + "v = " + (char) v);
                    outputText("onInput: " + "\n" +
                            "pinLen: " + pinLen + ", keyCode: " + v);
                }

                @Override
                public void onConfirm(byte[] data, boolean isNonePin) {
                    //PIN密文
                    if (data == null) {
                        outputText("onConfirm: " + "\n" +
                                "isNonePin: " + isNonePin + "\n" +
                                "pinData: " + "");
                        return;
                    }

                    String pinBlock = (data == null) ? "" : new String(data);
                    if (!isOnlinePin) {
                        pinBlock = new String(data);
                        outputText("onConfirm: " + "\n" +
                                "isNonePin: " + isNonePin + "\n" +
                                "plain pinData: " + pinBlock);
                    } else {
                        outputText("onConfirm: " + "\n" +
                                "Encrypted pinData: " + pinBlock + "\n" +
                                "isNonePin: " + isNonePin);
                        //decrypt PIN
                        pinBlock = PinpadUtil.getPinData(INDEX_WK, pan, BytesUtil.hexString2Bytes(pinBlock), plainKey);
                        outputText("plain pinData: " + pinBlock);
                    }
                }

                @Override
                public void onConfirm_dukpt(byte[] PinBlock, byte[] ksn) {

                }

                @Override
                public void onCancel() {
                    outputText("onCancel");
                }

                @Override
                public void onTimeOut() {
                    outputText("onTimeOut");
                }

                @Override
                public void onError(int errorCode) {
                    outputText("onError: " + errorCode);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String strJson;
    private Bitmap cancelBitmap;
    private Bitmap delBitmap;
    private Bitmap okBitemap;
    private Bitmap backspaceBitemap;
    private Bitmap imageViewBitmap;
    private Bitmap bodyBitmap;
    private Bitmap keyBitmap;
    private Bitmap echoBitmap_0;
    private Bitmap echoBitmap_1;
    private Bitmap echoBitmap_2;
    public static final int SECURITY_KEYBOARD_TITLE = 0;
    public static final int SECURITY_KEYBOARD_INFO = 1;
    public static final int SECURITY_KEYBOARD_PASSWORD = 2;
    public static final int SECURITY_KEYBOARD_KEY_NUMBER = 3;
    public static final int SECURITY_KEYBOARD_KEY_CANCEL = 4;
    public static final int SECURITY_KEYBOARD_KEY_DELETE = 5;
    public static final int SECURITY_KEYBOARD_KEY_OK = 6;
    public static final int SECURITY_KEYBOARD_HEAD = 7;
    public static final int SECURITY_KEYBOARD_MONEY = 8;
    public static final int SECURITY_KEYBOARD_VIEW = 9;
    public static final int SECURITY_KEYBOARD_KEY_BLANK = 10;
    public static final int SECURITY_KEYBOARD_KEY = 11;
    public static final int SECURITY_KEYBOARD_BODY = 12;
    public static final int SECURITY_KEYBOARD_BACKSPACE = 13;
    private int[] backgroundColor = {0XFF1c1c1c, 0XFFFFFFFF, 0XFFFFFFFF, 0XFFFFFFFF, 0Xffe3452f, 0Xffb4ac24, 0Xff2ead2a, Color.WHITE, 0XFFFFFFFF, 0XFFFFFFFF, 0XFFFFFFFF, 0X00FFFFFF, 0XFFFFFFFF, 0XFF1c1c1c};
    private int[] textColor = {Color.BLACK, Color.GRAY, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK};

    public void customKeyBoard1() {
        Bundle pinpadBundle = new Bundle();
        pinpadBundle.putString("cardNo", pan);
        pinpadBundle.putBoolean("sound", true);
        pinpadBundle.putBoolean("bypass", true);
        pinpadBundle.putString("supportPinLen", "0,4,5,6,7,8,9,10,11,12");
        pinpadBundle.putBoolean("FullScreen", true);
        pinpadBundle.putBoolean("onlinePin", true);
        pinpadBundle.putInt("PINKeyNo", INDEX_WK);
        pinpadBundle.putLong("timeOutMS", 30 * 1000);
        pinpadBundle.putString("title", "Security Keyborad");
        pinpadBundle.putString("message", "Please enter your pin");

        pinpadBundle.putBoolean("randomKeyboard", true);
        pinpadBundle.putBoolean("randomKeyboardLocation", false);
        pinpadBundle.putBoolean("inputBySP", false);
        pinpadBundle.putString("infoLocation", "CENTER");
        pinpadBundle.putBoolean("customKeyboard", true);
        pinpadBundle.putInt("customKeyboardDialog", 4);//Hardcode
        pinpadBundle.putString("head", "Urovo Urovo Urovo");
        pinpadBundle.putString("money", "$110");
        startInputPin(pinpadBundle, true, INDEX_WK, pinKey, false);
    }

    public void customKeyBoard2() {
        Bundle pinpadBundle = new Bundle();
        pinpadBundle.putString("cardNo", pan);
        pinpadBundle.putBoolean("sound", true);
        pinpadBundle.putBoolean("bypass", true);
        pinpadBundle.putString("supportPinLen", "0,4,5,6,7,8,9,10,11,12");
        pinpadBundle.putBoolean("FullScreen", true);
        pinpadBundle.putBoolean("onlinePin", true);
        pinpadBundle.putInt("PINKeyNo", INDEX_WK);
        pinpadBundle.putLong("timeOutMS", 30 * 1000);
        pinpadBundle.putString("title", "Security Keyborad");
        pinpadBundle.putString("message", "Please enter your pin");

        pinpadBundle.putBoolean("randomKeyboard", true);
        pinpadBundle.putBoolean("randomKeyboardLocation", false);
        pinpadBundle.putBoolean("inputBySP", false);
        pinpadBundle.putString("infoLocation", "LEFT");
        pinpadBundle.putBoolean("customKeyboard", true);
        pinpadBundle.putInt("customKeyboardDialog", 4);//Hardcode
        pinpadBundle.putString("head", "Urovo Urovo Urovo");
        pinpadBundle.putString("money", "$110");
        startInputPin(pinpadBundle, true, INDEX_WK, pinKey, false);
    }

    public void customKeyBoard3JSON() {
        Bundle pinpadBundle = new Bundle();
        pinpadBundle.putString("cardNo", pan);
        pinpadBundle.putBoolean("sound", true);
        pinpadBundle.putBoolean("bypass", true);
        pinpadBundle.putString("supportPinLen", "0,4,5,6,7,8,9,10,11,12");
        pinpadBundle.putBoolean("FullScreen", true);
        pinpadBundle.putBoolean("onlinePin", true);
        pinpadBundle.putInt("PINKeyNo", INDEX_WK);
        pinpadBundle.putLong("timeOutMS", 300 * 1000);
        pinpadBundle.putString("title", "");
        pinpadBundle.putString("money", "21,00 €");
        pinpadBundle.putString("head", "Introduzca pin y pulse aceptar");
        pinpadBundle.putString("message", "CONTACLTLESS11");

        pinpadBundle.putIntArray("backgroundColor", backgroundColor);
        pinpadBundle.putIntArray("textColor", textColor);
        pinpadBundle.putBoolean("inputBySP", false);
        pinpadBundle.putString("infoLocation", "RIGHT");
        pinpadBundle.putBoolean("randomKeyboard", false);
        pinpadBundle.putBoolean("customization", true);
        pinpadBundle.putString("strJson", strJson);
        pinpadBundle.putParcelable("cancelBitmap", cancelBitmap);
        pinpadBundle.putParcelable("delBitmap", delBitmap);
        pinpadBundle.putParcelable("okBitmap", okBitemap);
        pinpadBundle.putParcelable("backspaceBitmap", backspaceBitemap);
        pinpadBundle.putBoolean("randomKeyboardLocation", false);
        pinpadBundle.putIntArray("randomKeyboardStaticLocation", new int[]{0, randomInt()});

        startInputPin(pinpadBundle, true, INDEX_WK, pinKey, true);
    }

    public int randomInt() {
        int[] randomValue = new int[]{440, 480, 520, 560, 600, 660};
        String base = "012345";
        if (TextUtils.equals("I2000", model)) {
            randomValue = new int[]{280, 310, 340, 360, 400};
            base = "01234";
        }
        int result = 0;
        Random random = new Random();
        int index = random.nextInt(base.length());
        result = randomValue[index];
        Log.e("PinpadActivity", "randomInt: " + result);
        return result;
    }

    public void customKeyBoard4JSON() {
        Bundle pinpadBundle = new Bundle();
        pinpadBundle.putString("cardNo", pan);
        pinpadBundle.putBoolean("sound", true);
        pinpadBundle.putBoolean("bypass", true);
        pinpadBundle.putString("supportPinLen", "0,4,5,6,7,8,9,10,11,12");
        pinpadBundle.putBoolean("FullScreen", true);
        pinpadBundle.putBoolean("onlinePin", true);
        pinpadBundle.putInt("PINKeyNo", INDEX_WK);
        pinpadBundle.putLong("timeOutMS", 300 * 1000);
        pinpadBundle.putString("title", "");
        pinpadBundle.putString("head", "");
        pinpadBundle.putString("money", "");
        pinpadBundle.putString("message", "Enter your pin");

        int[] backgroundColor = {0X00FFFFFF, 0X00FFFFFF, 0X00FFFFFF, 0X00e3452f, 0X00895623, 0X00258945, 0X00364952, 0XFF123456, 0XFF876328, 0X00FFFFFF, 0XFF877454, 0X00FFFFFF, 0xff1234FF, 0X001c1c1c};
        int[] textColor = {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE};
        pinpadBundle.putIntArray("backgroundColor", backgroundColor);
        pinpadBundle.putIntArray("textColor", textColor);
        pinpadBundle.putBoolean("inputBySP", false);
        pinpadBundle.putString("infoLocation", "RIGHT");
        pinpadBundle.putBoolean("randomKeyboard", false);
        pinpadBundle.putBoolean("randomKeyboardLocation", false);
        pinpadBundle.putBoolean("customization", true);
        pinpadBundle.putString("strJson", strJson);
        pinpadBundle.putParcelable("cancelBitmap", cancelBitmap);
        pinpadBundle.putParcelable("delBitmap", delBitmap);
        pinpadBundle.putParcelable("okBitmap", okBitemap);
        pinpadBundle.putParcelable("backspaceBitmap", backspaceBitemap);
        pinpadBundle.putParcelable("viewBitmap", imageViewBitmap);
        pinpadBundle.putParcelable("bodyBitmap", bodyBitmap);

        startInputPin(pinpadBundle, true, INDEX_WK, pinKey, true);
    }

    public void customKeyBoard5JSON() {
        Bundle pinpadBundle = new Bundle();
        pinpadBundle.putString("cardNo", pan);
        pinpadBundle.putBoolean("sound", true);
        pinpadBundle.putBoolean("bypass", true);
        pinpadBundle.putString("supportPinLen", "0,4,5,6,7,8,9,10,11,12");
        pinpadBundle.putBoolean("FullScreen", true);
        pinpadBundle.putBoolean("onlinePin", true);
        pinpadBundle.putInt("PINKeyNo", INDEX_WK);
        pinpadBundle.putLong("timeOutMS", 300 * 1000);
        pinpadBundle.putString("title", "");
        pinpadBundle.putString("head", "");
        pinpadBundle.putString("money", "");
        pinpadBundle.putString("message", "Enter your pin");

        int[] textColor = {Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK};
        pinpadBundle.putIntArray("textColor", textColor);
        pinpadBundle.putBoolean("inputBySP", false);
        pinpadBundle.putBoolean("randomKeyboard", false);
        pinpadBundle.putBoolean("randomKeyboardLocation", false);
        pinpadBundle.putBoolean("customization", true);
        pinpadBundle.putString("strJson", strJson);
        pinpadBundle.putParcelable("cancelBitmap", cancelBitmap);
        pinpadBundle.putParcelable("delBitmap", delBitmap);
        pinpadBundle.putParcelable("okBitmap", okBitemap);
        pinpadBundle.putParcelable("backspaceBitmap", backspaceBitemap);

        startInputPin(pinpadBundle, true, INDEX_WK, pinKey, true);
    }

    public void customKeyBoard6JSON() {
        Bundle pinpadBundle = new Bundle();
        pinpadBundle.putString("cardNo", pan);
        pinpadBundle.putBoolean("sound", true);
        pinpadBundle.putBoolean("bypass", true);
        pinpadBundle.putString("supportPinLen", "0,4");
        pinpadBundle.putBoolean("FullScreen", true);
        pinpadBundle.putBoolean("onlinePin", true);
        pinpadBundle.putInt("PINKeyNo", INDEX_WK);
        pinpadBundle.putLong("timeOutMS", 30 * 1000);
        pinpadBundle.putString("title", "PIN de seguridad");
        pinpadBundle.putString("money", "Amount: 1.00");
        pinpadBundle.putString("message", "v.0.23 - build 5678        Serial 12345678");

        int[] backgroundColor = {0X00FFFFFF, 0X00FFFFFF, 0X00FFFFFF, 0X00e3452f, 0X00895623, 0X00258945, 0X00364952, 0XFF123456, 0XFF876328, 0X00FFFFFF, 0XFF877454, 0X00FFFFFF, Color.BLACK, Color.BLACK, 0XFF1ED94F};
        int[] textColor = {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.BLACK};
        pinpadBundle.putIntArray("backgroundColor", backgroundColor);
        pinpadBundle.putIntArray("textColor", textColor);
        pinpadBundle.putBoolean("inputBySP", false);
        pinpadBundle.putBoolean("randomKeyboard", true);
        pinpadBundle.putBoolean("customization", true);
        pinpadBundle.putString("strJson", strJson);
        pinpadBundle.putParcelable("backspaceBitmap", backspaceBitemap);
        pinpadBundle.putParcelable("delBitmap", delBitmap);
        pinpadBundle.putParcelable("okBitmap", okBitemap);

        //===========================
        pinpadBundle.putParcelable("keyBitmap", keyBitmap);
        pinpadBundle.putParcelable("echoBitmap_0", echoBitmap_0);
        pinpadBundle.putParcelable("echoBitmap_1", echoBitmap_1);
        pinpadBundle.putParcelable("echoBitmap_2", echoBitmap_2);
        //===========================
        pinpadBundle.putParcelable("bodyBitmap", bodyBitmap);
        //===========================
        startInputPin(pinpadBundle, true, INDEX_WK, pinKey, true);
    }

    public void customKeyBoard6JSON_I5000() {
        Bundle pinpadBundle = new Bundle();
        pinpadBundle.putString("cardNo", pan);
        pinpadBundle.putBoolean("sound", true);
        pinpadBundle.putBoolean("bypass", true);
        pinpadBundle.putString("supportPinLen", "0,6");
        pinpadBundle.putBoolean("FullScreen", true);
        pinpadBundle.putBoolean("onlinePin", true);
        pinpadBundle.putInt("PINKeyNo", INDEX_WK);
        pinpadBundle.putLong("timeOutMS", 30 * 1000);
        pinpadBundle.putString("title", "Please Entert PIN");
        pinpadBundle.putString("money", "Amount: $12,789");
//        pinpadBundle.putString("head", "head");
//        pinpadBundle.putString("message", "message");

        int[] backgroundColor = {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.BLUE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE};
        int[] textColor = {Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.WHITE, Color.WHITE, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK};
        pinpadBundle.putIntArray("backgroundColor", backgroundColor);
        pinpadBundle.putIntArray("textColor", textColor);
        pinpadBundle.putBoolean("inputBySP", false);
        pinpadBundle.putBoolean("randomKeyboard", true);
        pinpadBundle.putBoolean("customization", true);
        pinpadBundle.putString("strJson", strJson);
//        pinpadBundle.putParcelable("backspaceBitmap", backspaceBitemap);

        //===========================
        pinpadBundle.putParcelable("echoBitmap_0", echoBitmap_0);
        pinpadBundle.putParcelable("echoBitmap_1", echoBitmap_1);
        pinpadBundle.putParcelable("echoBitmap_2", echoBitmap_2);
        //===========================
//        pinpadBundle.putParcelable("bodyBitmap", bodyBitmap);
        startInputPin(pinpadBundle, true, INDEX_WK, pinKey, true);
    }

    public static String getJson(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

}
