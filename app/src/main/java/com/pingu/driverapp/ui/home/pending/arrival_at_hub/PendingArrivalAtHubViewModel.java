package com.pingu.driverapp.ui.home.pending.arrival_at_hub;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pingu.driverapp.R;
import com.pingu.driverapp.data.DataManager;
import com.pingu.driverapp.data.model.OutForDeliveryParcelData;
import com.pingu.driverapp.data.model.Parcel;
import com.pingu.driverapp.util.APIHelper;
import com.pingu.driverapp.util.Constants;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class PendingArrivalAtHubViewModel extends ViewModel {
    private CompositeDisposable disposable;
    private Context context;
    private DataManager.RemoteProvider remoteProvider;
    private DataManager.OfflineProvider offlineProvider;

    private final MutableLiveData<List<Parcel>> data = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public boolean isLoadingInProgress;

    @Inject
    public PendingArrivalAtHubViewModel(final Context context, final DataManager.RemoteProvider remoteProvider,
                                        final DataManager.OfflineProvider offlineProvider) {
        this.context = context;
        this.disposable = new CompositeDisposable();
        this.remoteProvider = remoteProvider;
        this.offlineProvider = offlineProvider;
        loadPendingArrivalAtHubParcels(true);
    }

    public void loadPendingArrivalAtHubParcels(final boolean showLoading) {
        if (showLoading)
            loading.setValue(true);
        isLoadingInProgress = true;

        try {
            disposable.add(remoteProvider.getPendingArrivalAtHubParcel()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<OutForDeliveryParcelData>() {
                        @Override
                        public void onSuccess(OutForDeliveryParcelData outForDeliveryParcelData) {

                            if (outForDeliveryParcelData != null && outForDeliveryParcelData.getStatus() == Constants.API_STATUS_SUCCESS) {
                                data.setValue(outForDeliveryParcelData.getData());
                            } else {
                                error.setValue(outForDeliveryParcelData.getMessage());
                            }
                            if (showLoading)
                                loading.setValue(false);
                            isLoadingInProgress = false;
                        }

                        @Override
                        public void onError(Throwable e) {
                            error.setValue(APIHelper.handleExceptionMessage(e));
                            isLoadingInProgress = false;
                            if (showLoading)
                                loading.setValue(false);
                        }
                    }));

        } catch (Exception ex) {
            error.setValue(context.getString(R.string.error_unexpected));
            isLoadingInProgress = false;
            if (showLoading)
                loading.setValue(false);
        }
    }

    public MutableLiveData<List<Parcel>> getData() {
        return data;
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
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