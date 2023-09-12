package com.urovo.sdk.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.zxing.BarcodeFormat;
import com.urovo.sdk.R;
import com.urovo.sdk.print.PrintFormat;
import com.urovo.sdk.print.PrinterProviderImpl;
import com.urovo.sdk.print.paint.PaintView;
import com.urovo.sdk.print.paint.PrintContentBean;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PrintActivity extends BaseActivity implements View.OnClickListener {

    private PrinterProviderImpl mPrintManager = null;
    private EditText editText_gray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        initView();
        editText_gray = (EditText) findViewById(R.id.editText_gray);
        mPrintManager = PrinterProviderImpl.getInstance(PrintActivity.this);
    }

    boolean isPrintting = false;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_getStatus:
                try {
                    outputColorText(TextColor.BLUE, "getStatus");
                    mPrintManager.initPrint();
                    int status = mPrintManager.getStatus();
                    outputColorText(TextColor.BLUE, "print status: " + status);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_startPrint:
                new Thread() {

                    @Override
                    public void run() {
                        super.run();
                        try {
                            outputText("isPrintting: " + isPrintting);
                            if (isPrintting) {
                                return;
                            }
                            isPrintting = true;
                            String grayStr = editText_gray.getText().toString().trim();
                            if (TextUtils.isEmpty(grayStr)) {
                                grayStr = "1";
                            }
                            int gray = Integer.parseInt(grayStr);
                            mPrintManager.initPrint();
                            mPrintManager.setGray(6);
                            int status = mPrintManager.getStatus();
                            outputText("getCurrentStatus before print: " + status);
                            if (status != 0) {
                                isPrintting = false;
                                return;
                            }
                            mPrintManager.setGray(gray);

                            String fontPath = Environment.getExternalStorageDirectory() + "/COMICATE.TTF";
                            String fontPath1 = Environment.getExternalStorageDirectory() + "/consolas.ttf";
                            Bundle format = new Bundle();
                            byte[] imageData = getBitmapBytes(getLogoBitmap(PrintActivity.this, R.drawable.visa));
                            format = new Bundle();
                            format.putInt("align", 1);
                            format.putInt("offset", 0);
                            format.putInt("height", 300);
                            mPrintManager.addImage(format, imageData);

                            format.putInt("font", 1);
                            format.putInt("align", 1);
                            format.putBoolean("fontBold", true);
                            format.putString("fontName", fontPath);
                            format.putInt("lineHeight", 0);
                            mPrintManager.addText(format, "CENTER");
                            format = new Bundle();
                            format.putInt("font", 1);
                            format.putInt("align", 1);
                            format.putBoolean("fontBold", true);
                            format.putString("fontName", fontPath);
                            format.putInt("lineHeight", 10);
                            mPrintManager.addText(format, "CENTER  CENTERCENTERCENTERCENTERCENTERCENTERCENTER");
                            format = new Bundle();
                            format.putInt("font", 1);
                            format.putInt("align", 1);
                            format.putBoolean("fontBold", true);
                            format.putString("fontName", fontPath);
                            format.putInt("lineHeight", 10);
                            mPrintManager.addText(format, "居中居中  居中居中居中居中居中居中居中居中");
                            format = new Bundle();
                            format.putInt("font", 1);
                            format.putInt("align", 0);
                            format.putString("fontName", fontPath1);
                            format.putInt("lineHeight", 10);
                            mPrintManager.addText(format, "LEFT");
                            format = new Bundle();
                            format.putInt("font", 1);
                            format.putInt("align", 0);
                            format.putString("fontName", fontPath1);
                            format.putInt("lineHeight", 10);
                            mPrintManager.addText(format, "LEFTLEFT  LEFTLEFTLEFTLEFTLEFTLEFTLEFTLEFTLEFTLEFT");
                            format = new Bundle();
                            format.putInt("font", 1);
                            format.putInt("align", 1);
                            format.putString("fontName", fontPath);
                            format.putInt("lineHeight", 10);
                            mPrintManager.addText(format, "左对齐左对齐  左对齐左对齐左对齐左对齐左对齐左对齐");
                            format = new Bundle();
                            format.putInt("font", 1);
                            format.putInt("align", 2);
                            format.putString("fontName", fontPath);
                            mPrintManager.addText(format, "RIGHT");
                            format = new Bundle();
                            format.putInt("font", 1);
                            format.putInt("align", 2);
                            format.putString("fontName", fontPath1);
                            format.putInt("lineHeight", 10);
                            mPrintManager.addText(format, "RIGHTRIGHT  RIGHTRIGHTRIGHTRIGHTRIGHTRIGHTRIGHTRIGHT");
                            format = new Bundle();
                            format.putInt("font", 1);
                            format.putInt("align", 2);
                            format.putString("fontName", fontPath);
                            format.putInt("lineHeight", 10);
                            mPrintManager.addText(format, "右对齐右对齐  右对齐右对齐右对齐右对齐右对齐右对齐");

                            mPrintManager.addTextLeft_Center_Right("LEFT", "CENTER", "RIGHT", 1, false);
                            mPrintManager.addTextLeft_Center_Right("LEFTLEFTLEFTLEFT", "CENTERCENTERCENTER", "RIGHTRIGHTRIGHTRIGHT", 1, false);
                            format = new Bundle();
                            format.putInt("font", 1);
                            format.putInt("align", 1);
                            mPrintManager.addTextLeft_Center_Right(format, "LEFT", "CENTER", "RIGHT");
                            mPrintManager.addTextLeft_Center_Right(format, "LEFTLEFTLEFT", "CENTERCENTERCENTER", "RIGHTRIGHTRIGHT");

                            mPrintManager.addTextLeft_Right("LEFT", "RIGHT", 1, false);
                            mPrintManager.addTextLeft_Right("LEFTLEFTLEFTLEFTLEFT", "RIGHTRIGHTRIGHTRIGHT", 1, false);
                            format = new Bundle();
                            format.putInt("font", 1);
                            format.putString("fontName", fontPath1);
                            format.putBoolean("fontBold", false);
                            mPrintManager.addTextLeft_Right(format, "LEFT", "RIGHT");
                            mPrintManager.addTextLeft_Right(format, "LEFTLEFTLEFTLEFTLEFTLEFTLEFTLEFT", "RIGHTRIGHTRIGHTRIGHTRIGHT");

                            format = new Bundle();
                            format.putInt("font", 1);
                            format.putInt("align", 2);
                            format.putInt("lineHeight", 20);
                            mPrintManager.addTextLeft_Right(format, "健力宝", "15元");

                            format = new Bundle();
                            format.putInt("font", 1);
                            format.putInt("align", 2);
                            format.putInt("lineHeight", 50);
                            mPrintManager.addTextLeft_Right(format, "健力宝", "15元");

                            format = new Bundle();
                            format.putInt("font", 1);
                            format.putInt("align", 2);
                            format.putInt("lineHeight", 20);
                            mPrintManager.addTextLeft_Center_Right(format, "健力宝", "16", "15元");

                            format = new Bundle();
                            format.putInt("font", 1);
                            format.putInt("align", 2);
                            format.putInt("lineHeight", 0);
                            mPrintManager.addTextLeft_Center_Right(format, "健力宝", "16", "15元");

                            format = new Bundle();
                            format.putInt("align", 0);
                            format.putInt("width", 300);
                            format.putInt("height", 100);
                            mPrintManager.addBarCode(format, "1111111111111111");
                            mPrintManager.feedLine(3);

                            format = new Bundle();
                            format.putInt("align", 1);
                            format.putInt("width", 300);
                            format.putInt("height", 100);
                            format.putSerializable(PrintFormat.BARCODE_TYPE, BarcodeFormat.CODE_39);
                            mPrintManager.addBarCode(format, "33333333333333");
                            mPrintManager.feedLine(3);

                            format = new Bundle();
                            format.putInt("align", 1);
                            format.putInt("offset", 20);
                            format.putInt("expectedHeight", 200);
                            mPrintManager.addQrCode(format, "222222222222222222222");
                            mPrintManager.feedLine(3);

                            format = new Bundle();
                            format.putInt("align", 1);
                            format.putInt("offset", -1);
                            format.putInt("expectedHeight", 300);
                            mPrintManager.addQrCode(format, "222222222222222222222");
                            mPrintManager.feedLine(3);

                            int iRet = mPrintManager.startPrint();
                            outputColorText(TextColor.BLUE, "print result: " + iRet);
                            mPrintManager.close();
                            isPrintting = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
            case R.id.btn_feedLine:
                new Thread() {

                    @Override
                    public void run() {
                        super.run();
                        try {
                            outputColorText(TextColor.BLUE, "feed line");
                            mPrintManager.initPrint();
                            mPrintManager.feedLine(3);

                            int iRet = mPrintManager.startPrint();
                            outputColorText(TextColor.BLUE, "print result: " + iRet);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
            case R.id.btn_startPrint_Bitmap:
                new Thread() {

                    @Override
                    public void run() {
                        super.run();
                        try {
                            PaintView paintView = PaintView.getInstance(PrintActivity.this);
                            int heightTotal = 10;
                            List<PrintContentBean> list = new ArrayList<PrintContentBean>();
                            PrintContentBean contentBean = new PrintContentBean();

                            byte[] imageData = getBitmapBytes(getLogoBitmap(PrintActivity.this, R.drawable.visa));
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                            contentBean.setPrintType(PrintContentBean.PrintType_Bitmap);
                            contentBean.setAlign(PrintFormat.ALIGN_CENTER);
                            contentBean.setBitmap(bitmap);
                            contentBean.setHeight(300);
                            list.add(contentBean);
                            heightTotal += contentBean.getHeight();

                            contentBean = new PrintContentBean();
                            contentBean.setContent("CENTERCENTERCENTER");
                            contentBean.setPrintType(PrintContentBean.PrintType_Text);
                            contentBean.setFont(PrintFormat.FONT_LARGE);
                            contentBean.setAlign(PrintFormat.ALIGN_CENTER);
                            contentBean.setBold(false);
                            list.add(contentBean);
                            heightTotal += paintView.getTextHeight(contentBean);

                            contentBean = new PrintContentBean();
                            contentBean.setContent("LEFTLEFTLEFTLEFT");
                            contentBean.setPrintType(PrintContentBean.PrintType_Text);
                            contentBean.setFont(PrintFormat.FONT_NORMAL);
                            contentBean.setAlign(PrintFormat.ALIGN_LEFT);
                            contentBean.setBold(true);
                            list.add(contentBean);
                            heightTotal += paintView.getTextHeight(contentBean);

                            contentBean = new PrintContentBean();
                            contentBean.setContent("LEFTLEFTLEFTLEFT");
                            contentBean.setPrintType(PrintContentBean.PrintType_Text);
                            contentBean.setFont(PrintFormat.FONT_NORMAL);
                            contentBean.setFontSize(20);
                            contentBean.setAlign(PrintFormat.ALIGN_LEFT);
                            contentBean.setBold(true);
                            list.add(contentBean);
                            heightTotal += paintView.getTextHeight(contentBean);

                            contentBean = new PrintContentBean();
                            contentBean.setContent("RIGHTRIGHTRIGHT");
                            contentBean.setPrintType(PrintContentBean.PrintType_Text);
                            contentBean.setFont(PrintFormat.FONT_NORMAL);
                            contentBean.setAlign(PrintFormat.ALIGN_RIGHT);
                            contentBean.setBold(true);
                            list.add(contentBean);
                            heightTotal += paintView.getTextHeight(contentBean);

                            contentBean = new PrintContentBean();
                            contentBean.setContent("LEFTLEFT");
                            contentBean.setPrintType(PrintContentBean.PrintType_Text);
                            contentBean.setFont(PrintFormat.FONT_NORMAL);
                            contentBean.setAlign(PrintFormat.ALIGN_LEFT_RIGHT);
                            contentBean.setBold(false);
                            list.add(contentBean);
                            heightTotal += paintView.getTextHeight(contentBean);

                            contentBean = new PrintContentBean();
                            contentBean.setContent("RIGHTRIGHT");
                            contentBean.setPrintType(PrintContentBean.PrintType_Text);
                            contentBean.setFont(PrintFormat.FONT_NORMAL);
                            contentBean.setAlign(PrintFormat.ALIGN_RIGHT);
                            contentBean.setBold(false);
                            list.add(contentBean);
                            heightTotal += paintView.getTextHeight(contentBean);

                            contentBean = new PrintContentBean();
                            contentBean.setContent("LEFT");
                            contentBean.setPrintType(PrintContentBean.PrintType_Text);
                            contentBean.setFont(PrintFormat.FONT_NORMAL);
                            contentBean.setAlign(PrintFormat.ALIGN_LEFT_RIGHT_CENTER);
                            contentBean.setBold(false);
                            list.add(contentBean);
                            heightTotal += paintView.getTextHeight(contentBean);

                            contentBean = new PrintContentBean();
                            contentBean.setContent("CENTER");
                            contentBean.setPrintType(PrintContentBean.PrintType_Text);
                            contentBean.setFont(PrintFormat.FONT_NORMAL);
                            contentBean.setAlign(PrintFormat.ALIGN_LEFT_RIGHT);
                            contentBean.setBold(false);
                            list.add(contentBean);
                            heightTotal += paintView.getTextHeight(contentBean);

                            contentBean = new PrintContentBean();
                            contentBean.setContent("RIGHT");
                            contentBean.setPrintType(PrintContentBean.PrintType_Text);
                            contentBean.setFont(PrintFormat.FONT_NORMAL);
                            contentBean.setAlign(PrintFormat.ALIGN_RIGHT);
                            contentBean.setBold(false);
                            list.add(contentBean);
                            heightTotal += paintView.getTextHeight(contentBean);

                            contentBean = new PrintContentBean();
                            contentBean.setContent("111111111111111111");
                            contentBean.setAlign(PrintFormat.ALIGN_CENTER);
                            contentBean.setPrintType(PrintContentBean.PrintType_BarCode);
                            contentBean.setWidght(300);
                            contentBean.setHeight(100);
                            list.add(contentBean);
                            heightTotal += contentBean.getHeight();

                            contentBean = new PrintContentBean();
                            contentBean.setContent("222222222222222222");
                            contentBean.setPrintType(PrintContentBean.PrintType_QRCode);
                            contentBean.setAlign(PrintFormat.ALIGN_CENTER);
                            contentBean.setHeight(300);
                            list.add(contentBean);
                            heightTotal += contentBean.getHeight();

                            mPrintManager.initPrint();
                            String grayStr = editText_gray.getText().toString().trim();
                            if (TextUtils.isEmpty(grayStr)) {
                                grayStr = "6";
                            }
                            int gray = Integer.parseInt(grayStr);
                            mPrintManager.setGray(gray);

                            Bitmap bitmap1 = mPrintManager.drawPrintBitmap(list, paintView, heightTotal);
                            mPrintManager.addBitmap(bitmap1, 0);
                            int ret = mPrintManager.startPrint();
                            outputColorText(TextColor.BLUE, "print result: " + ret);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;

        }
    }

    public Bitmap createMarkLineBitmap(Context context, String content, int align) {
        com.urovo.sdk.paint.PaintView paintView = com.urovo.sdk.paint.PaintView.getInstance(context, Color.WHITE, Color.BLACK);
        int heightTotal = 5;
        List<PrintContentBean> list = new ArrayList<PrintContentBean>();

        PrintContentBean contentBean = new PrintContentBean();
        contentBean.setContent(content);
        contentBean.setFont(PrintFormat.FONT_NORMAL);
        contentBean.setAlign(align);
        contentBean.setBold(false);
        list.add(contentBean);
        heightTotal += paintView.getTextHeight(contentBean);

        Bitmap bitmap = paintView.drawPrintBitmap(list, paintView, heightTotal);
        return bitmap;
    }


    private Bitmap getLogoBitmap(Context context, int id) {
        BitmapDrawable draw = (BitmapDrawable) context.getResources().getDrawable(id);
        Bitmap bitmap = draw.getBitmap();
        return bitmap;
    }

    public byte[] getBitmapBytes(Bitmap bitmap) {

        byte[] imageData = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageData = baos.toByteArray();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
        return imageData;
    }

}
