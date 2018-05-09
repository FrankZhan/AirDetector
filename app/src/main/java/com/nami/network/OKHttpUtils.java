package com.nami.network;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
    OKHttp 封装： 具体详见 https://blog.csdn.net/fightingXia/article/details/70947701
    设置请求头
    Request request = new Request.Builder()
        .url("http://www.baidu.com")
        .header("User-Agent", "OkHttp Headers.java")
        .addHeader("token", "myToken")
        .build();
 **/


public class OKHttpUtils {

    private static String TAG = "OKHttpUtils";
    private OkHttpClient client;

    public OKHttpUtils(){
        client = new OkHttpClient();
    }

    public void getDataAsync() {

        Request request = new Request.Builder()
                .url("http://www.baidu.com")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){//回调的方法执行在子线程。
                    Log.d("kwwl","获取数据成功了");
                    Log.d("kwwl","response.code()=="+response.code());
                    Log.d("kwwl","response.body().string()=="+response.body().string());
                }
            }
        });
    }

    private void postDataWithParame(String urlStr, String jsonStr) {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");  //数据类型为json格式
        RequestBody body = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder()
                .url(urlStr)
                .addHeader("Air-Token", "token")
                .post(body)
                .build();
        //String result;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){//回调的方法执行在子线程。

                    if(response.code() == 0){
                        Log.d(TAG,"获取数据成功了");
                        assert response.body() != null;
                        String result = response.body().string();
                        Log.d(TAG, "response.body().string(): " + result);
                    }else{
                        Log.e(TAG,"response.code(): "+response.code());

                    }

                }
            }
        });
    }
}
