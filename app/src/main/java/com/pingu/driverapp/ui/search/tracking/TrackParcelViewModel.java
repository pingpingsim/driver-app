package com.pingu.driverapp.ui.search.tracking;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pingu.driverapp.data.DataManager;
import com.pingu.driverapp.data.model.ParcelStatus;
import com.pingu.driverapp.data.model.ParcelStatusData;
import com.pingu.driverapp.util.APIHelper;
import com.pingu.driverapp.util.Constants;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class TrackParcelViewModel extends ViewModel {
    private CompositeDisposable disposable;
    private DataManager.RemoteProvider remoteProvider;
    private Context context;

    private final MutableLiveData<ParcelStatus> data = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    @Inject
    public TrackParcelViewModel(final Context context, final DataManager.RemoteProvider remoteProvider) {
        disposable = new CompositeDisposable();
        this.remoteProvider = remoteProvider;
        this.context = context;
    }

    public void trackParcel(final String parcelID) {
        loading.setValue(true);
        try {
            disposable.add(remoteProvider.trackParcel(parcelID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ParcelStatusData>() {
                        @Override
                        public void onSuccess(ParcelStatusData parcelStatusData) {

                            if (parcelStatusData != null && parcelStatusData.getStatus() == Constants.API_STATUS_SUCCESS) {
                                data.setValue(parcelStatusData.getData());
                            } else {
                                error.setValue(parcelStatusData.getMessage());
                            }
                            loading.setValue(false);
                        }

                        @Override
                        public void onError(Throwable e) {
                            error.setValue(APIHelper.handleExceptionMessage(e));
                            loading.setValue(false);
                        }
                    }));

        } catch (Exception ex) {
            error.setValue(ex.getMessage());
            loading.setValue(false);
        }
    }

    public MutableLiveData<ParcelStatus> getData() {
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