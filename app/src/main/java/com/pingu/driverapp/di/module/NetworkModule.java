package com.pingu.driverapp.di.module;

import android.content.Context;

import com.google.gson.Gson;
import com.pingu.driverapp.BaseApplication;
import com.pingu.driverapp.BuildConfig;
import com.pingu.driverapp.data.remote.CachedApiService;
import com.pingu.driverapp.data.remote.NonCachedApiService;
import com.pingu.driverapp.di.module.interceptor.CacheInterceptor;
import com.pingu.driverapp.di.module.interceptor.NetworkConnectionInterceptor;
import com.pingu.driverapp.di.module.interceptor.OfflineProviderInterceptor;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {
    private static final int CACHE_SIZE = 10 * 1024 * 1024;//10M cache size
    private static int REQUEST_TIMEOUT = 16;

    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_PRAGMA = "Pragma";

    @Singleton
    @Provides
    @Named("cachedOkHttpClient")
    final OkHttpClient providesClient() {

        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);
            builder.readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);
            builder.cache(new Cache(BaseApplication.getInstance().getCacheDir(), CACHE_SIZE));
            builder.addInterceptor(new OfflineProviderInterceptor());
            builder.addNetworkInterceptor(new CacheInterceptor());
            return builder.build();
        } catch (Exception e) {
        }
        return null;
    }

    @Singleton
    @Provides
    @Named("cachedRetrofit")
    Retrofit provideRetrofit(Gson gson, @Named("cachedOkHttpClient") OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        return retrofit;
    }

    @Singleton
    @Provides
    static CachedApiService provideAPIService(@Named("cachedRetrofit") Retrofit retrofit) {
        return retrofit.create(CachedApiService.class);
    }

    @Singleton
    @Provides
    @Named("nonCachedOkHttpClient")
    final OkHttpClient providesNonCachedClient() {

        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);
            builder.readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);
            builder.addInterceptor(new NetworkConnectionInterceptor());
            builder.cache(null);
            return builder.build();
        } catch (Exception e) {
        }

        return null;
    }

    @Singleton
    @Provides
    @Named("nonCachedRetrofit")
    Retrofit provideNonCachedRetrofit(Gson gson, @Named("nonCachedOkHttpClient") OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        return retrofit;
    }

    @Singleton
    @Provides
    static NonCachedApiService provideNonCachedAPIService(@Named("nonCachedRetrofit") Retrofit retrofit) {
        return retrofit.create(NonCachedApiService.class);
    }

    @Provides
    @Singleton
    Cache provideCache(Context context) {
        return new Cache(context.getCacheDir(), CACHE_SIZE);
    }
}
