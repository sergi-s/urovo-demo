package com.urovo.sdk.aid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.urovo.sdk.R;
import com.urovo.sdk.view.BaseActivity;

public class UpdateAIDActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateaid);
        initView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_updateAID:
                try {
                    IccParamsInitUtil.updateAID(this, IccParamsInitUtil.getInitAIDParams());
                    IccParamsInitUtil.updateRID(this, IccParamsInitUtil.getInitCAPKParams());
                    outputText("update success");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void startActivity(Class<?> clsName) {
        Intent intent = new Intent(this, clsName);
        startActivity(intent);
        overridePendingTransition(R.anim.dync_in_from_right, R.anim.dync_out_to_left);
    }
}
