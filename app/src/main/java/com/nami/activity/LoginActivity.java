package com.nami.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nami.Entity.Device;
import com.nami.Entity.User;
import com.nami.MyApplication;
import com.nami.R;
import com.nami.network.OKHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private String TAG = "LoginActivity";
    private TextInputLayout tl_username;
    private TextInputLayout tl_password;
    private String url;
    private String url_pipe;
    private final int MSG_SUCCESS = 0;  // 登录成功
    private final int MSG_FAIL = 1; // 登录失败
    private final int MSG_GET = 2; // 登录成功，准备接收账户信息和设备信息
    private ProgressDialog dialog;
    private MyApplication myApplication;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        url_pipe = getResources().getString(R.string.url_pipe);
        url = getResources().getString(R.string.url_login);
        tl_username = (TextInputLayout)findViewById(R.id.username);
        tl_password = (TextInputLayout)findViewById(R.id.password);
        Button bt_login = (Button) findViewById(R.id.login);
        Button bt_register = (Button) findViewById(R.id.register);
        myApplication = (MyApplication)getApplication();

        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage("正在登录中，请稍等... ...");

        bt_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                login();
            }
        });
        bt_register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistActivity.class));
            }
        });
    }

    private void login(){
        String username=tl_username.getEditText().getText().toString();
        String password=tl_password.getEditText().getText().toString();
        boolean flagPass=true, flagName=true;

        if(!validateUsername(username)){
            tl_username.setErrorEnabled(true);
            tl_username.setError("用户名字数过少");
            flagName = false;
        }else{
            tl_username.setErrorEnabled(false);
        }
        if(!validatePassword(password)) {
            tl_password.setErrorEnabled(true);
            tl_password.setError("密码字数过少");
            flagPass = false;
        }else{
            tl_password.setErrorEnabled(false);
        }

        if(flagPass && flagName){
            loginSever(username, password);
        }
    }
    // 连接服务器 确认
    private void loginSever(String Username, String Password){

        //显示进度条
        dialog.show();

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("Username", Username);
            jsonObject.put("Password", Password);
        }catch (JSONException e){
            Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }
        String jsonStr = jsonObject.toString();
        OKHttpUtil.doPost(url, jsonStr, myCallBack);
    }

    // 处理信息
    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MSG_SUCCESS:
                    dialog.cancel();
                    Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                    String result = (String)msg.obj;
                    initInfo(result);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                    break;
                case MSG_GET:
                    String token = (String)msg.obj;
                    myApplication.setToken(token);
                    Log.d(TAG, "Air-Token: " + myApplication.getToken());
                    getDevice(token);
                    break;
                case MSG_FAIL:
                    dialog.cancel();
                    Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    // 初始化用户信息
    // result: {"GetDeviceList":{"code":0,"devices":[],"message":"操作成功"},
    // "GetUserProfile":{"code":0,"message":"操作成功","user":{"ID":3,"Username":"frank","IsStaff":false,"IsSuperUser":false,"Email":"frank@163.com"}}}
    private void initInfo(String result){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
            JSONObject userObj = jsonObject.getJSONObject("GetUserProfile");
            JSONObject user = userObj.getJSONObject("user");
            User myUser = myApplication.getUser();
            myUser.setUserID(user.getInt("ID"));
            myUser.setName(user.getString("Username"));
            myUser.setEmail(user.getString("Email"));

            JSONObject devicesObj = jsonObject.getJSONObject("GetDeviceList");
            JSONArray devices = devicesObj.getJSONArray("devices");
            if(!devices.isNull(0)){
                JSONObject dev = devices.getJSONObject(0);
                Device myDev = myApplication.getMyDevice();
                myDev.setName(dev.getString("DeviceTag"));
                myDev.setMAC(dev.getString("Uid"));

                myApplication.setIF_BIND_DEV(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void getDevice(String token){

        JSONObject jsonObject = new JSONObject();
        JSONObject user = new JSONObject();
        JSONObject device = new JSONObject();
        try {
            jsonObject.put("GetUserProfile", user);
            jsonObject.put("GetDeviceList", device);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        String jsonStr = jsonObject.toString();
        OKHttpUtil.doPost(token, url_pipe, jsonStr, infoCallBack);
    }

    // Okhttp 回调函数
    private  Callback myCallBack = new  Callback() {

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
                    int code = jsonObject.getInt("code");
                    if(code == 0){
                        msg.what = MSG_GET;
                        msg.obj = response.header("Air-Token");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            myHandler.sendMessage(msg);
        }
    };

    private  Callback infoCallBack = new  Callback() {

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
                    JSONObject userObj = jsonObject.getJSONObject("GetUserProfile");
                    JSONObject deviceObj = jsonObject.getJSONObject("GetDeviceList");
                    int devCode = deviceObj.getInt("code");
                    int userCode = userObj.getInt("code");
                    if(userCode == 0 && devCode ==0){
                        msg.what = MSG_SUCCESS;
                        msg.obj = result;
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

    private boolean validatePassword(String password){
        return password.length() > 6;
    }
    private boolean validateUsername(String username){
        return username.length() > 1;
    }

}
