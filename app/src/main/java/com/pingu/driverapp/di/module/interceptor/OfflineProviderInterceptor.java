package com.pingu.driverapp.di.module.interceptor;

import com.pingu.driverapp.di.module.NetworkModule;
import com.pingu.driverapp.util.NetworkHelper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class OfflineProviderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if (!NetworkHelper.isConnected()) {
            CacheControl cacheControl = new CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build();

            request = request.newBuilder()
                    .removeHeader(NetworkModule.HEADER_PRAGMA)
                    .removeHeader(NetworkModule.HEADER_CACHE_CONTROL)
                    .cacheControl(cacheControl)
                    .build();
        }

        return chain.proceed(request);
    }
}
