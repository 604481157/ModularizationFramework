package com.zhangstar.app.common.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.zhangstar.app.common.manyprocess.application.BaseApplication;
import com.zhangstar.app.common.manyprocess.tools.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
 * 网络工具封装类
 * Created by zhangwenxing on 16/9/7.
 */
public class RetrofitUtil {
    public static final int CONNECT_TIMEOUT = 15;
    public static final int WRITE_TIMEOUT = 15;
    public static final int READ_TIMEOUT = 15;


    public static <S> S createService(Class<S> serviceClass, String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(createOkHttpBuilder().build())
                .build();
        return retrofit.create(serviceClass);
    }

    public static OkHttpClient.Builder createOkHttpBuilder() {
        File httpCacheDirectory = new File(BaseApplication.getInstance().getExternalCacheDir(), "responses");
        //设置缓存 10M
        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new CacheInterceptor())
                .cache(cache)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS) //
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS) //
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS) //
                .addInterceptor(progressInterceptor);


        //是否打印log
        if (LogUtil.isLoging) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClient.interceptors().add(httpLoggingInterceptor);
        }

        return okHttpClient;
    }

    /**
     * 判断网络是否可用
     *
     * @param context Context对象
     */
    public static Boolean isNetworkReachable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo current = cm.getActiveNetworkInfo();
        if (current == null) {
            return false;
        }
        return (current.isAvailable());
    }

    public static class CacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!isNetworkReachable(BaseApplication.getInstance())) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response response1;
            Response response = chain.proceed(request);

            if (isNetworkReachable(BaseApplication.getInstance())) {
                int maxAge = 60 * 60 * 24 * 7;
                // 有网络时 设置缓存超时时间1个小时
                response1 = response.newBuilder()
                        .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "max-age=" + maxAge)
                        .build();
            } else {
                // 无网络时，设置超时为2周
                int maxStale = 60 * 60 * 24 * 14;
                response1 = response.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "max-age=" + maxStale)
                        .build();
            }
            return response1;
        }
    }


    static Interceptor progressInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Response originalResponse = chain.proceed(originalRequest);
            Response response = originalResponse.newBuilder().body(new ProgressReponseBody(originalResponse.body())).build();
            return response;
        }
    };


    /**
     * 任意指定服务器
     *
     * @param baseUrl 服务器URL
     * @return API
     */
    public static <T> T getApiService(Class<T> t, String baseUrl) {
        return createService(t, baseUrl);
    }


}
