package com.pingu.driverapp.di.module.interceptor;

import com.pingu.driverapp.di.module.NetworkModule;
import com.pingu.driverapp.util.NetworkHelper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Response;

public class CacheInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        CacheControl cacheControl;

        if (NetworkHelper.isConnected()) {
            cacheControl = new CacheControl.Builder()
                    .maxAge(0, TimeUnit.SECONDS)
                    .build();
        } else {
            cacheControl = new CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build();
        }

        return response.newBuilder()
                .removeHeader(NetworkModule.HEADER_PRAGMA)
                .removeHeader(NetworkModule.HEADER_CACHE_CONTROL)
                .header(NetworkModule.HEADER_CACHE_CONTROL, cacheControl.toString())
                .build();
    }
}
