package com.nami.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchListener;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.espressif.iot.esptouch.task.__IEsptouchTask;
import com.espressif.iot.esptouch.util.EspAES;
import com.nami.Entity.Device;
import com.nami.MyApplication;
import com.nami.R;
import com.nami.network.OKHttpUtil;
import com.nami.network.UDPSocketClient;
import com.nami.network.UDPSocketServer;
import com.nami.util.EspWifiAdminSimple;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/*
导入外部库需要设置setting.gradle 和 依赖
 */

public class AddDeviceActivity extends AppCompatActivity{

    private TextView txtWiFi, txtDev;
    private EditText edtPwd;
    private RelativeLayout layoutStart, layoutInfo;
    private Button bntStart;
    private ProgressBar proAddDev;
    private String TAG="AddDeviceActivity";
    private ImageView mark;
    private String url;     // 绑定设备url
    private String severIP;
    private final int MSG_SUCCESS = 0;
    private final int MSG_FAIL = 1;
    private Device myDevice;
    private MyApplication myApplication;
    private String Uid;
    private String Tag;

    private EspWifiAdminSimple mWifiAdmin;
    private EsptouchAsyncTask3 mTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        myApplication = (MyApplication)getApplication();
        myDevice = myApplication.getMyDevice();

        url = getResources().getString(R.string.url_pipe);
        severIP = getResources().getString(R.string.severIP);
        mWifiAdmin = new EspWifiAdminSimple(this);
        mark = (ImageView)findViewById(R.id.mark);
        proAddDev = (ProgressBar)findViewById(R.id.progress_add);
        layoutStart = (RelativeLayout)findViewById(R.id.layout_start);
        layoutInfo = (RelativeLayout)findViewById(R.id.layout_info);
        txtWiFi = (TextView)findViewById(R.id.edt_wifi);
        edtPwd = (EditText)findViewById(R.id.edt_pwd);
        txtDev = (TextView)findViewById(R.id.txt_device);
        bntStart = (Button)findViewById(R.id.bnt_start);
        bntStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startConfig();
            }
        });

        // 向系统说明接受某个广播信号
        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);

    }

    private void startConfig(){
        String apSsid = txtWiFi.getText().toString().trim();
        String apPassword = edtPwd.getText().toString().trim();
        String apBssid = mWifiAdmin.getWifiConnectedBssid();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        Date date = new Date(System.currentTimeMillis());
        String jsonData = "{'token':'" + myApplication.getToken() +
                "', 'time':'"+simpleDateFormat.format(date)+
                "', 'severIP':'"+severIP+
                "'}";
        Log.d(TAG, "jsonData:"+jsonData);

        //设置展示控件
        layoutInfo.setVisibility(View.INVISIBLE);
        layoutStart.setVisibility(View.VISIBLE);

        if (__IEsptouchTask.DEBUG) {
            Log.d(TAG, "mBtnConfirm is clicked, mEdtApSsid = " + apSsid
                    + ", " + " mEdtApPassword = " + apPassword);
        }
        if(mTask != null) {
            mTask.cancelEsptouch();
        }
        // 执行espTouch
        mTask = new EsptouchAsyncTask3();
        mTask.execute(apSsid, apBssid, apPassword, jsonData);

    }

    // 监测 wifi 连接变化: 如果手机断开了WiFi连接，则中断touch
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }

            switch (action) {
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    NetworkInfo ni = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (ni != null && !ni.isConnected()) {
                        if (mTask != null) {
                            mTask.cancelEsptouch();
                            mTask = null;
                            new AlertDialog.Builder(AddDeviceActivity.this)
                                    .setMessage("Wifi disconnected or changed")
                                    .setNegativeButton(android.R.string.cancel, null)
                                    .show();
                        }
                    }
                    break;
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        // display the connected ap's ssid
        String apSsid = mWifiAdmin.getWifiConnectedSsid();
        if (apSsid != null) {
            txtWiFi.setText(apSsid);
        } else {
            txtWiFi.setText(R.string.no_wifi);
        }
        // check whether the wifi is connected
        boolean isApSsidEmpty = TextUtils.isEmpty(apSsid);
        bntStart.setEnabled(!isApSsidEmpty);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    // EspTouch 异步进程
    private class EsptouchAsyncTask3 extends AsyncTask<String, Void, List<IEsptouchResult>> {

        // without the lock, if the user tap confirm and cancel quickly enough,
        // the bug will arise. the reason is follows:
        // 0. task is starting created, but not finished
        // 1. the task is cancel for the task hasn't been created, it do nothing
        // 2. task is created
        // 3. Oops, the task should be cancelled, but it is running
        private final Object mLock = new Object();
        private ProgressDialog mProgressDialog;
        private IEsptouchTask mEsptouchTask;
        private Thread listenTask;
        private UDPSocketClient mSocketClient;
        private UDPSocketServer mSocketServer;
        private int mPortListening = 18266; //监听端口
        private int mTargetPort = 7001; //目标端口
        private int waitTime = 5000;  // 发送超时设置
        private int maxListenTime = 60000; //监听超时设置
        private Thread mTask;         //监听接受udp的ACK
        private boolean isACK;       //是否成功交互数据
        private boolean mIsInterrupt;

        // 取消ESPTouch
        public void cancelEsptouch() {
            cancel(true);
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            if (mEsptouchTask != null) {
                mEsptouchTask.interrupt();
            }
            interrupt();
        }

        //执行在工作进程
        @Override
        protected List<IEsptouchResult> doInBackground(String... params) {
            Log.d(TAG, "start doInBackground() ");
            int taskResultCount = -1;
            String jsonData;
            mIsInterrupt = false;
            synchronized (mLock) {
                // !!!NOTICE
                String apSsid = mWifiAdmin.getWifiConnectedSsidAscii(params[0]);
                String apBssid = params[1];
                String apPassword = params[2];
                jsonData = params[3];
                taskResultCount = 1;  // 找到一个设备即返回
                boolean useAes = false;
                if (useAes) {
                    byte[] secretKey = "1234567890123456".getBytes(); // TODO modify your own key
                    EspAES aes = new EspAES(secretKey);
                    mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, aes, AddDeviceActivity.this);
                } else {
                    mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, null, AddDeviceActivity.this);
                }

                mSocketClient = new UDPSocketClient();
                mSocketServer = new UDPSocketServer(mPortListening, maxListenTime, AddDeviceActivity.this);
            }
            List<IEsptouchResult> resultList = mEsptouchTask.executeForResults(taskResultCount);
            Log.d(TAG, "result: " + resultList.toString());
            IEsptouchResult firstResult = resultList.get(0);
            if(!firstResult.isCancelled() && firstResult.isSuc()){
                SendAppData(jsonData, firstResult.getInetAddress().getHostAddress());
                if(!isACK){     //若没有连接成功则返回空。
                    return null;
                }
            }
            return resultList;
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {

            Log.d(TAG, "onPostExecute");
//            proAddDev.setVisibility(View.INVISIBLE);  // 进度条
//            mark.setVisibility(View.VISIBLE);         // 结果
            if (result == null) {
                Log.d(TAG, "Create Esptouch task failed, the esptouch port could be used by other thread");
                txtDev.setText(R.string.no_device);
                mark.setImageResource(R.drawable.problem);
                Toast.makeText(AddDeviceActivity.this, "添加设备失败，请稍后再试", Toast.LENGTH_SHORT).show();
                return;
            }
            //成功配对
            IEsptouchResult firstResult = result.get(0);
            // check whether the task is cancelled and no results received
            if (!firstResult.isCancelled()) {
                if (firstResult.isSuc()) {
                    // 添加设备成功
                    Log.d(TAG, "Esptouch success, bssid = " + firstResult.getBssid()
                            + ",InetAddress = " + firstResult.getInetAddress().getHostAddress() + "\n");

                    Uid = firstResult.getBssid();
                    Tag = "Home";
                    upLoadSever();  // 把绑定信息上传服务器
                    //txtDev.setText(firstResult.getInetAddress().getHostAddress());
                } else {
                    Log.d(TAG, "Create Esptouch task failed, the esptouch port could be used by other thread");

                    proAddDev.setVisibility(View.INVISIBLE);  // 进度条
                    mark.setVisibility(View.VISIBLE);
                    txtDev.setText(R.string.no_device);
                    mark.setImageResource(R.drawable.problem);  // 结果
                    Toast.makeText(AddDeviceActivity.this, "添加设备失败，请稍后再试", Toast.LENGTH_SHORT).show();
                }
            }
        }
        //监听用户发回来的ACK
        private void listenACK(){
            mTask = new Thread() {
                public void run() {
                    long startTimestamp = System.currentTimeMillis();
                    String result;
                    long consume = System.currentTimeMillis()
                            - startTimestamp;
                    int timeout = (int) (maxListenTime - consume);

                    while (!mIsInterrupt) {
                        // change the socket's timeout

                        if (timeout < 0) {
                            if (__IEsptouchTask.DEBUG) {
                                Log.i(TAG, "esptouch timeout");
                            }
                            break;
                        }
                        result = mSocketServer.receiveString();
                        if (result != null) {
                            if (__IEsptouchTask.DEBUG) {
                                Log.i(TAG, "receive correct broadcast");
                            }
                            if (__IEsptouchTask.DEBUG) {
                                Log.i(TAG, "mSocketServer's new timeout is "
                                        + timeout + " milliseconds");
                            }
                            mSocketServer.setSoTimeout(timeout);
                            if (__IEsptouchTask.DEBUG) {
                                Log.i(TAG, "receive correct broadcast");
                            }
                            try{
                                JSONObject jsonResult = new JSONObject(result);
                                Log.e(TAG, "Receive smartConfig result:" + result);
                                int ack = jsonResult.getInt("smartConfig");
                                if(ack == 101){
                                    isACK = true;
                                }
                                break;
                            }catch(JSONException e){
                                Log.e(TAG, e.toString());
                            }catch(Exception e) {
                                Log.e(TAG, e.toString());
                                break;
                            }
                        }else {
                            if (__IEsptouchTask.DEBUG) {
                                Log.i(TAG, "receive rubiash message, just ignore");
                            }
                        }
                    }
                    interrupt();
                    Log.d(TAG, "listenACK() finish");
                }
            };
            mTask.start();
        }
        //发送用户数据
        private void SendAppData(String jsonData, String hostName){

            isACK = false;
            Timer timer = new Timer();
            listenACK();
            for(int i=0;i<3;i++){
                Log.e(TAG,"Send User Data: " + jsonData);
                mSocketClient.sendData(jsonData,
                        hostName,
                        mTargetPort,
                        waitTime);
                if(isACK){
                    break;
                }
            }
            interrupt();
        }

        private synchronized void interrupt() {
            if (!mIsInterrupt) {
                mIsInterrupt = true;
                mSocketClient.interrupt();
                mSocketServer.interrupt();
                // interrupt the current Thread which is used to wait for udp response
                if (mTask != null) {
                    mTask.interrupt();
                    mTask = null;
                }
            }
        }

    }

    // 上传更改信息
    private void upLoadSever(){
        //显示进度条

        JSONObject params = new JSONObject();
        JSONObject device = new JSONObject();
        try{
            device.put("Uid", Uid);
            device.put("Tag", Tag);
            params.put("BindDevice", device);
        }catch (JSONException e){
            Toast.makeText(getApplicationContext(), "绑定设备失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }
        String jsonStr = params.toString();
        OKHttpUtil.doPost(myApplication.getToken(), url, jsonStr, myCallBack);
    }

    // 处理信息
    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MSG_SUCCESS:
                    proAddDev.setVisibility(View.INVISIBLE);  // 进度条
                    mark.setVisibility(View.VISIBLE);
                    txtDev.setText(Tag);
                    mark.setImageResource(R.drawable.mark);  // 结果

                    myDevice.setMAC(Uid);
                    myDevice.setName(Tag);
                    myApplication.setIF_BIND_DEV(true);
                    Toast.makeText(getApplicationContext(), "绑定设备成功", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_FAIL:
                    proAddDev.setVisibility(View.INVISIBLE);  // 进度条
                    mark.setVisibility(View.VISIBLE);
                    txtDev.setText(R.string.no_device);
                    mark.setImageResource(R.drawable.problem);  // 结果
                    Toast.makeText(getApplicationContext(), "绑定设备失败", Toast.LENGTH_SHORT).show();
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
                String result = response.body().string();
                Log.d(TAG, "response.body().string(): " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject bindDevice = jsonObject.getJSONObject("BindDevice");
                    int code = bindDevice.getInt("code");
                    if(code == 0){
                        msg.what = MSG_SUCCESS;
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
}
