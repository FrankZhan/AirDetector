package com.nami.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class DeviceManageActivity extends AppCompatActivity {

    private String TAG = "DeviceMangeActivity";
    private Device myDevice;
    private final int MSG_SUCCESS = 0;
    private final int MSG_FAIL = 1;
    private ProgressDialog progressDialog;
    private String url;
    private Button device;
    private MyApplication myApplication;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_manage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        myApplication = (MyApplication)getApplication();
        myDevice = myApplication.getMyDevice();
        url = getResources().getString(R.string.url_pipe);
        progressDialog = new ProgressDialog(DeviceManageActivity.this);
        progressDialog.setMessage("正在解绑该设备，请稍候");
        device = (Button)findViewById(R.id.bnt_device);

        MyApplication application = (MyApplication)getApplication();

        // 短按编辑设备
        device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DeviceManageActivity.this, AlterDevInfoActivity.class));
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

    @Override
    protected void onResume() {
        super.onResume();
        if( !myApplication.getIF_BIND_DEV()){
            device.setEnabled(false);
            device.setText(R.string.no_device);
        }else{
            device.setText(myDevice.getName());
            device.setEnabled(true);
        }
    }

    // 长按显示解绑对话框
    private void showDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("提示信息");
        dialog.setMessage("您确定要解绑该设备吗？");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 解绑设备
                unBindDevice();
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

    // 解绑该设备
    private void unBindDevice(){

        //显示进度条
        progressDialog.show();

        JSONObject params = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("Uid", myDevice.getMAC());
            jsonObject.put("Tag", myDevice.getName());
            params.put("UnbindDevice", jsonObject);
        }catch (JSONException e){
            Toast.makeText(getApplicationContext(), "解绑设备失败", Toast.LENGTH_SHORT).show();
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
                    progressDialog.cancel();
                    myApplication.setIF_BIND_DEV(false);
                    device.setEnabled(false);
                    device.setText(R.string.no_device);
                    Toast.makeText(getApplicationContext(), "解绑设备成功", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_FAIL:
                    progressDialog.cancel();
                    Toast.makeText(getApplicationContext(), "解绑设备失败", Toast.LENGTH_SHORT).show();
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
                    JSONObject userObj = jsonObject.getJSONObject("UnbindDevice");
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
}
