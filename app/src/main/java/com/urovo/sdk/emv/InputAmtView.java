package com.urovo.sdk.emv;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.urovo.sdk.R;

/**
 * 金额输入键盘
 * 
 * @author Xiray
 *
 */
public class InputAmtView extends ViewGroup {

	private float density;
	private String[] num = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", ".", "", "Clear", "OK" };
	private int ws, hs;
	private onClickListener listener;
	private LayoutParams params;

	public InputAmtView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		density = getResources().getDisplayMetrics().density;
		removeAllViews();
		for (int i = 0; i < 14; i++) {
			if (i == 11) {
				ImageButton imageButton = new ImageButton(context);
				imageButton.setTag(i);
				imageButton.setOnClickListener(listener2);
				addView(imageButton);
			} else {
				Button button = new Button(context);
				button.setTag(i);
				button.setOnClickListener(listener2);
				addView(button);
			}
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int w = getMeasuredWidth();
		int h = getMeasuredHeight();
		ws = (w - getPaddingLeft() - getPaddingRight()) / 4;
		hs = (h - getPaddingBottom() - getPaddingTop()) / 4;
		params = new LayoutParams(ws, hs);
		for (int i = 0; i < 9; i++) {
			View view = getChildAt(i);
			view.setLayoutParams(params);
		}
		params = new LayoutParams(ws * 2, hs);
		View view = getChildAt(9);
		view.setLayoutParams(params);
		params = new LayoutParams(ws, hs);
		view = getChildAt(10);
		view.setLayoutParams(params);
		view = getChildAt(11);
		view.setLayoutParams(params);
		view = getChildAt(12);
		view.setLayoutParams(params);
		params = new LayoutParams(ws, hs * 2);
		view = getChildAt(13);
		view.setLayoutParams(params);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int w = getMeasuredWidth();
		int h = getMeasuredHeight();
		ws = (w - getPaddingLeft() - getPaddingRight()) / 4;
		hs = (h - getPaddingBottom() - getPaddingTop()) / 4;
		Point point = new Point(getPaddingLeft(), getPaddingTop());
		for (int i = 0; i < 3; i++) {
			point.y = hs * i + getPaddingTop();
			for (int j = 0; j < 3; j++) {
				View view = getChildAt(i * 3 + j);
				params = view.getLayoutParams();
				point.x = params.width * j + getPaddingLeft();
				view.layout(point.x, point.y, params.width + point.x, point.y + params.height);
			}

		}
		point.y = hs * 3 + getPaddingTop();
		point.x = getPaddingLeft();
		for (int i = 9; i < 11; i++) {
			View view = getChildAt(i);
			params = view.getLayoutParams();
			view.layout(point.x, point.y, params.width + point.x, point.y + params.height);
			point.x += params.width;
		}
		point.x = getPaddingLeft() + ws * 3;
		point.y = getPaddingTop();
		for (int i = 11; i < 14; i++) {
			View view = getChildAt(i);
			params = view.getLayoutParams();
			view.layout(point.x, point.y, params.width + point.x, point.y + params.height);
			point.y += params.height;
		}

		for (int i = 0; i < 14; i++) {
			View view = getChildAt(i);
			if (i < 11) {
				Button button = (Button) view;
				button.setText(num[i]);
				button.setTextColor(getResources().getColor(R.color.textColor_darkGery));
				button.setTextSize(30);
				button.setBackgroundResource(R.drawable.text_bg);
			} else if (i == 11) {
				ImageButton imageButton = (ImageButton) view;
				imageButton.setImageResource(R.drawable.shouyin_del);
				imageButton.setBackgroundResource(R.drawable.delete_bg);
			} else if (i == 12) {
				Button button = (Button) view;
				button.setText(num[i]);
				button.setTextSize(22);
				button.setTextColor(Color.WHITE);
				button.setBackgroundResource(R.drawable.clear_bg);
			} else if (i == 13) {
				Button button = (Button) view;
				button.setText(num[i]);
				button.setTextSize(22);
				button.setTextColor(Color.WHITE);
				button.setBackgroundResource(R.drawable.enter_bg);
			}
		}

	}

	/**
	 * 刪除
	 */
	public static final int ACTION_DELETE = 0x2016;
	/**
	 * 取消
	 */
	public static final int ACTION_CANCEL = 0x2017;
	/**
	 * 清除
	 */
	public static final int ACTION_CLEAR = 0x2018;
	/**
	 * 确定
	 */
	public static final int ACTION_ENTER = 0x2019;

	/**
	 * 数字
	 */
	public static final int ACTION_NUM = 0x2020;

	public interface onClickListener {
		void onClick(String text, int action);
	}

	public void setOnClickListener(onClickListener listener) {
		this.listener = listener;
	}

	private OnClickListener listener2 = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (listener != null) {
				Integer i = (Integer) v.getTag();
				if (i < 11) {
					listener.onClick(num[i], ACTION_NUM);
				} else if (i == 11) {
					listener.onClick("", ACTION_DELETE);
				} else if (i == 12) {
					listener.onClick("", ACTION_CLEAR);
				} else if (i == 13) {
					listener.onClick("", ACTION_ENTER);
				}

			}
		}
	};

}