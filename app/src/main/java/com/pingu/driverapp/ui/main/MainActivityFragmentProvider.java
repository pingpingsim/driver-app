package com.pingu.driverapp.ui.main;

import com.pingu.driverapp.ui.home.HomeFragment;
import com.pingu.driverapp.ui.orders.OrdersFragment;
import com.pingu.driverapp.ui.profile.ProfileFragment;
import com.pingu.driverapp.ui.search.SearchFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainActivityFragmentProvider {

    @ContributesAndroidInjector()
    abstract HomeFragment provideHomeFragment();

    @ContributesAndroidInjector()
    abstract SearchFragment provideSearchFragment();

    @ContributesAndroidInjector()
    abstract OrdersFragment provideOrdersFragment();

    @ContributesAndroidInjector()
    abstract ProfileFragment provideProfileFragment();
}



