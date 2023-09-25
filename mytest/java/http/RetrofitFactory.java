package com.example.aidltest.http;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.Utils;
import com.example.aidltest.Config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit工具类获
 *
 * @author csp
 * @date 2017/9/21
 */
public class RetrofitFactory {
    private ApiService service;

    private RetrofitFactory() {
        //   日志拦截器
        HttpLoggingInterceptor interceptor =
                new HttpLoggingInterceptor(message -> Log.i("HttpLoggingInterceptor", message));
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
                        .addInterceptor(new HttpCacheInterceptor())
                        .addNetworkInterceptor(new HttpCacheInterceptor())
                        .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                        .cache(cache);
        if (AppUtils.isAppDebug()) {
            okHttpClientBuilder.addInterceptor(interceptor);
        }

        Retrofit retrofit =
                new Retrofit.Builder()
                        .client(okHttpClientBuilder.build())
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .baseUrl(Config.BASE_URL)
                        .build();
        service = retrofit.create(ApiService.class);
    }

    /** 创建单例 */
    private static class SingletonHolder {
        private static final RetrofitFactory INSTANCE = new RetrofitFactory();
    }

    public static ApiService getApiService() {
        return SingletonHolder.INSTANCE.service;
    }

    private static class HttpCacheInterceptor implements Interceptor {

        @NonNull
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetworkUtils.isConnected()) {
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
                LogUtils.d("no network");
            }

            Response originalResponse = chain.proceed(request);
            if (NetworkUtils.isConnected()) {
                // 有网的时候读接口上的@Headers里的配置，可以在这里进行统一的设置
                String cacheControl = request.cacheControl().toString();
                return originalResponse
                        .newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
            } else {
                return originalResponse
                        .newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=2419200")
                        .removeHeader("Pragma")
                        .build();
            }
        }
    }

    private static SSLSocketFactory getSSLSocketFactory(Context context, int[] certificates) {

        if (context == null) {
            throw new NullPointerException("context == null");
        }

        CertificateFactory certificateFactory;
        SSLContext sslContext = null;
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);

            for (int i = 0; i < certificates.length; i++) {
                InputStream certificate = context.getResources().openRawResource(certificates[i]);
                keyStore.setCertificateEntry(
                        String.valueOf(i), certificateFactory.generateCertificate(certificate));

                try {
                    certificate.close();
                } catch (IOException ignored) {
                }
            }
            sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            X509TrustManager trustManager =
                    (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

        } catch (IOException
                | CertificateException
                | NoSuchAlgorithmException
                | KeyManagementException
                | KeyStoreException e) {
            e.printStackTrace();
        }
        return sslContext != null ? sslContext.getSocketFactory() : null;
    }

    protected static HostnameVerifier getHostnameVerifier(final String[] hostUrls) {

        return (hostname, session) -> {
            boolean ret = false;
            for (String host : hostUrls) {
                if (host.equalsIgnoreCase(hostname)) {
                    ret = true;
                }
            }
            return ret;
        };
    }
}
