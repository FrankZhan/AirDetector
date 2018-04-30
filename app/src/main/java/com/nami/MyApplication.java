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
    private static String ServerIP = "192.168.1.2";
    private static String SeverPort = "9800";

    @Override
    public void onCreate() {
        super.onCreate();
        myDevice = new Device();
        user = new User();

        Log.d("MyApplication", "onCreate()");
    }

    public Device getMyDevice() {
        return myDevice;
    }

    public User getUser() {
        return user;
    }

    public static String getServerIP() {
        return ServerIP;
    }

    public static String getSeverPort() {
        return SeverPort;
    }

    public void setMyDevice(Device myDevice) {
        this.myDevice = myDevice;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
