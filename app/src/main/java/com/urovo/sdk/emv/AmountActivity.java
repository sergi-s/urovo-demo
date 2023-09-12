package com.urovo.sdk.emv;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.urovo.sdk.R;


/**
 * 消费
 * 
 * @author Xiray
 *
 */
public class AmountActivity extends FragmentActivity {



	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_bg);
		InputAmtFragment fm = new InputAmtFragment();
		addLayoutFragment(R.id.fragmentContainer, fm, false);
	}
	public void addLayoutFragment(int layout, Fragment fragment, boolean add) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.replace(layout, fragment);// fragment换layout
		if (add) {
			ft.addToBackStack(null);
		}
		ft.commitAllowingStateLoss();
	}
}
