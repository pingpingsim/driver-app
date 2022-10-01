package com.pingu.driverapp.ui.main;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.pingu.driverapp.R;
import com.pingu.driverapp.databinding.ActivityMainBinding;
import com.pingu.driverapp.ui.base.BaseActivity;
import com.pingu.driverapp.ui.notification.NotificationActivity;
import com.pingu.driverapp.util.Constants;
import com.pingu.driverapp.worker.RetryWorker;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);

        initToolbar();
        initBottomNavView();
        initTopPanel();
        initPeriodicTask();
    }

    private void initPeriodicTask() {
        final WorkManager workManager = WorkManager.getInstance(getApplicationContext());

        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        final Data inputData = new Data.Builder()
                //.putString(Constants.TRACKER_MAC_ADDRESS, macAddress)
                .build();

        final PeriodicWorkRequest periodicSyncDataWork =
                new PeriodicWorkRequest.Builder(RetryWorker.class, Constants.SYNC_INTERVAL_IN_MINUTES, TimeUnit.MINUTES)
                        .addTag(Constants.PERIODIC_PENDING_TASK_CHECKER)
                        .setConstraints(constraints)
                        .setInitialDelay(5, TimeUnit.SECONDS)
                        .setInputData(inputData)
                        .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                        .build();

        workManager.enqueueUniquePeriodicWork(
                Constants.PERIODIC_PENDING_TASK_CHECKER,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicSyncDataWork
        );
    }

    private void initTopPanel() {
        mainViewModel.getGreeting().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String str) {
                binding.txtGreeting.setText(str);
            }
        });
        mainViewModel.getAccountNo().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String str) {
                binding.txtAccountNo.setText(str);
            }
        });
    }

    private void initToolbar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.mipmap.toolbar_logo, null);
        drawable = DrawableCompat.wrap(drawable);
        getSupportActionBar().setHomeAsUpIndicator(drawable);
        getSupportActionBar().setElevation(0);
    }

    private void initBottomNavView() {
        NavController navController = ((NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment))
                .getNavController();

        NavigationUI.setupWithNavController(binding.navView,
                navController);

        binding.navView.setItemIconTintList(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_notification:
                openNotificationScreen();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openNotificationScreen() {
        Intent notificationIntent = new Intent(this, NotificationActivity.class);
        startActivity(notificationIntent);
        //update last notification menu tap timestamp to backend
    }
}