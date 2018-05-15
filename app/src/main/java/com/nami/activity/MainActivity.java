package com.nami.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.util.JsonReader;
import android.util.Log;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 主界面，主要功能：
 * 1. 从聚合数据平台接受天气数据
 * 2. 从后台服务器接受传感器数据
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainActivity";
    private MyApplication myApplication;
    private User myUser;
    private Device myDevice;
    private CardView deviceView;
    private TextView userName, deviceName, deviceTem, deviceHum, deviceCH4, deviceCO2, devicePM;
    private TextView skTem, skWeather, skWind, skHum, secWeather, secTem, thrWeather, thrTem, lastWeather, lastTem, lastWeek;
    private String url_pipe;
    private final int MSG_SUCCESS = 0;
    private final int MSG_FAIL = 1;
    private final int WEATHER_SUCCESS = 2;
    private final int WEATHER_FAIL = 3;
    private final String url_weather = "http://v.juhe.cn/weather/index?cityname=大连&dtype=json&format=&key=96f292b72fa46205d62af2a32abba3da";


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

        myApplication = (MyApplication)getApplication();
        myUser = myApplication.getUser();
        myDevice = myApplication.getMyDevice();
        userName = (TextView)headerLayout.findViewById(R.id.nav_user_name);
        deviceView = (CardView)findViewById(R.id.card_device);
        deviceName = (TextView)findViewById(R.id.dev_name);
        deviceTem = (TextView)findViewById(R.id.dev_temperature);
        deviceHum = (TextView)findViewById(R.id.dev_humidity);
        deviceCH4 = (TextView)findViewById(R.id.dev_formaldehyde);
        deviceCO2 = (TextView)findViewById(R.id.dev_CO2);
        devicePM = (TextView)findViewById(R.id.dev_PM);
        url_pipe = getResources().getString(R.string.url_pipe);

        //天气模块
        skTem = (TextView)findViewById(R.id.sk_wen);
        skWeather = (TextView)findViewById(R.id.sk_weather);
        skWind = (TextView)findViewById(R.id.sk_wind);
        skHum = (TextView)findViewById(R.id.sk_hum);
        secWeather = (TextView)findViewById(R.id.second_weather);
        secTem = (TextView)findViewById(R.id.second_temperature);
        thrWeather = (TextView)findViewById(R.id.third_weather);
        thrTem = (TextView)findViewById(R.id.third_temperature);
        lastWeather = (TextView)findViewById(R.id.last_weather);
        lastTem = (TextView)findViewById(R.id.last_temperature);
        lastWeek = (TextView)findViewById(R.id.last_week);

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

        OKHttpUtil.doGet(url_weather, weatherCallBack);

//        // 设置listView
//        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.main_recycle);
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(new RecyclerViewAdapter(MainActivity.this));
    }

    // 把获取来的Json字符串解析出来
    private void showWeather(JSONObject result){
        try {
            JSONObject skObj = result.getJSONObject("sk");
            JSONObject todayObj = result.getJSONObject("today");
            JSONObject futurObj = result.getJSONObject("future");
            skTem.setText(skObj.getString("temp"));
            skWeather.setText(todayObj.getString("weather"));
            String temp = skObj.getString("wind_direction")+skObj.getString("wind_strength");
            skWind.setText(temp);
            skHum.setText(skObj.getString("humidity"));

            int i = 0;
            Iterator<String> it = futurObj.keys();
            while (it.hasNext() && i < 4){
                String date = (String) it.next();
                JSONObject object = futurObj.getJSONObject(date);
                switch (i){
                    case 1:
                        secWeather.setText(object.getString("weather"));
                        secTem.setText(object.getString("temperature"));
                        break;
                    case 2:
                        thrWeather.setText(object.getString("weather"));
                        thrTem.setText(object.getString("temperature"));
                        break;
                    case 3:
                        lastWeather.setText(object.getString("weather"));
                        lastTem.setText(object.getString("temperature"));
                        lastWeek.setText(object.getString("week"));
                        break;
                        default:
                            break;
                }
                i++;
            }

        } catch (JSONException e) {
            Log.e(TAG, "weather Json 字符串解析失败！");
            e.printStackTrace();
        }
    }
    // 处理信息
    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MSG_SUCCESS:
                    JSONObject newsObj = (JSONObject)msg.obj;
                    try {
                        float tem = (float)newsObj.getInt("Temperature") / 10;
                        float hum = (float)newsObj.getInt("Humidity") / 10;
                        float CH4 = (float)newsObj.getInt("Formaldehyde") / 100;
                        int CO2 = newsObj.getInt("CarbonDioxide");
                        int PM = newsObj.getInt("ParticlePollutionTwoPointFive");
                        deviceTem.setText(tem + "°C");
                        deviceHum.setText(hum + "%");
                        deviceCH4.setText(CH4 + "mg/m³");
                        deviceCO2.setText(CO2 + "PPM");
                        devicePM.setText(PM + "μg/m³");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case MSG_FAIL:
                    Log.e(TAG, "获取传感器数据失败");
                    break;
                case WEATHER_SUCCESS:
                    JSONObject infoObj = (JSONObject)msg.obj;
                    showWeather(infoObj);
                    break;
                case WEATHER_FAIL:
                    Log.e(TAG, "获取天气数据失败");
                    Toast.makeText(MainActivity.this, "获取天气数据失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    // 计时器更新传感器数据
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            JSONObject jsonObject = new JSONObject();
            JSONObject device = new JSONObject();
            try {
                device.put("Uid", myDevice.getMAC());
                jsonObject.put("GetNewestStatistic", device);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            String jsonStr = jsonObject.toString();
            OKHttpUtil.doPost(myApplication.getToken(), url_pipe, jsonStr, infoCallBack);
        }
    };

    // 显示设备获取的信息
    private void showDevice(){
        if(myApplication.getIF_BIND_DEV()){
            timer.schedule(task,0,5000);
        }
    }


    private Callback infoCallBack = new  Callback() {

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
                    JSONObject newsObj = jsonObject.getJSONObject("GetNewestStatistic");
                    int code = newsObj.getInt("code");
                    if(code == 0){
                        msg.what = MSG_SUCCESS;
                        msg.obj = newsObj.getJSONObject("statistic");
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
    private Callback weatherCallBack = new  Callback() {

        @Override
        public void onFailure(Call call, IOException e) {
            Message msg = Message.obtain();
            msg.what = WEATHER_FAIL;
            myHandler.sendMessage(msg);
            e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Message msg = Message.obtain();
            msg.what = WEATHER_FAIL;

            if (response.isSuccessful()) {
                String result = response.body().string();
                Log.e(TAG, "response.body().string(): " + result);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String code = jsonObject.getString("resultcode");
                    if(code.equals("200")){
                        msg.what = WEATHER_SUCCESS;
                        msg.obj = jsonObject.getJSONObject("result");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Log.e(TAG, "weatherCallBack response fail");
            }
            myHandler.sendMessage(msg);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        userName.setText(myUser.getName());
        if(myApplication.getIF_BIND_DEV()){
            deviceView.setVisibility(View.VISIBLE);
            deviceName.setText(myDevice.getName());
//            deviceView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    startActivity(new Intent(MainActivity.this, HistoryActivity.class));
//                }
//            });
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

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
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
