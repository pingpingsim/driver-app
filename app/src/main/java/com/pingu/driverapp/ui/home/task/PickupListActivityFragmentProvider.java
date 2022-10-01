package com.pingu.driverapp.ui.home.task;

import com.pingu.driverapp.ui.home.task.all_tasks.PickupAllTasksFragment;
import com.pingu.driverapp.ui.home.task.available_tasks.PickupAvailableTasksFragment;
import com.pingu.driverapp.ui.home.task.my_tasks.PickupMyTasksFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class PickupListActivityFragmentProvider {

    @ContributesAndroidInjector()
    abstract PickupAllTasksFragment providePickupAllTasksFragment();

    @ContributesAndroidInjector()
    abstract PickupAvailableTasksFragment providePickupAvailableTasksFragment();

    @ContributesAndroidInjector()
    abstract PickupMyTasksFragment providePickupMyTasksFragment();
}



