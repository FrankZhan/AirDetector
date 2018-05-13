package com.nami.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nami.Entity.Device;
import com.nami.Entity.User;
import com.nami.MyApplication;
import com.nami.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView userName;
    private MyApplication myApplication;
    private User myUser;
    private CardView deviceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 添加导航栏
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        LinearLayout headerLayout = (LinearLayout)navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);

        userName = (TextView)headerLayout.findViewById(R.id.nav_user_name);
        deviceView = (CardView)findViewById(R.id.card_device);

        myApplication = (MyApplication)getApplication();
        myUser = myApplication.getUser();

        //设置悬浮按钮
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddDeviceActivity.class));
            }
        });

        //设置导航
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

//        // 设置listView
//        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.main_recycle);
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(new RecyclerViewAdapter(MainActivity.this));
    }

    // 显示设备获取的信息
    private void showDevice(){

    }

    @Override
    protected void onResume() {
        super.onResume();
        userName.setText(myUser.getName());
        if(myApplication.getIF_BIND_DEV()){
            deviceView.setVisibility(View.VISIBLE);
            deviceView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                }
            });
            showDevice();
        }else {
            deviceView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // 监听导航栏
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_device) {
            startActivity(new Intent(MainActivity.this, DeviceManageActivity.class));
        } else if (id == R.id.nav_info) {
            startActivity(new Intent(MainActivity.this, AlterInfoActivity.class));
        } else if (id == R.id.nav_help) {
            startActivity(new Intent(MainActivity.this, UserHelpActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        } else if (id == R.id.nav_dropout) {
            dropout();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // 退出登录
    private void dropout(){
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}
