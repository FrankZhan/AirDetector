package com.nami.network;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OKHttpUtil {
    /**
     *
     * OKHttp工具包，可以获取实例自定义包，也可以使员工封装好的异步get和post方法
     * get和post方法均需要自定义回调函数
     */
    private static OkHttpClient okHttpClient = null;
    private static final String TAG = "OKHttpUtil";


    private OKHttpUtil() {
    }

    public static OkHttpClient getInstance() {
        if (okHttpClient == null) {
            //加同步安全
            synchronized (OKHttpUtil.class) {
                if (okHttpClient == null) {
                    //okhttp可以缓存数据....指定缓存路径
                    File sdcache = new File(Environment.getExternalStorageDirectory(), "cache");
                    //指定缓存大小
                    int cacheSize = 10 * 1024 * 1024;

                    okHttpClient = new OkHttpClient.Builder()//构建器
                            .connectTimeout(10, TimeUnit.SECONDS)//连接超时
                            .writeTimeout(15, TimeUnit.SECONDS)//写入超时
                            .readTimeout(15, TimeUnit.SECONDS)//读取超时
                            //添加应用拦击器,添加公共参数
//                            .addInterceptor(new CommonParamsInterceptor())//添加应用拦击器,添加公共参数

                            .cache(new Cache(sdcache.getAbsoluteFile(), cacheSize))//设置缓存
                            .build();
                }
            }
        }
        return okHttpClient;
    }

    /**
     * get请求
     * 参数1 url
     * 参数2 回调Callback
     */

    public static void doGet(String url, Callback callback) {

        //创建OkHttpClient请求对象
        OkHttpClient okHttpClient = getInstance();
        //创建Request
        Request request = new Request.Builder()
                .url(url)
                .build();

        Log.d(TAG, "Get: " + url);
        //得到Call对象
        Call call = okHttpClient.newCall(request);
        //执行异步请求
        call.enqueue(callback);

    }

    /**
     * post请求
     * 参数1 url
     * 参数2 Json字符串
     * 参数3 回调callback
     */

    public static void doPost(String url, String params, Callback callback) {

        //创建OkHttpClient请求对象
        OkHttpClient okHttpClient = getInstance();
        //3.x版本post请求换成FormBody 封装键值对参数

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, params);

        Log.d(TAG, "Post: " + params);
        //创建Request
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);

    }

    // 添加令牌头
    public static void doPost(String token, String url, String params, Callback callback) {

        //创建OkHttpClient请求对象
        OkHttpClient okHttpClient = getInstance();
        //3.x版本post请求换成FormBody 封装键值对参数

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, params);

        Log.d(TAG, "token: " + token);
        Log.d(TAG, "Post: " + params);
        //创建Request
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Air-Token", token)
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);

    }

}