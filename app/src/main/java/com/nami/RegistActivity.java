package com.nami;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistActivity extends AppCompatActivity {


    private TextInputLayout tl_username;
    private TextInputLayout tl_password1;
    private TextInputLayout tl_password2;
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;

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
        String password1=tl_password1.getEditText().getText().toString();
        String password2=tl_password2.getEditText().getText().toString();
        boolean flagName=true, flagPass=true;
        if(!validateUserName(username)) {
            tl_username.setErrorEnabled(true);
            tl_username.setError("请输入正确的邮箱地址");
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

        if(flagName && flagPass){
            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegistActivity.this, LoginActivity.class));
            finish();
        }
    }
    private boolean validatePassword(String password){
        return password.length() > 6;
    }
    private boolean validateUserName(String username){
        matcher = pattern.matcher(username);
        return matcher.matches();
    }
}
