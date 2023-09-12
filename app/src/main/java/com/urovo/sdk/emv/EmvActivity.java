package com.urovo.sdk.emv;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.device.DeviceManager;
import android.device.IccManager;
import android.device.MagManager;
import android.device.SEManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IInputActionListener;
import android.os.Message;
import android.os.RemoteException;


import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;




import com.urovo.i9000s.api.emv.ContactlessAidData;
import com.urovo.i9000s.api.emv.ContantPara;
import com.urovo.i9000s.api.emv.EmvAidData;
import com.urovo.i9000s.api.emv.EmvListener;
import com.urovo.i9000s.api.emv.EmvNfcKernelApi;
import com.urovo.i9000s.api.emv.Funs;
import com.urovo.paypassApi.PayPassApiJni;
import com.urovo.sdk.R;
import com.urovo.sdk.pinpad.PinPadProviderImpl;
import com.urovo.sdk.pinpad.listener.PinInputListener;


import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static com.urovo.i9000s.api.emv.Funs.StrToHexByte;
import static com.urovo.sdk.emv.MyHandler.ionShowMsg;
import static com.urovo.sdk.emv.SoundTool.SOUND_TYPE_ERR;
import static com.urovo.sdk.emv.SoundTool.SOUND_TYPE_SUCCESS;
import static com.urovo.sdk.emv.SoundTool.getMySound;

import static com.urovo.sdk.view.PinpadActivity.INDEX_DUKPT_PIN;
import static com.urovo.sdk.view.PinpadActivity.INDEX_WK;


//Cooperate "User guide of EMV kernel library API" decomentation
public class EmvActivity extends Activity {
    private static final String TAG="MainActivity";
    public final static int REQUEST_ONLINE    = 9    ;
    public final static int SHOW_ERR          = 10    ;
    public final static int REQUEST_ONLINEPIN = 11     ;
    public final static int SHOW_INFO         = 15     ;
    //false
    public  static volatile Boolean isEnterAmtAfterReadRecord=false;//When startEMV this flag is true, you need to enter the amount in the onRequestSetAmount() callback
    private AdminInputListener pinpadlistener = new AdminInputListener();
    private boolean isDUKPT=true;
    private boolean isRefound=false;
    Bundle param;
    Bundle param1;
    public final static String LogTag ="applog";
    private Button btnStartEmv = null;
    private Button btnmsr = null;
    private Button btnTest = null;
    public static TextView text1 = null;
    SEManager mSEManager ;

    public static EmvListener mEmvListener = null;
    public static Context mContext = null ;
    public static MyHandler mHandler;
    public static EditText edtInput;
    private CountDownTimer alertTimer;
    private boolean isLoop=true;
    private boolean isNeedFallback=false;
    private int mNotICCNumber=0;
    public EmvNfcKernelApi mKernelApi = EmvNfcKernelApi.getInstance();
    //about encryt key test
    private void test()
    {
        //mSEManager=new SEManager();
        String pinblock = "04121C86EE6FFC78";
        String Rowdata="7B22747261636B31223A22222C22747261636B32223A2234353039393530323134313338303034443231303532303130343930303030303030303030222C22656D7654616773223A2239463136306630303030303030303030303030303030303030303030303030303030303034463037413030303030303030333130313039413033323130343139394630363037413030303030303030333130313039463231303332323439343839463132303935363439353334313230343734463443343435413038343530393935303231343133383030343537313334353039393530323134313338303034443231303532303130343930303030303030303030463946344530663230323032303230323032303230323032303230333033303330333033303832303233433030384531383030303030303030303030303030303030323031314530343145303331463032303030303030303030303030303030303546323530333139303930313946303730324646303039463044303539383638433441383030394630453035323431303238303030303946304630353938363844344638303039433031303039463430303536303030463041303031393530353030383030303830303039423032453830303834303741303030303030303033313031303546333430313030354632303135344334353439353634313246344134463533343532303443353534393533323032303230323032303230354632383032303033323946344330323031443935303063353634393533343132303433353234353434343935343446394630383032303039413946303130363030303030303030303030303946313530323030303039463143303830303030303030303030303030303030394632363038333131434137463235323943423945423946323730313830394631303037303630313041303341304238303339463337303443314645413232453946333630323031443939353035303038303030383030303941303332313034313939433031303039463032303630303030303030313430303035463241303230313536383230323343303039463141303230313536394630333036303030303030303030303030394633333033453045394338394633343033314530333030394633353031323239463145303830303030303030303030303030303030383430374130303030303030303331303130394630393032303032303946343130343030303030303030227D";
        String bsBdk = "33333333333333333333333333333333";

        String bsKsn1 = "11111746011BEDE00002";
        String bsIpek1 = "AD0A0D6491EBF55FC9B11B257D9A4622";// "F706A15BE612755AF6556CAC41258088";

        String bsKsn2 = "FEDCBA9876543200001B";
        String bsIpek2 = "ffeeddccbbaa99887766554433221101";

        String bsKsn3 = "1122BA9876543200001B";
        String bsIpek3 = "ffeeddccbbaa99887766554433221102";

        String bsKsn4 = "1133BA9876543200001B";
        String bsIpek4 = "ffeeddccbbaa99887766554433221102";

        byte[] bsRetKsn = new byte[10];
        byte[] pinKsn = new byte[10];
        byte[] macKsn = new byte[10];
        byte[] bsOut1 = new byte[16];
        byte[] bsOut2 = new byte[16];
        byte[] macout = new byte[16];
        byte[] pinblockout = new byte[2048];
        byte[] dataBuf = new byte[16];

        byte[] pinblockbuf = StrToHexByte(pinblock);
        // byte[] dataBuf = StrToHexByte(data);
        byte[] databuf=StrToHexByte(Rowdata);
        byte[] bsBdkBuf = StrToHexByte(bsBdk);
        byte[] bsKsnBuf1 = StrToHexByte(bsKsn1);
        byte[] bsKsnBuf2 = StrToHexByte(bsKsn2);
        byte[] bsKsnBuf3 = StrToHexByte(bsKsn3);
        byte[] bsKsnBuf4 = StrToHexByte(bsKsn4);
        byte[] bsIpekBuf1 = StrToHexByte(bsIpek1);
        byte[] bsIpekBuf2 = StrToHexByte(bsIpek2);
        byte[] bsIpekBuf3 = StrToHexByte(bsIpek3);
        byte[] reslen = new byte[1];
        byte[] resksnlen=new byte[1];
        byte[] response = new byte[8];
        int[] pinblockoutlen=new int[2];
        int[] pinksnoutlen=new int[2];
        int[] macoutlen=new int[2];
        int[] macksnlen=new int[2];
        byte[] datas = StrToHexByte("313137333231303031303235343933383137313031303037343830334432383132323031303030303030303230303030");

        int ret = mSEManager.clearKey(response, reslen);
        byte[] mastkey = StrToHexByte("11111111111111111111111111111111");
        byte[] Leftwkmac = StrToHexByte("3131313131313131313131313131313131313131313131313131313131313131");
        byte[] Rightwkmac = StrToHexByte("31313131313131313131313131313131");
        mSEManager.deleteKey(4, 1, response, reslen);
        ret = mSEManager.downloadKey(4, 1, 1, mastkey, mastkey.length, response, reslen);//master key
        if(ret == 0) Log.d("applog"," \ndownload master key OK \n" + Funs.bytes2HexString(mastkey,mastkey.length)+" res"+Funs.bytes2HexString(response,8));

        mSEManager.deleteKey(3, 4, response, reslen);
        ret =  mSEManager.downloadKey(3, 4, 1, Leftwkmac, Leftwkmac.length, response, reslen);//mac key
        if(ret == 0) Log.d("applog","\ndownload mac Leftkey OK \n" + Funs.bytes2HexString(Leftwkmac,Leftwkmac.length) );
        mSEManager.deleteKey(3, 5, response, reslen);
        ret =  mSEManager.downloadKey(3, 5, 1, Rightwkmac, Rightwkmac.length, response, reslen);//mac key
        if(ret == 0) Log.d("applog","\ndownload mac Rightkey OK \n" + Funs.bytes2HexString(Rightwkmac,Rightwkmac.length) );
        mSEManager.deleteKey(3, 6, response, reslen);
        ret =  mSEManager.downloadKey(3, 6, 1, mastkey, mastkey.length, response, reslen);//mac key
        if(ret == 0) Log.d("applog","\ndownload mac key OK \n" + Funs.bytes2HexString(Rightwkmac,Rightwkmac.length) +" res"+Funs.bytes2HexString(response,8));
//       ret = mEmvApi.calcMac(4,5,datas.length,datas,response,0x02);
//       if(ret == 0) Log.d("applog"," \ncalcMac ANSI 9.19 ==" + Funs.bytes2HexString(response,8));

        //OS 8.1 MAC 9.19
//       ret= mSEManager.pciGetMac((byte)6,datas.length,datas,response,(byte)1);
//       if(ret == 0) Log.d("applog"," \npciGetMac calcMac ANSI 9.19==" + Funs.bytes2HexString(response,8));

        //OS 5.1 MAC 9.19
        //      ret=mSEManager.calculateMAC(3,6,1,2,datas,datas.length,response,reslen);
//        if(ret==0) Log.d("applog"," \ncalculateMAC calcMac ANSI 9.19==" + Funs.bytes2HexString(response,8));



        //i9100 SQ29WR android8.1
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O)
        {
            //keyType
            ret=mSEManager.downloadKeyDukpt(1,bsBdkBuf,16,bsKsnBuf1,10,bsIpekBuf1,0);//11111746011BEDE00002
            if(ret==0) Log.d("applog","downloadKeyDukpt_MSR success");
            ret=mSEManager.downloadKeyDukpt(2,bsBdkBuf,16,bsKsnBuf2,10,bsIpekBuf1,0);//FEDCBA9876543200001B
            if(ret==0) Log.d("applog","downloadKeyDukpt_EMV success");
            ret=mSEManager.downloadKeyDukpt(3,bsBdkBuf,16,bsKsnBuf3,10,bsIpekBuf1,0);//"1122BA9876543200001B";
            if(ret==0) Log.d("applog","downloadKeyDukpt_PIN success");
            ret=mSEManager.downloadKeyDukpt(4,bsBdkBuf,16,bsKsnBuf4,10,bsIpekBuf1,0);//1133BA9876543200001B
            if(ret==0) Log.d("applog","downloadKeyDukpt_MAC success");
            //KeyType 0x02--MAC 0x03--Data 0x01--Pin
            ret=mSEManager.encryptWithPEK(0x03, 0x01, databuf, databuf.length, pinblockout, pinblockoutlen , pinKsn, pinksnoutlen);
            if(ret==0) //encrypt data
            {
                Log.d("applog"," \nencryptWithPEK ==" + Funs.bytes2HexString(pinblockout,pinblockoutlen[0]));
                Log.d("applog"," \nencryptWithPEK ksn==" + Funs.bytes2HexString(pinKsn,10));

            }
            ret=mSEManager.encryptWithPEK(0x01, 0x01, pinblockbuf, pinblockbuf.length, pinblockout, pinblockoutlen , pinKsn, pinksnoutlen);
            if(ret==0)//encrypt pinblock
            {
                Log.d("applog"," \nencryptWithPEK keytype 0x01 ==" + Funs.bytes2HexString(pinblockout,pinblockoutlen[0]));
                Log.d("applog"," \nencryptWithPEK ksn==" + Funs.bytes2HexString(pinKsn,10));

            }

            //
            ret = mSEManager.calculateMACOfDUKPTExtend(0x04, databuf, databuf.length, macout, macoutlen , macKsn, macksnlen);
            if(ret==0) // Alg is ANSI 9.19
            {



                Log.d(LogTag, "rawData:"+ Funs.bytesToHexString(databuf)+"\n");
                Log.d(LogTag, "mac:"+ Funs.bytes2HexString(macout,macoutlen[0])+"\n");
                Log.d(LogTag, "KSN:"+Funs.bytes2HexString(macKsn,10)+"\n");

            }
            else
                Log.d(LogTag, "calculateMACOfDUKPTExtend ret="+ ret+"\n");

        }

//	   ret=mEmvApi.DukptInitial(3,bsBdkBuf,bsBdkBuf.length,bsKsnBuf1,bsKsnBuf1.length,bsIpekBuf1,0);
//       if(ret==0)
//       {
//           Log.d("applog","DukptInitial success!");
//       }
        //os 5.1
        //mode 1-PIN 2-MAC 3-Trackdata
        //iKeyType 1-MSR 2-EMV 3-PIN (3套DUKPT) 4-MAC
//        ret=mEmvApi.DukptEcryptData(1,3,pinblockbuf,8,pinblockout,pinblockoutlen,pinKsn);
//       if(ret==0)
//       {
//           Log.d("applog"," \nDukptEcryptData Pinblock ==" + Funs.bytes2HexString(pinblockout,pinblockoutlen[0]));
//           Log.d("applog"," \nDukptEcryptData ksn==" + Funs.bytes2HexString(pinKsn,10));
//
//       }
//       ret=mEmvApi.DukptEcryptData(3,3,databuf,databuf.length,pinblockout,pinblockoutlen,pinKsn);
//       if(ret==0)
//       {
//           Log.d("applog"," \nDukptEcryptData data ==" + Funs.bytes2HexString(pinblockout,pinblockoutlen[0]));
//           Log.d("applog"," \nDukptEcryptData ksn==" + Funs.bytes2HexString(pinKsn,10));
//
//       }

      /* ret=mSEManager.calculateMACOfDUKPT(macdatabuf,macdatabuf.length,pinblockout,pinblockoutlen,pinKsn,pinksnoutlen);
       if(ret==0)
       {
           Log.d("applog"," \ncalculateMACOfDUKPTExtend mac ==" + Funs.bytes2HexString(pinblockout,pinblockoutlen[0]));
           Log.d("applog"," \ncalculateMACOfDUKPTExtend ksn==" + Funs.bytes2HexString(pinKsn,10));

       }*/

        //
//
//       ret = mEmvApi.calcMac(5,5,datas.length,datas,response,0x01);
//       if(ret == 0) Log.d("applog"," \ncalcMac ANSI 9.9==" + Funs.bytes2HexString(response,8));
//        ret = mEmvApi.calcMac(4,5,datas.length,datas,response,0x04);
//        if(ret == 0) Log.d("applog"," \ncalcMac NCCC 9.9==" + Funs.bytes2HexString(response,8));
//       ret = mEmvApi.calcMac(4,5,datas.length,datas,response,0x00);
//       if(ret == 0) Log.d("applog"," \ncalcMac XOR==" + Funs.bytes2HexString(response,8));
//       ret = mEmvApi.calcMac(4,5,datas.length,datas,response,0x03);
//       if(ret == 0) Log.d("applog"," \ncalcMac UP_POS==" + Funs.bytes2HexString(response,8));




    }




    public void my_InitTestKey()
    {

        Log.i(LogTag, "MainActivity  my_testEmv");
        Hashtable<String, Object> data = new Hashtable<String, Object>();
        String libvers;
        String jarvers;




        libvers = mKernelApi.getEMVLibVers(ContantPara.CardSlot.ICC);
        jarvers = mKernelApi.getEMVjarVers();

        Log.d("applog", "libvers:"+libvers);
        Log.d("applog", "jarvers:"+jarvers);

        SEManager mSeManager = new SEManager();
        byte[] ResponseData =  new byte[256];
        byte[] ResLen =  new byte[256];





        int iret;
        byte[] pinMkey = StrToHexByte("31be3c4d151c17cfaec34d3c1fbc1b1d");//31be3c4d151c17cfaec34d3c1fbc1b1d
        byte[] pinkey  = StrToHexByte("D7E8E9C50607C5CA80F6A06FF18B187E");//D7E8E9C50607C5CA80F6A06FF18B187E

        mSeManager.deleteKey(4, 1, ResponseData, ResLen);
        iret = mSeManager.downloadKey(0x04, 1, 0, pinMkey, 16, ResponseData, ResLen);//pin Mkey
        Log.i("applog", "downloadKey Master Key:"+iret);
        mSeManager.deleteKey(2, 1, ResponseData, ResLen);
        iret = mSeManager.downloadKey(0x02 , 1, 1, pinkey, 16, ResponseData, ResLen);//pinkey
        Log.i("applog", "downloadKey Pin Key:"+iret);





        Log.i(LogTag, "MainActivity  return ");

    }


    public void StartKernel(final ContantPara.CheckCardMode checkCardMode)
    {
        new Thread(new Runnable() {


            public void run() {
                try {
                    Hashtable<String, Object> data = new Hashtable<String, Object>();
                    data.put("checkCardMode",checkCardMode);//
                    data.put("currencyCode", "682");//682
                    data.put("emvOption", ContantPara.EmvOption.START);  // START_WITH_FORCE_ONLINE
                    data.put("amount", EmvActivity.edtInput.getText().toString());
                    data.put("cashbackAmount", "");
                    data.put("checkCardTimeout", "30");// Check Card time out .Secondd
                    Log.d("applog", "amount:"+EmvActivity.edtInput.getText().toString());
                    if(isRefound)
                    {
                        data.put("transactionType","20");
                    }
                    else 
                    {
                        data.put("transactionType","00"); //00-goods 01-cash 09-cashback 20-refund
                    }
                    data.put("isEnterAmtAfterReadRecord", isEnterAmtAfterReadRecord);
                    data.put("FallbackSwitch", "0");//0- close fallback 1-open fallback
                    data.put("supportDRL", true); // support Visa DRL?
                    data.put("enableBeeper", false);
                    data.put("enableTapSwipeCollision", false);
                    //data.put("enableEncMagStripe", "1");
                    //data.put("NeedFallBackTryTimes", "3");
                    //
                    //data.put("DisableCheckMSRFormat", "1");
                    //data.put("enableTransTypeMatchAID", false);
                    mKernelApi.startKernel(data);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }





    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emv);



        edtInput = (EditText)findViewById(R.id.editText);
        edtInput.setText("2.00"); //amount is 2.00

        mSEManager = new SEManager();
        param = new Bundle();
        param1=new Bundle();
        mContext = this;


        mEmvListener = new MyEmvListener();

        mKernelApi.setListener(mEmvListener);
        mKernelApi.setContext(mContext);

        //0-disable kernel log  1-enable kernel log
        mKernelApi.LogOutEnable(1);//contactless: adb pull /sdcard/UROPE/TraceCL.txt D:\log\   export log
        //eg: contact:adb pull /sdcard/UROPE/Trace.txt D:\log

        mHandler = new MyHandler(mContext.getMainLooper());

        text1 = (TextView) findViewById(R.id.autoCompleteTextView2);


        Log.d("applog","SDK version:"+mKernelApi.getEMVjarVers());
        Log.d("applog","EMV lib version:"+mKernelApi.getEMVLibVers(ContantPara.CardSlot.ICC));
        Log.d("applog","PayPass lib version:"+mKernelApi.getNFCLibVers((byte)0x02));
        Log.d("applog","PayWave lib version:"+mKernelApi.getNFCLibVers((byte)0x03));
        Log.d("applog","Amex lib version:"+mKernelApi.getNFCLibVers((byte)0x04));

        SEManager manager=new SEManager();
        byte[] resp=new byte[128];
        byte[] resplen=new byte[2];
        manager.getFirmwareVersion(resp,resplen);
        byte[] version=new byte[resplen[0]];
        System.arraycopy(resp,0, version,0, resplen[0]);
        Log.d("applog", "SE version:"+new String(version)+"\nMODEL:"+Build.MODEL+"\nID:"+Build.ID+"\nFINGERPRINT:"+Build.FINGERPRINT+"\nHARDWARE:"+Build.HARDWARE+"\nBRAND:"+Build.BRAND+"\nSerial:"+Build.SERIAL);

        //LOG_KERNEL_AIDLIST_CAPKLIST();

        //  my_InitTestKey();



        text1.setText("--emv result--");

        btnStartEmv =(Button)findViewById(R.id.btn_startEmv_id);//start check card and do transaction
        btnStartEmv.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                StartKernel(ContantPara.CheckCardMode.SWIPE_OR_INSERT_OR_TAP);
                text1.setText("--Please Insert/Tap/Swipe Your Card!--");
            }
        });

        btnmsr =(Button)findViewById(R.id.button2);//init aid and capk param click once
        btnmsr.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                text1.setText("--Init AID/CAPK Paramters Start,Please Waiting !!!--");

                new Thread(new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            init_emvParam();
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).start();



            }
        });
        btnTest =(Button)findViewById(R.id.button3);//cancel check card
        btnTest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mKernelApi.abortKernel();//abort kernel
                Log.d("applog","CheckCardIsOut()="+CheckCardIsOut());

                isLoop=false;

            }
        });


    }

    private void LOG_KERNEL_AIDLIST_CAPKLIST()
    {
        int i;
        List<String> AidList=mKernelApi.getAIDList(ContantPara.CardSlot.ICC);
        for(i=0;i<AidList.size();i++)
        {
            Log.d("applog", "ContactAID:"+AidList.get(i));
        }

        List<String> AidListCL=mKernelApi.getAIDList(ContantPara.CardSlot.PICC);
        for(i=0;i<AidListCL.size();i++)
        {
            Log.d("applog", "ContactlessAID:"+AidListCL.get(i));
        }

        List<Hashtable<String,String>> CapkList=mKernelApi.getCAPKList(ContantPara.CardSlot.ICC);
        for(i=0;i<CapkList.size();i++)
        {
            Log.d("applog", "ContactCAPK_RID:"+CapkList.get(i).get("RID")+"  ContactCAPK_Index:"+CapkList.get(i).get("Index"));
        }

        List<Hashtable<String,String>> CapkListCL=mKernelApi.getCAPKList(ContantPara.CardSlot.PICC);
        for(i=0;i<CapkListCL.size();i++)
        {
            Log.d("applog", "ContactlessCAPK_RID:"+CapkListCL.get(i).get("RID")+"  ContactlessCAPK_Index:"+CapkListCL.get(i).get("Index"));
        }

        List<EmvAidData> AidDetiallist=mKernelApi.getEmvAIDDetail();
        for(i=0;i<AidDetiallist.size();i++)
        {
            Log.e("applog", "aid:"+AidDetiallist.get(i).aid +" appVersion:"+AidDetiallist.get(i).appVersion);
            Log.d("applog", "AppSelIndicator:"+AidDetiallist.get(i).AppSelIndicator +" TerminalAppPriority:"+AidDetiallist.get(i).TerminalAppPriority);
            Log.d("applog", "contactTACDefault:"+AidDetiallist.get(i).contactTACDefault +" contactTACDenial:"+AidDetiallist.get(i).contactTACDenial);
            Log.d("applog", "contactTACOnline:"+AidDetiallist.get(i).contactTACOnline +" defaultTDOL:"+AidDetiallist.get(i).defaultTDOL);
            Log.d("applog", "defaultDDOL:"+AidDetiallist.get(i).defaultDDOL +" ThresholdValue:"+AidDetiallist.get(i).ThresholdValue);
            Log.d("applog", "TargetPercentage:"+AidDetiallist.get(i).TargetPercentage +" MaxTargetPercentage:"+AidDetiallist.get(i).MaxTargetPercentage);
            Log.d("applog", "contactlessCVMRequiredLimit:"+AidDetiallist.get(i).contactlessCVMRequiredLimit +" contactlessFloorLimit:"+AidDetiallist.get(i).contactlessFloorLimit);
            Log.d("applog", "contactlessTransactionLimit:"+AidDetiallist.get(i).contactlessTransactionLimit +" terminalFloorLimit:"+AidDetiallist.get(i).terminalFloorLimit);
            Log.d("applog", "AcquirerIdentifier:"+AidDetiallist.get(i).AcquirerIdentifier +" terminalFloorLimitCheck:"+AidDetiallist.get(i).terminalFloorLimitCheck);
        }

        List<ContactlessAidData> AidDetiallistCL=mKernelApi.getNfcAIDDetail();
        for(i=0;i<AidDetiallistCL.size();i++)
        {
            Log.e("applog", "ApplicationIdentifier:"+AidDetiallistCL.get(i).ApplicationIdentifier);
            Log.d("applog", "NoOnDeviceCVM:"+AidDetiallistCL.get(i).NoOnDeviceCVM);//for master
            Log.d("applog", "OnDeviceCVM:"+AidDetiallistCL.get(i).OnDeviceCVM);//for master
            Log.d("applog", "ReaderCVMRequiredLimit:"+AidDetiallistCL.get(i).ReaderCVMRequiredLimit);//for master
            Log.d("applog", "ReaderContactlessFloorLimit:"+AidDetiallistCL.get(i).ReaderContactlessFloorLimit);//for master
            Log.d("applog", "TransactionLimit:"+AidDetiallistCL.get(i).TransactionLimit);//for other kernel
            Log.d("applog", "CvmRequiredLimit:"+AidDetiallistCL.get(i).CvmRequiredLimit);//for other kernel
            Log.d("applog", "FloorLimit:"+AidDetiallistCL.get(i).FloorLimit);//for other kernel
            Log.d("applog", "EmvTerminalFloorLimit:"+AidDetiallistCL.get(i).EmvTerminalFloorLimit);
        }
    }


    public void displayMessage(String messages)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog;
        alertDialog = dialogBuilder.create();
        String text = "";

        text = text + messages + "\r\n";

        alertDialog.setMessage(text);
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.INVISIBLE);

        if (alertTimer != null) {
            alertTimer.cancel();
        }
        alertTimer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long l) {
            }
            @Override
            public void onFinish() {
                alertDialog.cancel();
            }
        }.start();
    }

    public void displayApplicationSelectMenu(ArrayList<String> applicationLabels) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        AlertDialog alertDialog1 = dialogBuilder.create();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(alertDialog1.getContext(), android.R.layout.select_dialog_singlechoice);



        for (String s : applicationLabels) {
            arrayAdapter.add(s);
        }
        dialogBuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,final int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mKernelApi.selectApplication(which);
                    }
                }).start();


            }
        });
        dialogBuilder.show();
    }

    //init aid and capk
    public void init_emvParam()
    {
        EmvNfcKernelApi.getInstance().updateAID(ContantPara.Operation.CLEAR,null);
        EmvNfcKernelApi.getInstance().updateCAPK(ContantPara.Operation.CLEAR,null);
        EmvNfcKernelApi.getInstance().updateTerminalParamters(ContantPara.CardSlot.UNKNOWN,"9F1C0831323334353637389F4005F000F0A0019F1A0208409F3303E0F8C89F3501225F360102DF020101DF030101DF050100"+"9F1E08"+GetSN());//DF02---random trans select enable  DF03--Except file check enable DF04--Support SM DF05-- Valocity Check enable
        TestEmv.initEMV_AID_CAPK();   
        TestEmv.init_NfcAid_CAPK();
        TestEmv.init_BlackList();
        EmvActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayMessage("Init AID/CAPK Parameters Success!");
                text1.setText("Init AID/CAPK Parameters Success!");
            }
        });

    }

    //contact online pin
    public void emv_proc_onlinePin()
    {
        Log.i("applog", "emv_proc_onlinePin");

        if(isDUKPT)
            param.putInt("PINKeyNo", INDEX_DUKPT_PIN);
        else
            param.putInt("PINKeyNo", INDEX_WK);
        String cardno = GetCardNo();


        Log.i("applog", "emv_proc_onlinePin cardno "+cardno);
        param.putString("cardNo", cardno);
        param.putBoolean("sound", true);
        param.putBoolean("onlinePin", true);
        param.putBoolean("FullScreen", true);
        param.putLong("timeOutMS", 60000);
        param.putString("supportPinLen","0,4,5,6,7,8,9,10,11,12"); // "4,4");   //
        param.putString("title", "Security Keyboard");
        param.putString("message", "Please Enter PIN, "+"\n $"+EmvActivity.edtInput.getText().toString());// use your real amount

////////////////////////////////////sample example////////////////////////
//        short [] textSize= {10, 10, 10, 10, 10, 10, 10 };   //  10-32  // 720*1280
//        short [] leftMargin= {10, 10, 10, 10, 10, 10, 10 };
//        short [] topMargin= {10, 10, 10, 10, 10, 10, 10 };
//        short [] rightMargin= {10, 10, 10, 10, 10, 10, 10 };
//        short [] bottomMargin= {10, 10, 10, 10, 10, 10, 50 };
//        int [] backgroundColor= {0xff0C9213, 0xff0C9213, 0, 0, 0xFFFF0000, 0xffFFFE00, 0xff0C9213 };
//                               //Title,message,input box,number,  cancel,     delete,      ok
//        param.putShortArray("textSize", textSize);  // 字体大小
//        param.putShortArray("leftMargin", leftMargin);  // 左侧偏移
//        param.putShortArray("topMargin", topMargin);  // 顶部偏移
//        param.putShortArray("rightMargin", rightMargin);  // 右侧偏移
//        param.putShortArray("bottomMargin", bottomMargin);  // 底部偏移
//        // 背景颜色，目前只能设置 cancel, Delete, Enter 颜色
//        param.putIntArray("backgroundColor", backgroundColor);
//        String [] numberText = {"zero", "one", "2", "3", "4", "5", "6", "7", "8", "9" };
//        param.putStringArray("numberText", numberText);
//        param.putString("cancelText", "cancel1");
//        param.putString("deleteText", "delete1");
//        param.putString("okText", "enter1");
////////////////////////////////////////////////////////////////////////////

        param.putBoolean("ShowLine", false);
        param.putShortArray("textSize",new short[]{20, 30, 40, 50, 40, 30, 20});
        param.putShortArray("leftMargin",new short[]{20, 30, 40, 50, 40, 30, 20});
        param.putShortArray("topMargin",new short[]{20, 30, 40, 50, 40, 30, 20});
        param.putShortArray("rightMargin",new short[]{20, 30, 40, 50, 40, 30, 20});
        param.putShortArray("bottomMargin",new short[]{20, 30, 40, 50, 40, 30, 20});
        param.putStringArray("numberText",new String[]{"zero","one","two","three","four","five","six","seven","eight","nine"});
        param.putIntArray("backgroundColor",new int[]{Color.BLUE, Color.YELLOW, Color.GREEN, Color.GRAY, Color.RED, Color.BLACK, Color.LTGRAY});
        param.putString("deleteText", "删除");
        param.putString("cancelText", "取消");
        param.putString("okText", "确定");

        param.putBoolean("randomKeyboard", false);

        Log.i("applog", "getPinBlockEx ");
        EmvActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                if(isDUKPT)
                    PinPadProviderImpl.getInstance().GetDukptPinBlock(param,pinpadlistener);
                else
                    PinPadProviderImpl.getInstance().getPinBlockEx(param, pinpadlistener);

            }
        });



    }


    public static String GetCardNo()
    {
        String cardno = EmvNfcKernelApi.getInstance().getValByTag((int)0x5A);
        if(cardno == null || cardno.equals(""))
        {
            cardno = EmvNfcKernelApi.getInstance().getValByTag((int)0x57);
            if(cardno == null || cardno.equals(""))
                return "";
            cardno=cardno.substring(0,cardno.toUpperCase().indexOf("D"));
        }

        if ((cardno.charAt(cardno.length() - 1) == 'f') || (cardno.charAt(cardno.length() - 1) == 'F')
                || cardno.charAt(cardno.length() -1) == 'd' || cardno.charAt(cardno.length() -1) == 'D')
            cardno = cardno.substring(0, cardno.length() - 1);
        return cardno;
    }



    //Android os 8.1 contactless online pin callback
    private class AdminInputListener implements PinInputListener {

        @Override
        public void onInput(int len, int key) {

        }

        @Override
        public void onConfirm(byte[] pinBlock, boolean isNonePin) {
            if(isNonePin)
            {
               // mKernelApi.bypassPinEntry();//bypass
                mKernelApi.ProcOnlinePinAgain();
            }
            else
            {

                Log.d("applog", "pinblock:"+new String(pinBlock));
                mKernelApi.sendPinEntry();
            }
        }
        @Override
        public void onConfirm_dukpt(byte[] PinBlock, byte[] ksn) {
            if(PinBlock==null)
            {
                // mKernelApi.bypassPinEntry();//bypass
                mKernelApi.ProcOnlinePinAgain();
            }
            else
            {
                Log.d("applog", "pinblock:"+new String(PinBlock));
                Log.d("applog", "ksn:"+new String(ksn));
                mKernelApi.sendPinEntry();
            }
        }

        @Override
        public void onCancel() {
            Log.d("applog", "PINPAD cancel");
            mKernelApi.cancelPinEntry();
        }

        @Override
        public void onTimeOut() {
            mKernelApi.cancelPinEntry();
        }

        @Override
        public void onError(int i) {
            mKernelApi.cancelPinEntry();
        }
    }


    public static String GetSN()
    {
        DeviceManager deviceManager=new DeviceManager();
        String SN=deviceManager.getDeviceId();
        if(SN.equals("")||SN==null||SN.length()<8)
            return "3030303030303030";
        int SNlen=SN.length();
        SN=Funs.convertStringToHex(SN.substring(SNlen-8,SNlen));
        return SN;
    }

    // use in OS 8.0 ,offline pin verify
    public int proc_offlinePin(final int pinEntryType,final boolean isLastPinTry, Bundle bundle)
    {
        int iret = 0;

        // TODO Auto-generated method stub
        final Bundle emvBundle = bundle;


        Log.d("applog", "proc_offlinePin pinEntryType = " + pinEntryType +" isLastPinTry="+isLastPinTry) ;

        Bundle paramVar = new Bundle();
        paramVar.putInt("inputType", 3); //Offline PlainPin
        paramVar.putInt("CardSlot", 0);

        paramVar.putBoolean("sound", true);
        paramVar.putBoolean("onlinePin", false);
        paramVar.putBoolean("FullScreen", true);
        paramVar.putLong("timeOutMS", 60000);
        paramVar.putString("supportPinLen", "0,4,5,6,7,8,9,10,11,12");
        paramVar.putString("title", "Security Keyboard");
        paramVar.putBoolean("randomKeyboard", false);
        int pinTryTimes=bundle.getInt("PinTryTimes");
        boolean isFirst=bundle.getBoolean("isFirstTime", false);
        Log.d("applog", "PinTryTimes:"+pinTryTimes);
        if(isLastPinTry)
        {
            if(isFirst)
                paramVar.putString("message", "Please input PIN \nLast PIN Try");
            else
                paramVar.putString("message", "Please input PIN \nWrong PIN \nLast Pin Try");
        }
        else
        {
            if(isFirst)
                paramVar.putString("message", "Please input PIN \n");
            else
            {
                paramVar.putString("message","Please input PIN \nWrong PIN \nPin Try Times:"+pinTryTimes);
            }
        }


        if(pinEntryType == 1)
        {
            paramVar.putInt("inputType", 4); //Offline CipherPin

            final byte[] pub   = emvBundle.getByteArray("pub");
            final int[] publen = emvBundle.getIntArray("publen");
            final byte[] exp   = emvBundle.getByteArray("exp");
            final int[] explen = emvBundle.getIntArray("explen");

            Log.d("applog", "ModuleLen = " + publen[0] + ": " + Funs.bytesToHexString(pub));
            Log.d("applog", "ExponentLen = " + explen[0] + ": " + Funs.bytesToHexString(exp));



            int ModuleLen = publen[0];
            int ExponentLen = explen[0];
            byte[] Module = new byte[ModuleLen];
            byte[] Exponent = new byte[ExponentLen];

            if(ModuleLen==0||ExponentLen==0)
            {
                mKernelApi.sendOfflinePINVerifyResult(-198);
                return 0;
            }

            System.arraycopy(pub, 0, Module, 0, ModuleLen);
            System.arraycopy(exp, 0, Exponent, 0, ExponentLen);

            paramVar.putInt("ModuleLen", ModuleLen);//Modulus length
            paramVar.putString("Module", Funs.bytesToHexString(Module));//Module
            paramVar.putInt("ExponentLen", ExponentLen);//Exponent length
            paramVar.putString("Exponent", Funs.bytesToHexString(Exponent));//Exponent
        }


        Log.d("applog", "proc_offlinePin getPinBlockEx start");

        paramVar.putInt("PinTryMode",1);
        paramVar.putString("ErrorMessage","Incorrect PIN, # More Retries");
        paramVar.putString("ErrorMessageLast","Incorrect PIN, Last Chance");


        SEManager se = new SEManager();
        iret=se.getPinBlockEx(paramVar, new IInputActionListener.Stub() {
            @Override
            public void onInputChanged(int type, int result, Bundle bundle)
            {
                Bundle resultBundle = bundle;
                try
                {
                    //    7101~7115 The number of remaining PIN tries(7101 PIN BLOCKED   7102 the last one chance  7103 two chances ....)
                    //		7006 PIN length error
                    //		7010 防穷举出错
                    //		7016 Wrong PIN
                    //		7071 The return code is wrong
                    //		7072 IC command failed
                    //		7073 Card data error
                    //		7074 PIN BLOCKED
                    //		7075 Encryption error
                    //
                    //The offline PIN verification result is sent to the kernel
                    //   use api EmvApi.sendOfflinePINVerifyResult();
                    //		    (-198)     //Return code error
                    //		    (-202)     //IC command failed
                    //		    (-192)     //PIN BLOCKED
                    //          (-199)     //user cancel or Pinpad timeout
                    //		    (1)        //bypass
                    //		    (0)        //success

                    Log.i("applog", "proc_offlinePin：getPinBlockEx===onInputChanged：type=" + type + "，result=" + result);

                    if (type == 2)
                    {// entering PIN
                    }
                    else if (type==0) //bypass
                    {
                        if(result==0)
                        {
                            Log.d("applog", "proc_offlinePin bypass");
                            mKernelApi.sendOfflinePINVerifyResult(1);//bypass
                        }
                        else
                        {
                            mKernelApi.sendOfflinePINVerifyResult(-198);//return code error
                        }
                    }
                    else if(type==3)//Offline plaintext
                    {
                        Log.d("applog", "proc_offlinePin Plaintext offline");
                        if(result==0)
                        {
                            mKernelApi.sendOfflinePINVerifyResult(0);//Offline plaintext verify successfully
                        }
                        else
                        { //Incorrect PIN, try again
                            String arg1Str = result+"";
                            if(arg1Str.length()>=4 && "71".equals(arg1Str.subSequence(0, 2)))
                            {
                                if("7101".equals(arg1Str))
                                {
                                    mKernelApi.sendOfflinePINVerifyResult(-192);//PIN BLOCKED
                                }
                                else
                                {
                                    if("7102".equals(arg1Str))
                                    {
                                        emvBundle.putBoolean("isFirstTime", false);
                                        emvBundle.putInt("PinTryTimes",1);
                                        proc_offlinePin(pinEntryType,true, emvBundle);//try again the last pin try
                                    }
                                    else
                                    {
                                        emvBundle.putBoolean("isFirstTime", false);
                                        emvBundle.putInt("PinTryTimes",(Integer.valueOf(arg1Str.substring(2,4))-1));
                                        proc_offlinePin(pinEntryType,false, emvBundle);//try again
                                    }

                                }
                            }
                            else if("7074".equals(arg1Str))
                            {
                                mKernelApi.sendOfflinePINVerifyResult(-192);//PIN BLOCKED
                            }
                            else if("7072".equals(arg1Str)||"7073".equals(arg1Str))
                            {
                                mKernelApi.sendOfflinePINVerifyResult(-202);//IC command failed
                            }
                            else
                            {
                                mKernelApi.sendOfflinePINVerifyResult(-198);//Return code error
                            }
                        }
                    }
                    else if(type==4)//Offline encryption PIN
                    {
                        Log.d("applog", "proc_offlinePin Offline encryption");
                        if(result==0)
                        {
                            mKernelApi.sendOfflinePINVerifyResult(0);//Offline encryption PIN verify successfully
                        }
                        else
                        {
                            String arg1Str = result+"";
                            if(arg1Str.length()>=4 && "71".equals(arg1Str.subSequence(0, 2)))
                            {
                                if("7101".equals(arg1Str))
                                {
                                    mKernelApi.sendOfflinePINVerifyResult(-192);//PIN BLOCKED
                                }
                                else
                                {
                                    Log.d("applog", "proc_offlinePin Offline encryption entry pin again");
                                    if("7102".equals(arg1Str))
                                    {
                                        emvBundle.putBoolean("isFirstTime", false);
                                        emvBundle.putInt("PinTryTimes",1);
                                        proc_offlinePin(pinEntryType,true, emvBundle);//try again the last pin try
                                    }
                                    else
                                    {
                                        emvBundle.putBoolean("isFirstTime", false);
                                        emvBundle.putInt("PinTryTimes",(Integer.valueOf(arg1Str.substring(2,4))-1));
                                        proc_offlinePin(pinEntryType,false, emvBundle);//try again
                                    }
                                }
                            }
                            else if("7074".equals(arg1Str))
                            {
                                mKernelApi.sendOfflinePINVerifyResult(-192);//PIN BLOCKED
                            }
                            else if("7072".equals(arg1Str)||"7073".equals(arg1Str))
                            {
                                mKernelApi.sendOfflinePINVerifyResult(-202);//IC command failed(card removed)
                            }
                            else
                            {

                                mKernelApi.sendOfflinePINVerifyResult(-198);//Return code error
                            }
                        }
                    }
                    else if (type == 0x10)// click Cancel button
                    {
                        mKernelApi.sendOfflinePINVerifyResult(-199); //cancel
                    }
                    else if (type == 0x11)// pinpad timed out
                    {
                        mKernelApi.sendOfflinePINVerifyResult(-199); //timeout
                    }
                    else
                    {
                        mKernelApi.sendOfflinePINVerifyResult(-198);//Return code error
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.d("applog", "proc_offlinePin exception");
                }
            }
        });
        if(iret==-3||iret==-4)
            mKernelApi.sendOfflinePINVerifyResult(-198);
        return iret;
    }

    public boolean CheckCardIsOut()
    {
        IccManager manager=new IccManager();
        int ret=manager.open((byte)0,(byte)1,(byte)1);

        if(ret==0)
        {
            ret=manager.detect();
            if(ret==0)
                return false;
            else
                return true;
        }
        return false;

    }
    public void CheckMsgCard()
    {
        int ret;
        byte[] stripInfo = new byte[1024];
        MagManager magManager=new MagManager();
        magManager.open();
        while(true)
        {
            ret=magManager.checkCard();
            if(ret==0)
            {
                ret=magManager.getAllStripInfo(stripInfo);
                if(ret>0)
                {
                    //   logfile.printLog(TAG + "===stripInfo: " + new String(stripInfo, 0, 113));
                    break;

                }
            }

        }

        magManager.close();
    }

    class MyEmvListener implements EmvListener {


        @Override
        public void onRequestSetAmount()
        {
            Log.i("applog", "MainActivity  onRequestSetAmount");

            if(isEnterAmtAfterReadRecord)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.setClass(EmvActivity.this, AmountActivity.class);
                        startActivity(intent);
                    }
                });

            }
        }






        /*
        Track 1[edit]
        Format B:

        Start sentinel — one character (generally '%')
        Format code="B" — one character (alpha only)
        Primary account number (PAN) — up to 19 characters. Usually, but not always, matches the credit card number printed on the front of the card.
        Field Separator — one character (generally '^')
        Name — 2 to 26 characters
        Field Separator — one character (generally '^')
        Expiration date — four characters in the form YYMM.
        Service code — three characters
        Discretionary data — may include Pin Verification Key Indicator (PVKI, 1 character), PIN Verification Value (PVV, 4 characters), Card Verification Value or Card Verification Code (CVV or CVC, 3 characters)
        End sentinel — one character (generally '?')
        Longitudinal redundancy check (LRC) — it is one character and a validity character calculated from other data on the track.
        */
        /*
Track 2
The Track 2 structure is specified as:[citation needed]

STX : Start sentinel ";"
PAN : Primary Account Number, up to 19 digits, as defined in ISO/IEC 7812-1
FS : Separator "="
ED : Expiration date, YYMM or "=" if not present
SC : Service code, 3 digits or "=" if not present
DD : Discretionary data, balance of available digits
ETX : End sentinel "?"
LRC : Longitudinal redundancy check, calculated according to ISO/IEC 7811-2

Service code values common in financial cards:

First digit

1: International interchange OK
2: International interchange, use IC (chip) where feasible
5: National interchange only except under bilateral agreement
6: National interchange only except under bilateral agreement, use IC (chip) where feasible
7: No interchange except under bilateral agreement (closed loop)
9: Test
Second digit

0: Normal
2: Contact issuer via online means
4: Contact issuer via online means except under bilateral agreement
Third digit

0: No restrictions, PIN required
1: No restrictions
2: Goods and services only (no cash)
3: ATM only, PIN required
4: Cash only
5: Goods and services only (no cash), PIN required
6: No restrictions, use PIN where feasible
7: Goods and services only (no cash), use PIN where feasible

 */
        //  TLV TLV TLV
        public void my_process_trackdata(String StripStr)
        {
            byte [] Stripbuff =  StrToHexByte(StripStr);
            int track1len,track2len,track3len;
            byte [] track1 = new byte[128];
            byte [] track2 = new byte[128];
            byte [] track3 = new byte[128];
            int i=0,j=0;
            track1len = Stripbuff[1];
            System.arraycopy(Stripbuff, 2, track1, 0, track1len);
            track2len = Stripbuff[2+track1len+1];
            System.arraycopy(Stripbuff, 4+track1len, track2, 0, track2len);

            byte [] expirationDate = new byte[4];
            byte [] ServiceCode = new byte[3];

            for (i = 1; i < track2len; i++){
                if (track2[i] == '=') break;
            }
            System.arraycopy(track2, i+1, expirationDate, 0, 4);
            System.arraycopy(track2, i+1+4, ServiceCode, 0, 3);


            Log.d("applog", "expirationDate "+ new String(expirationDate));
            Log.d("applog", "ServiceCode "+ new String(ServiceCode));
            byte [] cardholderName = new byte[27];
            if(track1len>0) {
                for (i = 1; i < track1len; i++) {
                    if (track1[i] == '^') break;
                }
                for (j = i+1; j < track1len; j++) {
                    if (track1[j] == '^') break;
                }
                System.arraycopy(track1, i+1, cardholderName, 0, j-i-1);
                Log.d("applog", "cardholderName "+ Funs.bytes2HexString(cardholderName, j-i-1));
                Log.d("applog", "cardholderName "+ new String(cardholderName));
            }

        }

        @Override
        public void onReturnCheckCardResult(ContantPara.CheckCardResult checkCardResult, Hashtable<String, String> hashtable)
        {
            Log.i(LogTag, "MainActivity  onReturnCheckCardResult checkCardResult ="+checkCardResult);
            Log.d(LogTag , hashtable.toString());
            Log.d("applog", "POS Entry Mode:"+hashtable.get("POSEntryMode"));
            Log.d("applog", "FallbackType:"+hashtable.get("FallbackType"));


            if(checkCardResult == ContantPara.CheckCardResult.MSR)
            {
                Message tempMsg = mHandler.obtainMessage();
                tempMsg.what = ionShowMsg;
                tempMsg.obj = " MAG CARD SWIPE SUCCESS  !";
                mHandler.sendMessage(tempMsg);
                final String StripStr = hashtable.get("StripInfo").toUpperCase();
                final String CardNo=hashtable.get("CardNo");
                String  ExpiredDate="";
                String ServiceCode="";
                String hstr1=Funs.TLV_Find("D1",StripStr);
                String hstr2=Funs.TLV_Find("D2",StripStr);
                String hstr3=Funs.TLV_Find("D3",StripStr);
                final String track1,track2,track3;
                if(hstr1.equals(""))
                {
                    track1="";
                }
                else
                {
                    track1=new String(StrToHexByte(hstr1));
                }
                if(hstr2.equals(""))
                {
                    track2="";
                }
                else
                {
                    track2=new String(StrToHexByte(hstr2));
                }
                if(hstr3.equals(""))
                {
                    track3="";
                }
                else
                {
                    track3=new String(StrToHexByte(hstr3));
                }

                int index = track2.indexOf("=");
                if (index != -1)
                {
                    index++;
                    ExpiredDate = track2.substring(index, index + 4);
                    ServiceCode = track2.substring(index + 4, index + 4 + 3);

                }


                final String finalEXPIRED_DATE = ExpiredDate;
                final String finalSERVICE_CODE = ServiceCode;
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        text1.setText("StripTnfo:\n"+"track1:"+track1+"\ntrack2:"+track2+"\ntrack3:"+track3+"\nCardNo="+CardNo+"\nExpiredDate:"+ finalEXPIRED_DATE+"\nServiceCode:"+ finalSERVICE_CODE);
                    }
                });

                // my_process_trackdata(StripStr);
            }
            else if(checkCardResult == ContantPara.CheckCardResult.NEED_FALLBACK)
            {
                Message tempMsg = mHandler.obtainMessage();
                tempMsg.what = ionShowMsg;
                tempMsg.obj = " NEED_FALLBACK ! Please Tap or Swipe Card!";
                mHandler.sendMessage(tempMsg);
                while(mKernelApi.CheckCardIsOut(10000)==false)
                {


                }
                Log.d("applog", "Card is Removed:"+mKernelApi.CheckCardIsOut(1000));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StartKernel(ContantPara.CheckCardMode.SWIPE_OR_TAP);
                    }
                });

            }
            else if(checkCardResult == ContantPara.CheckCardResult.BAD_SWIPE)
            {

                Message tempMsg = mHandler.obtainMessage();
                tempMsg.what = ionShowMsg;
                tempMsg.obj = " BAD_SWIPE !";
                mHandler.sendMessage(tempMsg);
                getMySound(EmvActivity.this).playSound(SOUND_TYPE_ERR);
            }
            else if(checkCardResult == ContantPara.CheckCardResult.NOT_ICC)
            {



                Message tempMsg = mHandler.obtainMessage();


                tempMsg.what = ionShowMsg;
                tempMsg.obj = " NOT_ICC, Remove and Insert Card Again!";
                mHandler.sendMessage(tempMsg);

                while(mKernelApi.CheckCardIsOut(10000)==false)
                {


                }
                Log.d("applog", "Card is Removed:"+mKernelApi.CheckCardIsOut(1000));
                Log.i("applog", "runOnUiThread-----NOT_ICC");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StartKernel(ContantPara.CheckCardMode.SWIPE_OR_INSERT_OR_TAP);
                    }
                });


            }
            else if(checkCardResult == ContantPara.CheckCardResult.TIMEOUT)
            {
                Message tempMsg = mHandler.obtainMessage();
                tempMsg.what = ionShowMsg;
                tempMsg.obj = " Check Card Time Out!";
                mHandler.sendMessage(tempMsg);

                Log.i("applog", "Check card time out!");
            }
            else if(checkCardResult == ContantPara.CheckCardResult.CANCEL)
            {
                Message tempMsg = mHandler.obtainMessage();
                tempMsg.what = ionShowMsg;
                tempMsg.obj = " User Cancel Check Card!";
                mHandler.sendMessage(tempMsg);

                Log.i("applog", "Check card Cancel!");
            }
            else if(checkCardResult == ContantPara.CheckCardResult.DEVICE_BUSY)
            {
                Message tempMsg = mHandler.obtainMessage();
                tempMsg.what = ionShowMsg;
                tempMsg.obj = " Check Card Device Busy !";
                mHandler.sendMessage(tempMsg);

                Log.i("applog", "Check card DEVICE_BUSY!");
            }
            else if(checkCardResult == ContantPara.CheckCardResult.USE_ICC_CARD)
            {
                Message tempMsg = mHandler.obtainMessage();
                tempMsg.what = ionShowMsg;
                tempMsg.obj = " Chip Card! Please Use Contact Interface,Please Insert Card!";
                mHandler.sendMessage(tempMsg);
                Log.i("applog", "Chip Card! Please Use Contact Interface,Please Insert Card!");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StartKernel(ContantPara.CheckCardMode.INSERT_OR_TAP);
                    }
                });
            }
            else if(checkCardResult== ContantPara.CheckCardResult.MULT_CARD)
            {
                Message tempMsg = mHandler.obtainMessage();
                tempMsg.what = ionShowMsg;
                tempMsg.obj = "Mult Card! Please Tap one Card!";
                mHandler.sendMessage(tempMsg);
                Log.i("applog", "Mult Card! Please Tap one Card!");

            }
            else if(checkCardResult== ContantPara.CheckCardResult.INSERTED_CARD)
            {

            }


            /*try {
                ; // Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            */

        }

        @Override
        public void onRequestSelectApplication(final ArrayList<String> arrayList) {
            Log.i(LogTag, "MainActivity  onRequestSelectApplication");
            int i;
            for (i = 0  ; i < arrayList.size() ; i++)
            {
                Log.d(LogTag , "app name "+i +" : "+arrayList.get(i));

            }
            //block

            if(i==1)
                mKernelApi.selectApplication(0);
            else
            {
//                //use dialog

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayApplicationSelectMenu(arrayList);
                    }
                });
            }

            // break;
        }






        //if contact online pin verify, will callback
        @Override
        public void onRequestPinEntry(ContantPara.PinEntrySource pinEntrySource)
        {

            Log.i(LogTag, "MainActivity  onRequestPinEntry request online pin");

            if(pinEntrySource == ContantPara.PinEntrySource.KEYPAD )
            {
                emv_proc_onlinePin();
                Log.i(LogTag, "MainActivity  emv_proc_onlinePin over");

            }
            else
            {

            }

        }

        @Override
        public void onRequestOfflinePinEntry(ContantPara.PinEntrySource pinEntrySource, int PinTryCount)
        {
            //usually not use ,For external password keyboard
            Log.i(LogTag, "MainActivity  onRequestOfflinePinEntry");

        }

        @Override
        public void onRequestConfirmCardno()
        {
            Log.i(LogTag, "MainActivity  onRequestConfirmCardno-----------");

            Log.d("applog","CardNo:"+GetCardNo());

            mKernelApi.sendConfirmCardnoResult(true);



            //

        }
        @Override
        public void onRequestFinalConfirm()
        {
            Log.i(LogTag, "MainActivity  onRequestFinalConfirm-----------");
            mKernelApi.sendFinalConfirmResult(true);

        }

        @Override
        public void onRequestOnlineProcess(final String cardTlvData, final String dataKsn)
        {



            Log.i(LogTag, "MainActivity  onRequestOnlineProcess");
            //send s to sever
            Log.i(LogTag, "MainActivity  onRequestOnlineProcess iccdata:"+ cardTlvData);
            Log.i(LogTag, "MainActivity  onRequestOnlineProcess dataKsn:"+dataKsn);
            EmvActivity.this.runOnUiThread(new Runnable()
            {
                public void run()
                {
                    text1.setText("iccdata:" + cardTlvData +"\n"+"KSN:"+dataKsn);
                }
            });

            byte[] tag9F34=Funs.StrToHexByte(mKernelApi.getValByTag(0x9F34));
            if(((tag9F34[0]& 0x3F)==0x01)||((tag9F34[0]& 0x3F)==0x02)||((tag9F34[0]& 0x3F)==0x03)||((tag9F34[0]& 0x3F)==0x04)||((tag9F34[0]& 0x3F)==0x05))
            {
                Log.d("applog", "REQUESTED PIN");

            }
            else
            {
                Log.d("applog", "NOT REQUEST PIN");

            }

            String track2 = mKernelApi.getValByTag(0x57);

            Log.i(LogTag, "MainActivity  onRequestOnlineProcess track2:"+track2);

            String TVR = mKernelApi.getValByTag(0x95);

            Log.i(LogTag, "MainActivity  onRequestOnlineProcess TVR:"+TVR);


            String PSN=mKernelApi.getValByTag(0x5F34);
            Log.i(LogTag, "MainActivity  onRequestOnlineProcess PSN:"+PSN);

            String TermID=mKernelApi.getValByTag(0x9F1C);
            Log.i(LogTag, "MainActivity  onRequestOnlineProcess TermID:"+TermID);

            String AppLable=mKernelApi.getValByTag(0x50);
            if(!TextUtils.isEmpty(AppLable))
            {
                Log.i(LogTag, "MainActivity  onRequestOnlineProcess AppLable:"+new String(Funs.StrToHexByte(AppLable)));
                AppLable=new String(Funs.StrToHexByte(AppLable));

            }
            String PerfName=mKernelApi.getValByTag(0x9F12);
            if(!TextUtils.isEmpty(PerfName))
            {
                Log.i(LogTag, "MainActivity  onRequestOnlineProcess PerfName:"+new String(Funs.StrToHexByte(PerfName)));
                PerfName=new String(Funs.StrToHexByte(PerfName));
            }

            if(AppLable.toUpperCase().contains("DEBIT")||PerfName.toUpperCase().contains("DEBIT"))
            {
                Log.d("applog", "it is a Debit card!");
            }
            else if(AppLable.toUpperCase().contains("CREDIT")||PerfName.toUpperCase().contains("CREDIT"))
            {
                Log.d("applog", "it is a CREDIT card!");
            }
            //String responseData = "8A023030";//just for test  //server return data

            Log.i(LogTag, "tag 9F40:"+mKernelApi.getValByTag(0x9F40));
            Log.d("applog", "GetField55ForSAMA:"+mKernelApi.GetField55ForSAMA());

            Log.d("applog", "getField55ForJIO:"+mKernelApi.getField55ForJIO(1));

            String responseData = "710F860D842400000817C217D34162474C910A1397ECEFC7A6051100128A023030";
            //String responseData =  "8A02303091080102030405060708";
            //true:online success false:online failded
            mKernelApi.sendOnlineProcessResult(true, responseData);
        }


        @Override
        public void onReturnBatchData(final String cardTlvData)
        {
            Log.i(LogTag, "MainActivity  onReturnBatchData");
            Log.i(LogTag, "MainActivity  onReturnBatchData iccdata:"+ cardTlvData);
            EmvActivity.this.runOnUiThread(new Runnable()
            {
                public void run()
                {
                    text1.setText("iccdata:" + cardTlvData );

                }
            });
        }



        @Override
        public void onReturnTransactionResult(final ContantPara.TransactionResult transactionResult)
        {

            Log.i(LogTag, "MainActivity  onReturnTransactionResult  transactionResult="+transactionResult);
            String TVR = mKernelApi.getValByTag(0x95);
            Log.i(LogTag, "MainActivity  onReturnTransactionResult TVR:"+TVR);
            String TSI=mKernelApi.getValByTag(0x9B);
            Log.i(LogTag, "MainActivity  onReturnTransactionResult TSI:"+TSI);
            String IAD=mKernelApi.getValByTag(0x9F10);
            Log.i(LogTag, "MainActivity  onReturnTransactionResult IAD:"+IAD);
            String AC=mKernelApi.getValByTag(0x9F26);
            Log.i(LogTag, "MainActivity  onReturnTransactionResult AC:"+AC);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayMessage(transactionResult.name());
                }
            });


            if(transactionResult== ContantPara.TransactionResult.OFFLINE_APPROVAL||transactionResult==ContantPara.TransactionResult.ONLINE_APPROVAL)
            {
                Log.i(LogTag, "MainActivity onReturnTransactionResult  APPROVED");
                getMySound(EmvActivity.this).playSound(SOUND_TYPE_SUCCESS);
                EmvActivity.this.runOnUiThread(new Runnable() {
                                                   public void run() {

                                                       Message tempMsg = mHandler.obtainMessage();
                                                       tempMsg.what = MyHandler.iOnReturnTransactionResult;
                                                       tempMsg.obj = " TransactionResult = APPROVED !";
                                                       String str = text1.getText().toString();

                                                       text1.setText(str + "   " + tempMsg.obj);

                                                       mHandler.sendMessage(tempMsg);
                                                   }
                                               }
                );

            }
            else if(transactionResult== ContantPara.TransactionResult.OFFLINE_DECLINED||transactionResult==ContantPara.TransactionResult.ONLINE_DECLINED)
            {
                Log.i(LogTag, "MainActivity onReturnTransactionResult  DECLINED");
                getMySound(EmvActivity.this).playSound(SOUND_TYPE_ERR);
                EmvActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Message tempMsg = mHandler.obtainMessage();
                        tempMsg.what = MyHandler.iOnReturnTransactionResult;
                        tempMsg.obj = " TransactionResult = DECLINED !";

                        String str = text1.getText().toString();
                        text1.setText(str+"   "+tempMsg.obj);

                        mHandler.sendMessage(tempMsg);
                    }
                });
            }
            else if(transactionResult== ContantPara.TransactionResult.ICC_CARD_REMOVED)
            {
                Log.i(LogTag, "MainActivity onReturnTransactionResult  ICC_CARD_REMOVED");
                getMySound(EmvActivity.this).playSound(SOUND_TYPE_ERR);
                EmvActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Message tempMsg = mHandler.obtainMessage();
                        tempMsg.what = MyHandler.iOnReturnTransactionResult;
                        tempMsg.obj = " TransactionResult = ICC_CARD_REMOVED !";

                        String str = text1.getText().toString();
                        text1.setText(str+"   "+tempMsg.obj);

                        mHandler.sendMessage(tempMsg);
                    }
                });
            }
            else if(transactionResult== ContantPara.TransactionResult.TERMINATED)
            {
                getMySound(EmvActivity.this).playSound(SOUND_TYPE_ERR);
                EmvActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.i(LogTag, " MainActivity onReturnTransactionResult  TERMINATED");
                        Message tempMsg = mHandler.obtainMessage();
                        tempMsg.what = MyHandler.iOnReturnTransactionResult;
                        tempMsg.obj = " TransactionResult = TERMINATED !";
                        mHandler.sendMessage(tempMsg);
                    }
                });
            }
            else if(transactionResult== ContantPara.TransactionResult.CANCELED_OR_TIMEOUT)
            {
                getMySound(EmvActivity.this).playSound(SOUND_TYPE_ERR);
                EmvActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.i(LogTag, " MainActivity onReturnTransactionResult  CANCELED_OR_TIMEOUT");
                        Message tempMsg = mHandler.obtainMessage();
                        tempMsg.what = MyHandler.iOnReturnTransactionResult;
                        tempMsg.obj = " TransactionResult = CANCELED_OR_TIMEOUT !";
                        mHandler.sendMessage(tempMsg);
                    }
                });

            }
            else if(transactionResult== ContantPara.TransactionResult.CANCELED)
            {
                getMySound(EmvActivity.this).playSound(SOUND_TYPE_ERR);
                EmvActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.i(LogTag, " MainActivity onReturnTransactionResult  CANCELED");
                        Message tempMsg = mHandler.obtainMessage();
                        tempMsg.what = MyHandler.iOnReturnTransactionResult;
                        tempMsg.obj = " TransactionResult = CANCELED !";
                        mHandler.sendMessage(tempMsg);
                    }
                });

            }
            else if(transactionResult== ContantPara.TransactionResult.CARD_BLOCKED_APP_FAIL)
            {
                getMySound(EmvActivity.this).playSound(SOUND_TYPE_ERR);
                EmvActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.i(LogTag, " MainActivity onReturnTransactionResult  CARD_BLOCKED_APP_FAIL");
                        Message tempMsg = mHandler.obtainMessage();
                        tempMsg.what = MyHandler.iOnReturnTransactionResult;
                        tempMsg.obj = " TransactionResult = CARD_BLOCKED_APP_FAIL !";
                        mHandler.sendMessage(tempMsg);
                    }
                });
            }
            else if(transactionResult== ContantPara.TransactionResult.APPLICATION_BLOCKED_APP_FAIL)
            {
                getMySound(EmvActivity.this).playSound(SOUND_TYPE_ERR);
                EmvActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.i(LogTag, " MainActivity onReturnTransactionResult  APPLICATION_BLOCKED_APP_FAIL");
                        Message tempMsg = mHandler.obtainMessage();
                        tempMsg.what = MyHandler.iOnReturnTransactionResult;
                        tempMsg.obj = " TransactionResult = APPLICATION_BLOCKED_APP_FAIL !";
                        mHandler.sendMessage(tempMsg);
                    }
                });
            }
            else if(transactionResult== ContantPara.TransactionResult.INVALID_ICC_DATA)
            {
                getMySound(EmvActivity.this).playSound(SOUND_TYPE_ERR);
                EmvActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.i(LogTag, " MainActivity onReturnTransactionResult  INVALID_ICC_DATA");
                        Message tempMsg = mHandler.obtainMessage();
                        tempMsg.what = MyHandler.iOnReturnTransactionResult;
                        tempMsg.obj = " TransactionResult = INVALID_ICC_DATA !";
                        mHandler.sendMessage(tempMsg);
                    }
                });
            }
            else if(transactionResult == ContantPara.TransactionResult.NO_EMV_APPS)
            {

                EmvActivity.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        Log.i(LogTag, " MainActivity onReturnTransactionResult  NO_EMV_APPS");
                        Message tempMsg = mHandler.obtainMessage();
                        tempMsg.what = MyHandler.iOnReturnTransactionResult;
                        tempMsg.obj = " TransactionResult = NO_EMV_APPS !";
                        mHandler.sendMessage(tempMsg);
                    }
                });

                //..visa case 8, fallback

                /*****
                 MagManager mMagManager = new MagManager();
                 int iret = -1;
                 byte[] StripInfo = new byte[1024];
                 mMagManager.open();
                 while (true)
                 {
                 iret = mMagManager.checkCard();
                 if (iret == 0)
                 {
                 int len = 0;
                 len = mMagManager.getAllStripInfo(StripInfo);
                 Log.i(TAG, "mMagManager.getAllStripInfo(StripInfo) ret = " + len);
                 Log.i(TAG, "mMagManager.getAllStripInfo(StripInfo) buf = " + Funs.bytes2HexString(StripInfo, len));
                 break;
                 }
                 else
                 {
                 Log.i(TAG, "mMagManager.checkCard() iret = " + iret);
                 }
                 try
                 {
                 Thread.sleep(100);
                 } catch (InterruptedException e) {
                 e.printStackTrace();
                 }
                 }
                 ******/
            }
        }

        @Override
        public void onRequestDisplayText(ContantPara.DisplayText displayText)
        {
            Log.i(LogTag, "MainActivity  onRequestDisplayText");
        }



        @Override
        public void onRequestOfflinePINVerify(ContantPara.PinEntrySource pinEntrySource, int pinEntryType, Bundle bundle)
        {
            if(pinEntrySource== ContantPara.PinEntrySource.KEYPAD)
            {//use in os 8.0
                //pinEntryType 0-offline plain pin ,1-offline encrypt pin
                int pinTryTimes=mKernelApi.getOfflinePinTryTimes();
                bundle.putInt("PinTryTimes", pinTryTimes);
                bundle.putBoolean("isFirstTime", true);
                if(pinTryTimes==1)
                    proc_offlinePin(pinEntryType,true,bundle);
                else
                {
                    proc_offlinePin(pinEntryType,false,bundle);
                }

            }
            else
            {

            }

        }

        //if you call mEmvApi.getIssuerScriptResult() ,this will callback
        @Override
        public void onReturnIssuerScriptResult(ContantPara.IssuerScriptResult issuerScriptResult, String s) {
            if(issuerScriptResult==ContantPara.IssuerScriptResult.SUCCESS)
            {
                Log.d(LogTag, "onReturnIssuerScriptResult:"+s);

            }
            else if(issuerScriptResult==ContantPara.IssuerScriptResult.NO_OR_FAIL)
            {
                Log.d("applog", "not issuer script result");
            }
        }



        //contactless Tip message callback
        public void onNFCrequestTipsConfirm(ContantPara.NfcTipMessageID msgID, final String msg)
        {



            if(msgID==ContantPara.NfcTipMessageID.TRY_AGAIN_RESENT_CARD||msgID==ContantPara.NfcTipMessageID.SEE_PHONE_REMOVE_AND_PRESENT_CARD)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayMessage(msg);
                    }
                });
            }

            Message tempMsg = new Message();
            tempMsg.what = SHOW_INFO;
            Bundle bundle = new Bundle();

            if(msgID==ContantPara.NfcTipMessageID.END_APPLICATION)
            {
                getMySound(EmvActivity.this).playSound(SOUND_TYPE_ERR);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else if(msgID==ContantPara.NfcTipMessageID.CARD_READ_OK)
            {
                getMySound(EmvActivity.this).playSound(SOUND_TYPE_SUCCESS);
            }

            Log.d("applog", "requestTipsConfirm:"+"SHOW_INFO="+msg);
            bundle.putString("msg", msg);
            tempMsg.setData(bundle);

            mHandler.sendMessage(tempMsg);


        }


        //go online send data to host ,then import online result
        public void onNFCrequestOnline()
        {

            //should send to host
            Log.d("applog", "onNFCrequestOnline ") ;
            Message tempMsg = new Message();
            tempMsg.what = ionShowMsg;
            tempMsg.obj="request online";
            mHandler.sendMessage(tempMsg);
            try
            {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //from server  true:online success false:online failed
            //just for test 710F860D842400000817C217D34162474C910A1397ECEFC7A605110012

            mKernelApi.sendOnlineProcessResult(true, "8A023030");

        }


        public void onNFCrequestImportPin(int type, int  lasttimeFlag, String amt)
        {

            Log.d("applog", "onNFCrequestImportPin");
            Message tempMsg = new Message();
            tempMsg.what = REQUEST_ONLINEPIN;
            tempMsg.obj = amt ;
            mHandler.sendMessage(tempMsg);

            emv_proc_onlinePin();
        }


        public void onNFCTransResult(ContantPara.NfcTransResult result)
        {
            Message tempMsg = new Message();
            tempMsg.what = ionShowMsg;
            tempMsg.obj=""+result;
            if(result==ContantPara.NfcTransResult.ONLINE_APPROVAL)
                getMySound(EmvActivity.this).playSound(SOUND_TYPE_SUCCESS);
            else if(result==ContantPara.NfcTransResult.CARD_REMOVED)
                getMySound(EmvActivity.this).playSound(SOUND_TYPE_ERR);
            else if(result== ContantPara.NfcTransResult.RETRY)
            {
                StartKernel(ContantPara.CheckCardMode.SWIPE_OR_INSERT_OR_TAP);
                return;
            }
            else if(result== ContantPara.NfcTransResult.OFFLINE_APPROVAL)
            {
                if(isRefound)
                {
                    if(mKernelApi.getValByTag(0x9F2A).toUpperCase().equals("03"))//visa refund
                    {
                        //request online refund success!

                    }

                }
            }
            else if(result== ContantPara.NfcTransResult.TERMINATE)
            {

                if(isRefound)
                {
                    if(mKernelApi.getValByTag(0x9F2A).toUpperCase().equals("02"))//Master refund
                    {
                        //request online refund success!

                    }

                }
                if(mKernelApi.getValByTag(0x9F2A).equals("02"))
                {
                    String TransLimit = mKernelApi.getValByTag(0xDF20);
                    long Amt      =Funs.StrAmtToLong(EmvActivity.edtInput.getText().toString()) ;
                    Log.d("emvlog", "TransLimit:" + TransLimit);
                    Log.d("emvlog", "Amt:" + Amt);
                    if (!TextUtils.isEmpty(TransLimit)&&( Amt > Long.parseLong(TransLimit)))
                    {
                        Log.d("applog","Max Amount exceed");
                    }
                }
            }

            String value=mKernelApi.getValByTag(0x5F20);
            Log.d("applog","5F20="+value);
            String value1=mKernelApi.getValByTag(0xDF20);
            Log.d("applog","DF20="+value1);
            String Track = mKernelApi.getValByTag(0x57);
            Log.i(LogTag, " Track:"+Track);

            Log.i(LogTag, " CardType:"+mKernelApi.getDeviceType());
            mHandler.sendMessage(tempMsg);
        }
        public void onNFCErrorInfor(ContantPara.NfcErrMessageID erroCode, final String strErrInfo)
        {

            Log.d("applog", "errcode:"+erroCode+"   onErrorInfor:"+strErrInfo);




            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    text1.append(" "+"ErrInfo:"+strErrInfo);
                    displayMessage(strErrInfo);
                }
            });

        }



        //save data about go online or offline
        public void onReturnNfcCardData(Hashtable<String, String> hashtable)
        {
            final String ksn = hashtable.get("KSN");
            final String TrackData = hashtable.get("TRACKDATA");
            final String EmvData = hashtable.get("EMVDATA");
            final String QPBOCType=hashtable.get("QPBOCTYPE");

            String tagValue = mKernelApi.getValByTag(0x9F26);
            if(tagValue!=null){
                Log.d("applog", "tagValue 0x9F26:" + tagValue);
            }
            String Track = mKernelApi.getValByTag(0x57);

            Log.i(LogTag, " Track:"+Track);

            Log.i(LogTag, " CardNo:"+GetCardNo());

            tagValue = mKernelApi.getValByTag(0x9F24);//use for contact or contactless
            if(tagValue!=null) {
                Log.d("applog", "tagValue 0x9F24:" + tagValue);
            }
            String TermID=mKernelApi.getValByTag(0x9F1C);
            Log.i(LogTag, "tagValue 0x9F1C TermID:"+TermID);

            String TVR = mKernelApi.getValByTag(0x95);
            Log.d("applog", "tagValue 0x95:" + TVR);

            tagValue =mKernelApi.getValByTag(0x9F41);
            if(tagValue!=null)
                Log.d("applog","tagValue 0x9F41:"+tagValue);
            tagValue =mKernelApi.getValByTag(0x9F1E);
            if(tagValue!=null)
                Log.d("applog","tagValue 0x9F1E:"+tagValue);
            tagValue =mKernelApi.getValByTag(0x5F24);
            if(tagValue!=null)
                Log.d("applog","tagValue 0x5F24:"+tagValue);
            String AppLable=mKernelApi.getValByTag(0x50);
            if(!TextUtils.isEmpty(AppLable))
            {
                Log.i(LogTag, "AppLable:"+new String(Funs.StrToHexByte(AppLable)));
                AppLable=new String(Funs.StrToHexByte(AppLable));

            }
            String PerfName=mKernelApi.getValByTag(0x9F12);
            if(!TextUtils.isEmpty(PerfName))
            {
                Log.i(LogTag, "PerfName:"+new String(Funs.StrToHexByte(PerfName)));
                PerfName=new String(Funs.StrToHexByte(PerfName));
            }

            if(AppLable.toUpperCase().contains("DEBIT")||PerfName.toUpperCase().contains("DEBIT"))
            {
                Log.d("applog", "it is a Debit card!");
            }
            else if(AppLable.toUpperCase().contains("CREDIT")||PerfName.toUpperCase().contains("CREDIT"))
            {
                Log.d("applog", "it is a CREDIT card!");
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    text1.setText("iccdata:"+EmvData+" \ntrack KSN:"+ksn+"\nTrackData:"+TrackData+"\nQPBOCType:"+QPBOCType+"\n");

                }
            });



            int MstripMode = mKernelApi.getMstripFlag();
            Log.d("applog","TransMode:"+MstripMode );
            String track2="";
            if((MstripMode==1)&&mKernelApi.getValByTag(0x9F2A).equals("02"))//MC
            {
                track2 = mKernelApi.getValByTag(0x9F6B);
                Log.d("applog", "mStrip track2:"+track2);
            }
            else if((MstripMode==1)&&mKernelApi.getValByTag(0x9F2A).equals("04")) //Amex MS mode
            {
                track2 =Funs.FormatTrack2StripeMode(mKernelApi.getValByTag(0xDF52)) ;
                Log.d("applog", "mStrip track2:"+track2);
                String track1=Funs.FormatTrack1StripeMode(mKernelApi.getValByTag(0xDF51));
                Log.d("applog", "mStrip track1:"+track1);

            }


            Log.d("applog", "onReturnNfcCardData");
            Log.d("applog", "KSN:"+ksn);
            Log.d("applog", "TrackData:"+TrackData);
            Log.d("applog", "EmvData:"+EmvData);
            Log.d("applog", "GetField55ForSAMA:"+mKernelApi.GetField55ForSAMA());
            Log.d("applog", "getField55ForJIO:"+mKernelApi.getField55ForJIO(1));

        }



    }







}
