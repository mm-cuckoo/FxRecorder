package com.cfox.fxrecorder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.cfox.fxrecorder.R;

public class MainActivity extends FragmentActivity implements View.OnClickListener{

    private Button mBtnOpenRec;
    private Button mBtnOpenPly;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initEvn();
    }

    private void initView() {
        mBtnOpenRec = (Button) findViewById(R.id.btn_open_rec);
        mBtnOpenPly = (Button) findViewById(R.id.btn_open_ply);
    }

    private void initEvn() {
        mBtnOpenRec.setOnClickListener(this);
        mBtnOpenPly.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open_rec:
                Intent intentRec = new Intent(this, RecorderActivity.class);
                startActivity(intentRec);
                break;

            case R.id.btn_open_ply:
                Intent intentPly = new Intent(this, PlayerActivity.class);
                startActivity(intentPly);
                break;
        }
    }
}
