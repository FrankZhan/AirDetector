package com.nami.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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

        // 短按编辑设备
        device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DeviceManageActivity.this, AlterDevInfoActivity.class));
                Toast.makeText(DeviceManageActivity.this, "您点击了button", Toast.LENGTH_SHORT).show();
            }
        });
        //长按解绑设备
        device.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDialog();
                return false;
            }
        });

    }

    private void showDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("提示信息");
        dialog.setMessage("您确定要解绑该设备吗？");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 解绑设备
                Toast.makeText(DeviceManageActivity.this, "解绑该设备", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 取消解绑设备
            }
        });
        dialog.show();
    }
}
