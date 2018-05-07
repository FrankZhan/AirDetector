package com.nami.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nami.R;

/*
    修改设备信息
 */
public class AlterDevInfoActivity extends AppCompatActivity {

    private String TAG = "AlterDevInfoActivity";
    private EditText deviceName;
    private TextView deviceMac;
    private Button confirm;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter_dev);

        deviceName = (EditText)findViewById(R.id.edt_name);
        deviceMac = (TextView)findViewById(R.id.txt_mac);
        confirm = (Button)findViewById(R.id.bnt_go);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upLoadInfo();
            }
        });
    }

    // 上传更改的信息
    private void upLoadInfo(){
        String name = deviceName.getText().toString().trim();
        String mac = deviceMac.getText().toString().trim();
        if(name.isEmpty()){
            Toast.makeText(AlterDevInfoActivity.this, "请输入设备昵称", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
