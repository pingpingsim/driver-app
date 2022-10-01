package com.pingu.driverapp.ui.profile;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.pingu.driverapp.data.DataManager;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class ProfileViewModel extends ViewModel {
    private CompositeDisposable disposable;
    private Context context;
    private DataManager.OfflineProvider offlineProvider;

    @Inject
    public ProfileViewModel(Context context, DataManager.OfflineProvider offlineProvider) {
        this.context = context;
        this.offlineProvider = offlineProvider;
    }

    public void deleteLocalData() {
        deletePendingPickupOrder();
        deletePendingDeliveryOrder();
        deletePendingTask();
        deleteOfflineCache();
    }

    private void deleteOfflineCache() {
        clearToken();

        offlineProvider.setProfile(null);

        offlineProvider.setAvailableAddress(0);
        offlineProvider.setPendingPickup(0);
        offlineProvider.setPendingDelivery(0);
        offlineProvider.setTodayPickup(0);
        offlineProvider.setTodayDelivery(0);
        offlineProvider.setTodayCompleted(0);
        offlineProvider.setTodayProblematic(0);

        offlineProvider.setHistoryPickup(0);
        offlineProvider.setHistoryDelivery(0);
        offlineProvider.setHistoryCompleted(0);
        offlineProvider.setHistoryProblematic(0);
        //delete photo folder
    }

    private void deletePendingPickupOrder() {

        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                offlineProvider.deleteAllPendingPickupOrder();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }
        });
    }

    private void deletePendingDeliveryOrder() {

        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                offlineProvider.deleteAllPendingDeliveryOrder();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }
        });
    }

    private void deletePendingTask() {

        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                offlineProvider.deleteAllPendingTask();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }
        });
    }

    public void clearToken() {
        offlineProvider.setToken(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null) {
            disposable.clear();
            disposable = null;
        }
    }
}