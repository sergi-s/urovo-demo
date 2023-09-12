package com.urovo.sdk.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.urovo.sdk.R;

public abstract class BaseActivity extends Activity {
    public static final String TAG = "UROVO_Demo_AAR";

    private ScrollView scrollView;
    private TextView tvOutput;
    private Button button_clearPage;
    public Button button_clearAPDU;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * 通过设置全屏，设置状态栏透明
     *
     * @param activity
     */
    @SuppressLint({"NewApi", "ResourceAsColor"})
    protected void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // 5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                // 两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                // 导航栏颜色也可以正常设置
                //				 window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
                // attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
    }

    public void initView() {
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        tvOutput = (TextView) findViewById(R.id.tvOutput);

        button_clearAPDU = (Button) findViewById(R.id.btn_clearAPDU);
        button_clearPage = (Button) findViewById(R.id.btn_clearpage);
        button_clearPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvOutput.setText("");
            }
        });
    }

    protected void outputText(String text) {
        outputColorText(TextColor.BLUE, text);
    }

    protected void outputColorText(final TextColor color, final String text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String appendText = "\n" + text;
                tvOutput.append(getColorText(color, appendText));

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }, 50);
            }
        });
    }

    protected void clearText() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                tvOutput.setText("");

            }
        });
    }

    protected static SpannableStringBuilder getColorText(TextColor color, String text) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        ForegroundColorSpan colorSpan = null;
        switch (color) {
            case RED:
                colorSpan = new ForegroundColorSpan(Color.RED);
                break;
            case GREEN:
                colorSpan = new ForegroundColorSpan(Color.GREEN);
                break;
            case BLUE:
                colorSpan = new ForegroundColorSpan(Color.BLUE);
                break;
            case DKGRAY:
                colorSpan = new ForegroundColorSpan(Color.DKGRAY);
                break;
            case YELLOW:
                colorSpan = new ForegroundColorSpan(Color.YELLOW);
                break;
            case MAGENTA:
                colorSpan = new ForegroundColorSpan(Color.MAGENTA);
                break;
            case CYAN:
                colorSpan = new ForegroundColorSpan(Color.CYAN);
                break;
            case BLACK:
            default:
                colorSpan = new ForegroundColorSpan(Color.BLACK);
                break;
        }
        ssb.setSpan(colorSpan, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    enum TextColor {
        BLACK,
        RED,
        GREEN,
        BLUE,
        DKGRAY,
        YELLOW,
        MAGENTA,
        CYAN
    }

    public void showMessage(String message) {
        Toast.makeText(BaseActivity.this, "" + message, Toast.LENGTH_SHORT).show();
    }

}
