package com.pingu.driverapp.di.component;

import android.app.Application;

import com.pingu.driverapp.BaseApplication;
import com.pingu.driverapp.di.module.ActivityBindingModule;
import com.pingu.driverapp.di.module.AppModule;
import com.pingu.driverapp.di.module.LocalDBModule;
import com.pingu.driverapp.di.module.NetworkModule;
import com.pingu.driverapp.di.module.ViewModelModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        NetworkModule.class,
        LocalDBModule.class,
        ActivityBindingModule.class,
        AppModule.class,
        ViewModelModule.class,
})
public interface AppComponent extends AndroidInjector<BaseApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        AppComponent.Builder application(Application application);

        AppComponent build();
    }

}