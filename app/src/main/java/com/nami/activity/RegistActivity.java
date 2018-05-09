package com.nami.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nami.R;
import com.nami.network.OKHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegistActivity extends AppCompatActivity {


    private String TAG = "RegisterActivity";
    private String url = "http://192.168.1.2/api/accounts/register";
    private TextInputLayout tl_username;
    private TextInputLayout tl_mail;
    private TextInputLayout tl_password1;
    private TextInputLayout tl_password2;
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;
    private final int MSG_SUCCESS = 0;
    private final int MSG_FAIL = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        tl_username = (TextInputLayout)findViewById(R.id.username);
        tl_mail = (TextInputLayout)findViewById(R.id.e_mail);
        tl_password1 = (TextInputLayout)findViewById(R.id.password1);
        tl_password2 = (TextInputLayout)findViewById(R.id.password2);

        //设置悬浮按钮
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    //注册
    private void register(){
        String username=tl_username.getEditText().getText().toString();
        String email = tl_mail.getEditText().getText().toString();
        String password1=tl_password1.getEditText().getText().toString();
        String password2=tl_password2.getEditText().getText().toString();
        boolean flagName=true, flagPass=true, flagMail=true;
        if(!validateEmail(email)){
            tl_mail.setErrorEnabled(true);
            tl_mail.setError("请输入正确的邮箱地址");
            flagMail = false;
        }else{
            tl_mail.setErrorEnabled(false);
        }
        if(!validateUsername(username)) {
            tl_username.setErrorEnabled(true);
            tl_username.setError("用户名字数过少");
            flagName = false;
        }else{
            tl_username.setErrorEnabled(false);
        }
        if(!validatePassword(password1)) {
            tl_password1.setErrorEnabled(true);
            tl_password1.setError("密码字数过少");
            flagPass = false;
        }else{
            tl_password1.setErrorEnabled(false);
            if(password1.equals(password2)){
                tl_password2.setErrorEnabled(false);
            }else{
                tl_password2.setEnabled(true);
                tl_password2.setError("两次密码不一致");
                flagPass = false;
            }
        }

        if(flagName && flagPass && flagMail){
            registSever(username, email, password1);
        }
    }

    // 连接服务器 注册
    private void registSever(String Username, String Email, String Password){

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("Username", Username);
            jsonObject.put("Password", Password);
            jsonObject.put("Email", Email);
        }catch (JSONException e){
            Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistActivity.this, LoginActivity.class));
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
                Log.d(TAG, response.code() + "");
                if (response.code() == 0) {
                    msg.what = MSG_SUCCESS;
                }
            }
            myHandler.sendMessage(msg);
        }
    };

    // 校验输入的数据是否符合要求
    private boolean validateUsername(String username){
        return username.length() > 1;
    }
    private boolean validatePassword(String password){
        return password.length() > 6;
    }
    private boolean validateEmail(String username){
        matcher = pattern.matcher(username);
        return matcher.matches();
    }
}
