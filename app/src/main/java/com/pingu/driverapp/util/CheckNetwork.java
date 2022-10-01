package com.pingu.driverapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.pingu.driverapp.worker.RetryWorker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class CheckNetwork {
    private Context context;

    public CheckNetwork(Context context) {
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void registerNetworkCallback() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkRequest.Builder builder = new NetworkRequest.Builder();

            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                                                                   @Override
                                                                   public void onAvailable(Network network) {
                                                                       Variables.isNetworkConnected = true;
                                                                       retryWorker();
                                                                   }

                                                                   @Override
                                                                   public void onLost(Network network) {
                                                                       Variables.isNetworkConnected = false;
                                                                   }
                                                               }

            );
            Variables.isNetworkConnected = false;
        } catch (Exception e) {
            Variables.isNetworkConnected = false;
        }
    }

    private void retryWorker() {
        final Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(false)
                //.setRequiredNetworkType(NetworkType.METERED)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        final Data inputData = new Data.Builder()
                //.putString(Constants.EXTRA_KEY_MEMBER_NAME, memberName)
                .build();

        final OneTimeWorkRequest retryApiCallWorkRequest =
                new OneTimeWorkRequest.Builder(RetryWorker.class)
                        .setConstraints(constraints)
                        .setInputData(inputData)
//                            .setInitialDelay(intervalMillis, TimeUnit.MILLISECONDS)
//                            .addTag(workRequestUniqueTag)
                        .build();

        WorkManager.getInstance(context).enqueue(retryApiCallWorkRequest);
    }
}
