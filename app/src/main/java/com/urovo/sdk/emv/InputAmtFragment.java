package com.urovo.sdk.emv;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.urovo.i9000s.api.emv.EmvNfcKernelApi;
import com.urovo.sdk.R;


/**
 * 输入金额fragment
 *
 * 20210620
 *
 */
public class InputAmtFragment extends Fragment {
	protected EditText etAmt;
	protected TextView CardNotext;
	protected InputAmtView inputAmtView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_input_amt, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView(view);
	}

	@SuppressLint("ResourceAsColor")
	public void initView(View view) {
		etAmt = (EditText) view.findViewById(R.id.etAmt);
		CardNotext=(TextView) view.findViewById(R.id.CardNo);
		CardNotext.setText("CardNo:"+EmvActivity.GetCardNo());
		inputAmtView = (InputAmtView) view.findViewById(R.id.nkv);
		etAmt.addTextChangedListener(new MyEditChangeListener(etAmt));
		inputAmtView.setOnClickListener(new InputAmtView.onClickListener() {

			@Override
			public void onClick(String text, int action) {
				switch (action) {
					case InputAmtView.ACTION_DELETE:
						if (etAmt != null && etAmt.length() > 0) {
							etAmt.setText(etAmt.getText().subSequence(0, etAmt.length() - 1));
							etAmt.setSelection(etAmt.getText().length());
						}
						break;
					case InputAmtView.ACTION_NUM:
						etAmt.getEditableText().replace(etAmt.getSelectionStart(), etAmt.getSelectionEnd(), text);
						break;
					case InputAmtView.ACTION_CLEAR:
						etAmt.setText("0.0");
						break;
					case InputAmtView.ACTION_ENTER:
						continueEMV();
						getActivity().finish();
						break;
				}

			}
		});
		etAmt.setSelection(etAmt.getText().length());
	}

	public void continueEMV(){

		String amt = etAmt.getText().toString();
		Log.d("applog","amt:" + amt);
		EmvActivity.edtInput.setText(amt);
		EmvNfcKernelApi.getInstance().setAmountEx(amt,null);

	}



	/**
	 * 把文本框的显示内容进行格式化，格式化为标准的人名币金额格式（0.00）
	 *
	 * @author KuCoffee
	 */
	private class MyEditChangeListener implements TextWatcher {
		private boolean status = true;
		private EditText et3;
		private StringBuffer sb;

		public MyEditChangeListener(EditText et) {
			this.et3 = et;
			sb = new StringBuffer(et.getText());
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			if (status) {
				status = false;
				et3.setText(dealString(s.toString()));
				et3.setSelection(et3.length());
			} else {
				status = true;
			}
		}

		private String dealString(String arg1) {
			if (sb.length() != 0) {
				sb.delete(0, sb.length());
			}
			if (arg1.length() == 0) {
				return "";
			} else if (arg1.length() == 1) {
				if (arg1.equals(".")) {
					return "";
				}
				if (arg1.equals("0")) {
					return "";
				}
				double re = Double.parseDouble(arg1);
				re = re / 100;
				return re + "";
			} else if (arg1.length() == 2) {
				if (arg1.equals("00")) {
					return "";
				}
			} else {
				sb.append(arg1);
				if (sb.length() - 1 - sb.indexOf(".") == 4) {
					double content = Double.parseDouble(sb.toString());
					if (content < 0.1) {
						sb.delete(0, 3);
						sb.insert(1, ".");
					} else if (content < 1) {
						sb.delete(0, 2);
						sb.insert(2, ".");
					} else {
						int index = sb.indexOf(".");
						sb.deleteCharAt(index);
						sb.insert(index + 2, ".");
					}
				} else if (sb.length() - 1 - sb.indexOf(".") == 3) {
					if (arg1.charAt(0) == '0') {
						if (arg1.charAt(2) == '0') {
							sb.deleteCharAt(2);
						} else {
							sb.delete(0, 2);
							sb.insert(1, ".");
						}
					} else {
						int index = sb.indexOf(".");
						sb.deleteCharAt(index);
						sb.insert(index + 1, ".");
					}
				} else {
					int index = sb.indexOf(".");
					if (index > 1) {
						sb.deleteCharAt(index);
						sb.insert(index - 1, ".");
					} else {
						if (sb.charAt(0) == '0') {
							sb.insert(2, "0");
						} else {
							sb.deleteCharAt(index);
							sb.insert(index - 1, ".");
							sb.insert(index - 1, "0");
						}
					}
				}
			}
			return sb.toString();
		}
	}



}
