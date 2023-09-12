package com.urovo.sdk.view.scan;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.urovo.sdk.R;

import java.util.Collection;
import java.util.HashSet;

public class ScanCaptureView extends View {
    private Paint paint;
    private Bitmap resultBitmap;

    public ScanCaptureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ScanCaptureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScanCaptureView(Context context) {
        this(context, null);
    }

    private void init() {
        paint = new Paint();
        Resources resources = getResources();
        maskColor = new Color().parseColor("#60000000");
        resultColor = new Color().parseColor("#b0000000");
        resultPointColor = new Color().parseColor("#c0ffff00");
        resultPointColor = Color.parseColor("#c0ffff00");

        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        // 扫描框边角颜色
        innercornercolor = Color.parseColor("#45DDDD");
        // 扫描框边角长度
        innercornerlength = 20;
        // 扫描框边角宽度
        innercornerwidth = 1;
        SCAN_VELOCITY = 10;
        isCircle = true;
        // 扫描控件
        scanLight = BitmapFactory.decodeResource(getResources(), R.drawable.scan_light);
        possibleResultPoints = new HashSet<ResultPoint>(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect frame = getFramingRect();
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        paint.setColor(maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
                paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        drawFrameBounds(canvas, frame);

        drawScanLight(canvas, frame);

        Collection<ResultPoint> currentPossible = possibleResultPoints;
        Collection<ResultPoint> currentLast = lastPossibleResultPoints;
        if (currentPossible.isEmpty()) {
            lastPossibleResultPoints = null;
        } else {
            possibleResultPoints = new HashSet<ResultPoint>(5);
            lastPossibleResultPoints = currentPossible;
            paint.setAlpha(OPAQUE);
            paint.setColor(resultPointColor);

            if (isCircle) {
                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top
                            + point.getY(), 6.0f, paint);
                }
            }
        }
        if (currentLast != null) {
            paint.setAlpha(OPAQUE / 2);
            paint.setColor(resultPointColor);

            if (isCircle) {
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top
                            + point.getY(), 3.0f, paint);
                }
            }
        }

        postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
                frame.right, frame.bottom);

    }

    /**
     * 绘制移动扫描线
     *
     * @param canvas
     * @param frame
     */
    private void drawScanLight(Canvas canvas, Rect frame) {
        if (scanLineTop == 0) {
            scanLineTop = frame.top;
        }
        if (scanLineTop >= frame.bottom - 30) {
            scanLineTop = frame.top;
        } else {
            scanLineTop += SCAN_VELOCITY;
        }
        Rect scanRect = new Rect(frame.left, scanLineTop, frame.right,
                scanLineTop + 30);
        canvas.drawBitmap(scanLight, null, scanRect, paint);
    }

    /**
     * 绘制取景框边框
     *
     * @param canvas
     * @param frame
     */
    private void drawFrameBounds(Canvas canvas, Rect frame) {

        /*
         * paint.setColor(Color.WHITE); paint.setStrokeWidth(2);
         * paint.setStyle(Paint.Style.STROKE);
         *
         * canvas.drawRect(frame, paint);
         */

        paint.setColor(innercornercolor);
        paint.setStyle(Paint.Style.FILL);

        int corWidth = innercornerwidth;
        int corLength = innercornerlength;

        // 左上角
        canvas.drawRect(frame.left, frame.top, frame.left + corWidth, frame.top
                + corLength, paint);
        canvas.drawRect(frame.left, frame.top, frame.left + corLength,
                frame.top + corWidth, paint);
        // 右上角
        canvas.drawRect(frame.right - corWidth, frame.top, frame.right,
                frame.top + corLength, paint);
        canvas.drawRect(frame.right - corLength, frame.top, frame.right,
                frame.top + corWidth, paint);
        // 左下角
        canvas.drawRect(frame.left, frame.bottom - corLength, frame.left
                + corWidth, frame.bottom, paint);
        canvas.drawRect(frame.left, frame.bottom - corWidth, frame.left
                + corLength, frame.bottom, paint);
        // 右下角
        canvas.drawRect(frame.right - corWidth, frame.bottom - corLength,
                frame.right, frame.bottom, paint);
        canvas.drawRect(frame.right - corLength, frame.bottom - corWidth,
                frame.right, frame.bottom, paint);
    }

    public Rect getFramingRect() {

        return getFramingRect_nose();

//        return getFramingRect_land();
    }

    public Rect getFramingRect_nose() {
        WindowManager manager = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point screenResolution = new Point(
                display.getWidth(), display.getHeight());
        int width = (int)(findDesiredDimensionInRange(screenResolution.x,
                MIN_FRAME_WIDTH, MAX_FRAME_WIDTH) );
        int height = width;

        int leftOffset = (screenResolution.x - width) / 2;
        int topOffset = (screenResolution.y - height) / 2;
        Rect framingRect = new Rect(leftOffset, topOffset, leftOffset + width,
                topOffset + height);
        return framingRect;
    }

    public Rect getFramingRect_land() {
        WindowManager manager = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point screenResolution = new Point(
                display.getWidth(), display.getHeight());
        int width = (int) (findDesiredDimensionInRange(screenResolution.x,
                MIN_FRAME_WIDTH, MAX_FRAME_WIDTH));
        int height = (int) (findDesiredDimensionInRange(screenResolution.y,
                MIN_FRAME_WIDTH, MAX_FRAME_WIDTH));

        int leftOffset = (screenResolution.x - width) / 2;
        int rightOffset = leftOffset + width;
        int topOffset = leftOffset;
        int bottomOffset = height - leftOffset;
        Rect framingRect = new Rect(leftOffset, topOffset, rightOffset,
                bottomOffset);
        return framingRect;
    }

    private static final int MAX_FRAME_WIDTH = 1200; // = 5/8 * 1920
    private static final int MAX_FRAME_HEIGHT = 675; // = 5/8 * 1080
    private static final int MIN_FRAME_WIDTH = 240;
    private static final int MIN_FRAME_HEIGHT = 240;
    private int maskColor;
    private int resultColor;
    private int resultPointColor;
    // 扫描框边角颜色
    private int innercornercolor;
    // 扫描框边角长度
    private int innercornerlength;
    // 扫描框边角宽度
    private int innercornerwidth;
    // 扫描线
    private Bitmap scanLight;
    // 是否展示小圆点
    private boolean isCircle;
    // 扫描线移动的y
    private int scanLineTop;
    // 扫描线移动速度
    private int SCAN_VELOCITY;

    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;

    private static final long ANIMATION_DELAY = 100L;
    private static final int OPAQUE = 0xFF;

    private static int findDesiredDimensionInRange(int resolution, int hardMin,
                                                   int hardMax) {
        int dim = 7 * resolution / 8; // Target 5/8 of each dimension
        if (dim < hardMin) {
            return hardMin;
        }
        if (dim > hardMax) {
            return hardMax;
        }
        return dim;
    }

}
