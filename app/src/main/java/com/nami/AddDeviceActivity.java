package com.nami;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.RelativeLayout;


public class AddDeviceActivity extends AppCompatActivity {

    private TextView txtWiFi, txtDev;
    private EditText edtPwd;
    private RelativeLayout layoutStart, layoutInfo;
    private Button bntStart;
    private ProgressBar proAddDev;
    private String wifiName, wifiPwd, TAG="AddDeviceActivity";
    private ImageView mark;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        mark = (ImageView)findViewById(R.id.mark);
        proAddDev = (ProgressBar)findViewById(R.id.progress_add);
        layoutStart = (RelativeLayout)findViewById(R.id.layout_start);
        layoutInfo = (RelativeLayout)findViewById(R.id.layout_info);
        txtWiFi = (TextView)findViewById(R.id.edt_wifi);
        edtPwd = (EditText)findViewById(R.id.edt_pwd);
        txtDev = (TextView)findViewById(R.id.txt_device);
        bntStart = (Button)findViewById(R.id.bnt_start);
        bntStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startConfig();
            }
        });

    }
    private void startConfig(){
        wifiName = txtWiFi.getText().toString().trim();
        wifiPwd = edtPwd.getText().toString().trim();
        Log.d(TAG, "wifiName: "+ wifiName+" wifiPwd: " + wifiPwd);
        layoutInfo.setVisibility(View.INVISIBLE);
        layoutStart.setVisibility(View.VISIBLE);
    }

}
