package com.urovo.sdk.view;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;

import com.urovo.sdk.R;
import com.urovo.sdk.magcard.MagCardReaderImpl;
import com.urovo.sdk.magcard.listener.MagCardListener;
import com.urovo.sdk.utils.DateUtil;

import java.util.Date;

public class MagCardReaderActivity extends BaseActivity implements View.OnClickListener {

	private MagCardReaderImpl magCardReader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_magcard);
		initView();
		magCardReader = MagCardReaderImpl.getInstance();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.btnsearchCard:
				try {
					outputText("searchCard，please swipe the card in 30 seconds: "+ DateUtil.getDateTime(new Date()));
					magCardReader.searchCard(30, new MagCardListener(){
						@Override
						public void onSuccess(Bundle track) {
							outputText("onSuccess");
							outputText("PAN: "+track.getString("PAN"));
							outputText("Track 1: "+track.getString("TRACK1"));
							outputText("Track 2: "+track.getString("TRACK2"));
							outputText("Track 3: "+track.getString("TRACK3"));
							outputText("Sevice Code:"+track.getString("SERVICE_CODE"));
							outputText("Expire Date: "+track.getString("EXPIRED_DATE"));
						}

						@Override
						public void onError(int error, String message){
							outputText("onError: ret="+error+", message: "+message);
						}

						@Override
						public void onTimeout() {
							outputText("time out: "+DateUtil.getDateTime(new Date()));
						}
					});
				}catch (Exception e){
					e.printStackTrace();
				}
				break;
			case R.id.btnstopSearch:
				try {
					outputText("stopSearch");
					magCardReader.stopSearch();
				}catch (Exception e){
					e.printStackTrace();
				}
				break;
		}
	}

}
