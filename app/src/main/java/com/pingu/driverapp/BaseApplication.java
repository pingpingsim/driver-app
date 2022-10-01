package com.pingu.driverapp;

import android.os.Build;

import com.pingu.driverapp.data.model.ParcelProblem;
import com.pingu.driverapp.di.component.AppComponent;
import com.pingu.driverapp.di.component.DaggerAppComponent;
import com.pingu.driverapp.util.CheckNetwork;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

public class BaseApplication extends DaggerApplication {
    private static BaseApplication instance;
    public Map<String, List<ParcelProblem>> parcelProblemReference = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        CheckNetwork network = new CheckNetwork(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            network.registerNetworkCallback();
        }
    }

    public List<ParcelProblem> getParcelProblemByType(final String problemType) {
        if (parcelProblemReference != null && parcelProblemReference.size() > 0) {
            return parcelProblemReference.get(problemType);
        }
        return null;
    }

    public static synchronized BaseApplication getInstance() {
        return instance;
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        AppComponent appComponent = DaggerAppComponent.builder().application(this).build();
        appComponent.inject(this);
        return appComponent;
    }
}
