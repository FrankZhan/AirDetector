package com.nami;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tl_username;
    private TextInputLayout tl_password;
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);

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
        boolean flagName=true, flagPass=true;
        if(!validateUserName(username)) {
            tl_username.setErrorEnabled(true);
            tl_username.setError("请输入正确的邮箱地址");
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
        if(flagName && flagPass){
            Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
    private boolean validatePassword(String password){
        return password.length() > 6;
    }

    private boolean validateUserName(String username){
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

}
