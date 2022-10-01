package com.pingu.driverapp.di.module.interceptor;

import com.pingu.driverapp.di.module.NetworkModule;
import com.pingu.driverapp.di.module.exception.NoConnectivityException;
import com.pingu.driverapp.util.NetworkHelper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkConnectionInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!NetworkHelper.isConnected()) {
            throw new NoConnectivityException();
        }

        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }
}
