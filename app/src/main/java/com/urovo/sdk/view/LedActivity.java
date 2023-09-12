package com.urovo.sdk.view;

import android.os.Bundle;
import android.view.View;

import com.urovo.sdk.R;
import com.urovo.sdk.led.LEDDriverImpl;

public class LedActivity extends BaseActivity implements View.OnClickListener {

	private LEDDriverImpl led;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_led);
		initView();
		led = LEDDriverImpl.getInstance();
	}

	@Override
	public void onClick(View view) {
		try {
			switch (view.getId()){
				case R.id.btnOnBlue:
					outputText("blue: open");
					led.turnOn(1);
					break;
				case R.id.btnOffBlue:
					outputText("blue: close");
					led.turnOff(1);
					break;
				case R.id.btnOnYellow:
					outputText("yellow: open");
					led.turnOn(2);
					break;
				case R.id.btnOffYellow:
					outputText("yellow: close");
					led.turnOff(2);
					break;
				case R.id.btnOnGreen:
					outputText("green: open");
					led.turnOn(3);
					break;
				case R.id.btnOffGreen:
					outputText("green: close");
					led.turnOff(3);
					break;
				case R.id.btnOnRed:
					outputText("red: open");
					led.turnOn(4);
					break;
				case R.id.btnOffRed:
					outputText("red: close");
					led.turnOff(4);
					break;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
