package com.nami;

import android.app.Application;
import android.util.Log;

import com.nami.Entity.Device;
import com.nami.Entity.User;

/**
 * 初始化全局变量
 */
public class MyApplication extends Application {

    private Device myDevice;
    private User user;
    private String token;  // Air-Token
    private boolean IF_BIND_DEV; // 是否绑定了设备, 是的话为true;

    @Override
    public void onCreate() {
        super.onCreate();
        myDevice = new Device();
        user = new User();
        token = null;
        IF_BIND_DEV = false;

        Log.d("MyApplication", "onCreate()");
    }

    public void setIF_BIND_DEV(boolean IF_BIND_DEV) {
        this.IF_BIND_DEV = IF_BIND_DEV;
    }

    public boolean getIF_BIND_DEV(){
        return IF_BIND_DEV;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Device getMyDevice() {
        return myDevice;
    }

    public User getUser() {
        return user;
    }


    public void setMyDevice(Device myDevice) {
        this.myDevice = myDevice;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
