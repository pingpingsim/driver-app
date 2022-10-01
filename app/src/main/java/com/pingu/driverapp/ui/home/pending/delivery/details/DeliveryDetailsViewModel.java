package com.pingu.driverapp.ui.home.pending.delivery.details;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class DeliveryDetailsViewModel extends ViewModel {
    private CompositeDisposable disposable;
    private Context context;

    @Inject
    public DeliveryDetailsViewModel(Context context) {
        this.context = context;
        disposable = new CompositeDisposable();
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