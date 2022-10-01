package com.pingu.driverapp.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.pingu.driverapp.R;
import com.pingu.driverapp.data.DataManager;
import com.pingu.driverapp.databinding.AcitivtySplashBinding;
import com.pingu.driverapp.ui.login.LoginActivity;
import com.pingu.driverapp.ui.main.MainActivity;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class SplashActivity extends DaggerAppCompatActivity {
    private AcitivtySplashBinding binding;

    @Inject
    DataManager.OfflineProvider offlineProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.acitivty_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final String token = offlineProvider.getToken();
                Intent targetIntent;

                if (TextUtils.isEmpty(token)) {
                    targetIntent = new Intent(SplashActivity.this, LoginActivity.class);
                } else {
                    targetIntent = new Intent(SplashActivity.this, MainActivity.class);
                }

                startActivity(targetIntent);
                finish();
            }
        }, 2000);

    }
}
