package com.nami.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.nami.Entity.User;
import com.nami.MyApplication;
import com.nami.R;

public class AlterInfoActivity extends AppCompatActivity {

    private String TAG = "AlterInfoActivity";
    private User user;
    private TextView userName;
    private TextView mail;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        MyApplication application = (MyApplication)getApplication();
        user = application.getUser();
        userName = (TextView)findViewById(R.id.txt_username);
        mail = (TextView)findViewById(R.id.txt_mail);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userName.setText(user.getName());
        mail.setText(user.getEmail());
    }
}
