package com.urovo.sdk.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.urovo.sdk.R;
import com.urovo.sdk.beeper.BeeperImpl;

public class BeeperActivity extends BaseActivity implements View.OnClickListener {

    private EditText editText_time;
    private EditText editText_cnts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beeper);
        initView();
        editText_time = (EditText) findViewById(R.id.editText_time);
        editText_cnts = (EditText) findViewById(R.id.editText_cnts);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnStartBeeper:
                try {
                    String time = editText_time.getText().toString();
                    if (TextUtils.isEmpty(time)) {
                        time = "2000";
                    }
                    String cnts = editText_cnts.getText().toString();
                    if (TextUtils.isEmpty(cnts) || TextUtils.equals("0", cnts)) {
                        cnts = "1";
                    }
                    outputText("startBeep," + time + "ms" + ", cnts:" + cnts);
                    BeeperImpl.getInstance().startBeep(Integer.parseInt(cnts), Integer.parseInt(time));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnStopBeeper:
                try {
                    outputText("stopBeep");
                    BeeperImpl.getInstance().stopBeep();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

}
