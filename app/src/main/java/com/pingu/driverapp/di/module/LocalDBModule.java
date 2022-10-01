package com.pingu.driverapp.di.module;


import android.content.Context;

import com.pingu.driverapp.data.local.db.LocalRoomDatabase;

import dagger.Module;
import dagger.Provides;

@Module
public class LocalDBModule {

    @Provides
    LocalRoomDatabase provideHealthRoomDatabase(Context context) {
        return LocalRoomDatabase.getDatabase(context);
    }
}
