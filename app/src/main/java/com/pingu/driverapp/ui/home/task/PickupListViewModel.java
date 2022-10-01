package com.pingu.driverapp.ui.home.task;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class PickupListViewModel extends ViewModel {
    private CompositeDisposable disposable;
    private Context context;

    @Inject
    public PickupListViewModel(Context context) {
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