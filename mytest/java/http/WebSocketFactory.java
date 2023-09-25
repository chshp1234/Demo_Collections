package com.example.aidltest.http;

import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.example.aidltest.Config;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.ByteString;

public class WebSocketFactory {
    public OkHttpClient client;
    private WebSocketFactory webSocketFactory;

    private WebSocketFactory() {
        //   日志拦截器
        HttpLoggingInterceptor interceptor =
                new HttpLoggingInterceptor(message -> Log.i("WebSocketFactory", message));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        File cacheFile = new File(Utils.getApp().getCacheDir(), "cache");
        // 缓存100Mb
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100);

        //        int[] certificates = {R.raw.myssl};
        HttpsUtils.SSLParams sslParams = null;
        try {
            sslParams =
                    HttpsUtils.getSslSocketFactory(Utils.getApp().getAssets().open("myssl.cer"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        OkHttpClient.Builder okHttpClientBuilder =
                new OkHttpClient.Builder()
                        .writeTimeout(Config.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                        .readTimeout(Config.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                        .connectTimeout(Config.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                        .retryOnConnectionFailure(true)
                        //                        .sslSocketFactory(sslParams.sSLSocketFactory,
                        // sslParams.trustManager)
                        .cache(cache);
        if (AppUtils.isAppDebug()) {
            okHttpClientBuilder.addInterceptor(interceptor);
        }

        client = okHttpClientBuilder.build();
    }

    /** 创建单例 */
    private static class SingletonHolder {
        private static final WebSocketFactory INSTANCE = new WebSocketFactory();
    }

    public static WebSocketFactory getInstance() {
        return WebSocketFactory.SingletonHolder.INSTANCE;
    }

    public WebSocket getWebSocket(String url) {
        Request request = new Request.Builder().url(url).build();
        return client.newWebSocket(
                request,
                new WebSocketListener() {
                    @Override
                    public void onOpen(WebSocket webSocket, Response response) {
                        LogUtils.d("onOpen:" + response);
                        super.onOpen(webSocket, response);
                    }

                    @Override
                    public void onMessage(WebSocket webSocket, String text) {
                        LogUtils.d("onMessage:" + text);
                        super.onMessage(webSocket, text);
                    }

                    @Override
                    public void onMessage(WebSocket webSocket, ByteString bytes) {
                        LogUtils.d("onMessage:" + bytes.toString());
                        super.onMessage(webSocket, bytes);
                    }

                    @Override
                    public void onClosing(WebSocket webSocket, int code, String reason) {
                        LogUtils.d("onClosing:" + code + " " + reason);
                        super.onClosing(webSocket, code, reason);
                    }

                    @Override
                    public void onClosed(WebSocket webSocket, int code, String reason) {
                        LogUtils.d("onClosed:" + code + " " + reason);
                        super.onClosed(webSocket, code, reason);
                    }

                    @Override
                    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                        LogUtils.d("onFailure:" + t + " " + response);
                        super.onFailure(webSocket, t, response);
                    }
                });
    }
}
