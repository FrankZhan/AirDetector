package com.nami.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nami.R;
import com.nami.network.OKHttpUtil;

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
    private final String url =  "http://192.168.1.2/api/accounts/login";
    private final int MSG_SUCCESS = 0;
    private final int MSG_FAIL = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tl_username = (TextInputLayout)findViewById(R.id.username);
        tl_password = (TextInputLayout)findViewById(R.id.password);
        Button bt_login = (Button) findViewById(R.id.login);
        Button bt_register = (Button) findViewById(R.id.register);

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
                    Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                    break;
                case MSG_FAIL:
                    Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };
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
                Log.d(TAG, response.code() + "");
                if (response.code() == 0) {
                    msg.what = MSG_SUCCESS;
                }
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
