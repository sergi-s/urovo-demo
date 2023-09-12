package com.urovo.sdk.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;

import com.urovo.sdk.print.EncodingHandler;
import com.urovo.sdk.print.PrintFormat;
import com.urovo.sdk.print.PrinterProviderImpl;
import com.urovo.sdk.print.paint.PrintContentBean;

import java.io.File;
import java.util.List;

public class PaintView {

    private static Context mContext;
    private static Paint mPaint;
    private static String mFontNameLast;
    private static Typeface mTypeface;
    private static Canvas cacheCanvas;
    private static Bitmap cachebBitmap;
    private static PaintView mPaintView;

    private static int mEraseColor = 0xffffffff;
    private static int mPaintColor = Color.WHITE;

    private static final int MAX_PAGEWIDTH = 380;

    public static PaintView getInstance(Context context, int eraseColor, int paintColor) {
        if (mPaintView == null) {
            mPaintView = new PaintView();
        }
        if (mContext == null) {
            mContext = context;
        }
        mEraseColor = eraseColor;
        mPaintColor = paintColor;
        return mPaintView;
    }

    public void init(int height) {
        cachebBitmap = Bitmap.createBitmap(MAX_PAGEWIDTH, height, Config.ARGB_8888);//高清，650, 380
        cachebBitmap.eraseColor(mEraseColor);
        cacheCanvas = new Canvas(cachebBitmap);

        mPaint = new Paint();
        mPaint.setColor(mPaintColor);
        cacheCanvas.save();
        cacheCanvas.restore();
    }

    public float drawText(PrintContentBean bean, int y) {
        String text = bean.getContent();
        int font = bean.getFont();
        int align = bean.getAlign();
        boolean isBold = bean.isBold();
        String fontName = bean.getFontName();
        float lineHeight = 0;
        int x = 0;
        if (align == PrintFormat.ALIGN_CENTER || align == PrintFormat.ALIGN_LEFT_RIGHT_CENTER) {
            x = MAX_PAGEWIDTH / 2;
        } else if (align == PrintFormat.ALIGN_RIGHT) {
            x = MAX_PAGEWIDTH;
        }
        int fontSize = PrinterProviderImpl.DEF_FONT_SIZE;
        if (font == PrintFormat.FONT_SMALL) {
            fontSize = PrinterProviderImpl.DEF_FONT_SIZE_SMALL;
        } else if (font == PrintFormat.FONT_LARGE) {
            fontSize = PrinterProviderImpl.DEF_FONT_SIZE_BIG;
        }
        if (bean.getFontSize() > 0) {
            fontSize = bean.getFontSize();
        }

        fontName = checkFontFileExist(fontName);
        Typeface typeface = null;
        if (TextUtils.equals(mFontNameLast, fontName) && mPaint != null) {
            typeface = mTypeface;
        } else {
            File file = new File(fontName);
            if (!file.exists()) {
                fontName = PrinterProviderImpl.fontName_default;
                file = new File(fontName);
                if (!file.exists()) {
                    fontName = PrinterProviderImpl.fontName_default_2;
                    file = new File(fontName);
                }
            }
            if (!file.exists()) {
                typeface = Typeface.defaultFromStyle(Typeface.NORMAL);
            } else {
                typeface = Typeface.create(Typeface.createFromFile(fontName), Typeface.NORMAL);
            }
        }
        mTypeface = typeface;
        mFontNameLast = fontName;

        mPaint.setTypeface(typeface);
        mPaint.setFakeBoldText(isBold);
        mPaint.setTextSize(fontSize);
        mPaint.setTextAlign(getAlign(align));
        //通过设置Flag来应用抗锯齿效果
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        float fonehight = fm.bottom - fm.top;
        lineHeight = fonehight;
        cacheCanvas.drawText(text, x, y, mPaint);
        return lineHeight;
    }

    /**
     * 获取每一行的高度
     *
     * @param font
     * @return
     */
    public float getTextHeight(int font, boolean isBold) {
        float height = 0;
        int fontSize = PrinterProviderImpl.DEF_FONT_SIZE;
        if (font == PrintFormat.FONT_SMALL) {
            fontSize = PrinterProviderImpl.DEF_FONT_SIZE_SMALL;
        } else if (font == PrintFormat.FONT_LARGE) {
            fontSize = PrinterProviderImpl.DEF_FONT_SIZE_BIG;
        }
        String fontName = PrinterProviderImpl.fontName_default;
        fontName = checkFontFileExist(fontName);
        Typeface typeface = null;
        if (TextUtils.equals(mFontNameLast, fontName) && mPaint != null) {
            typeface = mTypeface;
        } else {
            File file = new File(fontName);
            if (!file.exists()) {
                fontName = PrinterProviderImpl.fontName_default;
                file = new File(fontName);
                if (!file.exists()) {
                    fontName = PrinterProviderImpl.fontName_default_2;
                    file = new File(fontName);
                }
            }
            if (!file.exists()) {
                typeface = Typeface.defaultFromStyle(Typeface.NORMAL);
            } else {
                typeface = Typeface.create(Typeface.createFromFile(fontName), Typeface.NORMAL);
            }
        }
        mTypeface = typeface;
        mFontNameLast = fontName;

        Paint paint = new Paint();
        paint.setTypeface(typeface);
        paint.setFakeBoldText(isBold);
        paint.setTextSize(fontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        height = fm.bottom - fm.top;
        if (font == PrintFormat.FONT_LARGE) {
            //    height+=5;
        }
        return height;
    }

    /**
     * 获取每一行的高度
     *
     * @param bean
     * @return
     */
    public float getTextHeight(PrintContentBean bean) {
        String text = bean.getContent();
        int font = bean.getFont();
        int align = bean.getAlign();
        boolean isBold = bean.isBold();
        String fontName = bean.getFontName();
        float lineHeight = 0;
        int fontSize = PrinterProviderImpl.DEF_FONT_SIZE;
        if (font == PrintFormat.FONT_SMALL) {
            fontSize = PrinterProviderImpl.DEF_FONT_SIZE_SMALL;
        } else if (font == PrintFormat.FONT_LARGE) {
            fontSize = PrinterProviderImpl.DEF_FONT_SIZE_BIG;
        }
        if (bean.getFontSize() > 0) {
            fontSize = bean.getFontSize();
        }
        fontName = checkFontFileExist(fontName);
        Typeface typeface = null;
        if (TextUtils.equals(mFontNameLast, fontName) && mPaint != null) {
            typeface = mTypeface;
        } else {
            if (TextUtils.isEmpty(fontName)) {
                fontName = PrinterProviderImpl.fontName_default;
            }
            File file = new File(fontName);
            if (!file.exists()) {
                fontName = PrinterProviderImpl.fontName_default;
                file = new File(fontName);
                if (!file.exists()) {
                    fontName = PrinterProviderImpl.fontName_default_2;
                    file = new File(fontName);
                }
            }
            if (!file.exists()) {
                typeface = Typeface.defaultFromStyle(Typeface.NORMAL);
            } else {
                typeface = Typeface.create(Typeface.createFromFile(fontName), Typeface.NORMAL);
            }
        }
        mTypeface = typeface;
        mFontNameLast = fontName;

        Paint paint = new Paint();
        paint.setTypeface(typeface);
        paint.setFakeBoldText(isBold);
        paint.setTextSize(fontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        lineHeight = fm.bottom - fm.top;
        if (font == PrintFormat.FONT_LARGE) {
            //    lineHeight+=5;
        }
        if (align == PrintFormat.ALIGN_LEFT_RIGHT || align == PrintFormat.ALIGN_LEFT_RIGHT_CENTER) {
            return 0;
        }
        return lineHeight;
    }

    public float drawBitmap(Bitmap bitmap, int x, int y, int align) {
        if (bitmap == null) {
            Log.e("PaintView", "drawBitmap bitmap is NULL");
            return 0;
        }
        float height = 40;
        mPaint.setTextAlign(getAlign(align));
        cacheCanvas.drawBitmap(bitmap, x, y, mPaint);
        //height = mPaint.getFontSpacing();
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        height = fm.bottom - fm.top;
        if (bitmap != null) {
            bitmap.recycle();
        }
        return height;
    }

    private int getFontHight(String text, int fontSize) {
        // FontMetrics对象
        Paint pFont = new Paint();
        pFont.setTextSize(fontSize);
        Paint.FontMetrics fontMetrics = pFont.getFontMetrics();
        int fontHeight = (int) Math.ceil(fontMetrics.descent - fontMetrics.ascent);
        return fontHeight;
    }

    public Rect getTextSize(String text, int fontSize) {
        Paint pFont = new Paint();
        pFont.setTextSize(fontSize);
        Rect rect = new Rect();
        pFont.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }

    private Paint getPaint(int size, boolean bold, boolean fontItalic) {
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.reset();
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(size);
        mPaint.setFakeBoldText(bold);
        if (fontItalic)
            mPaint.setTextSkewX(-0.5f);
        return mPaint;
    }

    public Align getAlign(int align) {
        if (align == 1 || align == 4) {
            return Align.CENTER;
        } else if (align == 2) {
            return Align.RIGHT;
        } else {
            return Align.LEFT;
        }
    }

    public Bitmap getBitmap() {
        return cachebBitmap;
    }


    public void close() {
        if (cachebBitmap != null) {
            cachebBitmap.recycle();
            cachebBitmap = null;
        }
        cacheCanvas = null;
        mPaint = null;
    }

    public String checkFontFileExist(String fontName) {
        File file = null;
        if (TextUtils.isEmpty(fontName)) {
            fontName = PrinterProviderImpl.fontName_default;
        }
        file = new File(fontName);
        if (!file.exists()) {
            fontName = PrinterProviderImpl.fontName_default;
            file = new File(fontName);
            if (!file.exists()) {
                fontName = PrinterProviderImpl.fontName_default_2;
            }
        }
        return fontName;
    }

    public void drawTextRect(float left, float top, float right, float bottom) {
        int x = 0;
        Paint paint = new Paint();
        // 设置样式-空心矩形
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        cacheCanvas.drawRect(left, top, right, bottom, paint);
    }


    /**
     * 格绘制打印图片
     *
     * @param contentBeanList
     * @param mPaintView
     * @param height
     * @return
     */
    public Bitmap drawPrintBitmap(List<PrintContentBean> contentBeanList, PaintView mPaintView, int height) {
        if (contentBeanList == null || contentBeanList.size() == 0) {
            return null;
        }
        mPaintView.init(height);
        int paintY = 30;
        PrintContentBean bean = null;
        for (int i = 0; i < contentBeanList.size(); i++) {
            bean = contentBeanList.get(i);
            if (bean == null) {
                continue;
            }
            String content = bean.getContent();
            int align = bean.getAlign();
            switch (bean.getPrintType()) {
                case PrintContentBean.PrintType_QRCode:
                    mPaintView.drawBitmap(getQRCodeBitmap(content, bean.getOffset(), bean.getHeight()), bean.getOffset(), paintY, align);
                    paintY += bean.getHeight();
                    break;
                default:
                    if (align == PrintFormat.ALIGN_LEFT_RIGHT || align == PrintFormat.ALIGN_LEFT_RIGHT_CENTER) {
                        mPaintView.drawText(bean, paintY);
                    } else {
                        paintY += mPaintView.drawText(bean, paintY);
                    }
                    break;
            }

        }
        Bitmap mBitmap = mPaintView.getBitmap();
        //保存图片 测试验证
        //	String path = SignFiles.createSignFile(mBitmap);
        return mBitmap;
    }

    public Bitmap getQRCodeBitmap(String qrCode, int offset, int height) {
        if (offset > 55) {
            offset -= 55;
        }
        int calHeight = height - offset * 2;
        if (height > MAX_PAGEWIDTH) {
            height = MAX_PAGEWIDTH;
        }
        try {
            if (qrCode != null && qrCode.length() != 0) {
                Bitmap qrCodeBmp = EncodingHandler.createQRImage(qrCode, height, height);
                return qrCodeBmp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
