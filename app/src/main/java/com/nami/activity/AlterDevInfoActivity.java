package com.nami.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nami.Entity.Device;
import com.nami.MyApplication;
import com.nami.R;
import com.nami.network.OKHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/*
    修改设备信息
 */
public class AlterDevInfoActivity extends AppCompatActivity {

    private String TAG = "AlterDevInfoActivity";
    private EditText deviceName;
    private TextView deviceMac;
    private Button confirm;
    private Device myDevice;
    private MyApplication myApplication;
    private final int MSG_SUCCESS = 0;
    private final int MSG_FAIL = 1;
    private ProgressDialog dialog;
    private String url;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter_dev);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        url = getResources().getString(R.string.url_pipe);
        deviceName = (EditText)findViewById(R.id.edt_name);
        deviceMac = (TextView)findViewById(R.id.txt_mac);
        confirm = (Button)findViewById(R.id.bnt_go);

        dialog = new ProgressDialog(AlterDevInfoActivity.this);
        dialog.setMessage("正在上传信息，请稍候");
        myApplication = (MyApplication)getApplication();
        myDevice = myApplication.getMyDevice();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alterInfo();
            }
        });
    }

    @Override
    protected void onResume() {
        //每次打开都更新界面
        super.onResume();
        deviceName.setText(myDevice.getName());
        deviceMac.setText(myDevice.getMAC());
    }

    // 更改的信息
    private void alterInfo(){
        String name = deviceName.getText().toString().trim();
        String mac = myDevice.getMAC();

        if(validateUsername(name)){
            if (name.equals(myDevice.getName())){
                Toast.makeText(AlterDevInfoActivity.this, "您未更改设备信息", Toast.LENGTH_SHORT).show();
            }else {
                upLoadSever(mac, name);
            }
        }
    }
    // 上传更改信息
    private void upLoadSever(String Uid, String Tag){
        //显示进度条
        dialog.show();

        JSONObject params = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("Uid", Uid);
            jsonObject.put("Tag", Tag);
            params.put("EditDevice", jsonObject);
        }catch (JSONException e){
            Toast.makeText(getApplicationContext(), "更改信息失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }
        String jsonStr = params.toString();
        OKHttpUtil.doPost(myApplication.getToken(), url, jsonStr, myCallBack);
    }

    // 处理信息
    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MSG_SUCCESS:
                    dialog.cancel();
                    Toast.makeText(getApplicationContext(), "更改信息成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AlterDevInfoActivity.this, DeviceManageActivity.class));
                    finish();
                    break;
                case MSG_FAIL:
                    dialog.cancel();
                    Toast.makeText(getApplicationContext(), "更改信息失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };
    // Okhttp 回调函数
    private Callback myCallBack = new  Callback() {

        @Override
        public void onFailure(Call call, IOException e) {
            Message msg = Message.obtain();
            msg.what = MSG_FAIL;
            myHandler.sendMessage(msg);
            e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Message msg = Message.obtain();
            msg.what = MSG_FAIL;

            if (response.isSuccessful()) {
                String result = response.body().string();
                Log.d(TAG, "response.body().string(): " + result);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject userObj = jsonObject.getJSONObject("EditDevice");
                    int code = userObj.getInt("code");
                    if(code == 0){
                        msg.what = MSG_SUCCESS;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Log.e(TAG, "infoCallBack response fail");
            }
            myHandler.sendMessage(msg);
        }
    };

    // 校验输入信息
    private boolean validateUsername(String username){
        return username.length() > 1;
    }
}
