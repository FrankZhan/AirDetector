package com.nami.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nami.R;

import java.util.Objects;

public class DeviceManageActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_manage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        Button device = (Button)findViewById(R.id.bnt_device);

        device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DeviceManageActivity.this, "您点击了button", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
