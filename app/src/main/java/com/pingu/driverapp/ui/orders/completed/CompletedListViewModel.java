package com.pingu.driverapp.ui.orders.completed;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pingu.driverapp.data.DataManager;
import com.pingu.driverapp.data.model.CompletedParcelData;
import com.pingu.driverapp.data.model.Parcel;
import com.pingu.driverapp.util.APIHelper;
import com.pingu.driverapp.util.Constants;
import com.pingu.driverapp.util.DateTimeHelper;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class CompletedListViewModel extends ViewModel {
    private CompositeDisposable disposable;
    private DataManager.RemoteProvider remoteProvider;
    private Context context;
    private final MutableLiveData<List<Parcel>> data = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    @Inject
    public CompletedListViewModel(Context context, DataManager.RemoteProvider remoteProvider) {
        disposable = new CompositeDisposable();
        this.context = context;
        this.remoteProvider = remoteProvider;
    }

    public void loadTodayCompletedTask(final int type) {
        loading.setValue(true);
        Single<CompletedParcelData> getParcelHistoryQuery;

        if (type == Constants.TYPE_PICKUP) {
            getParcelHistoryQuery = remoteProvider.getHistoryPickupTask(DateTimeHelper.getTodayDateForQuery());
        } else if (type == Constants.TYPE_DELIVERY) {
            getParcelHistoryQuery = remoteProvider.getHistoryDeliveryTask(DateTimeHelper.getTodayDateForQuery());
        } else if (type == Constants.TYPE_DELIVERY_AND_SIGNATURE) {
            getParcelHistoryQuery = remoteProvider.getHistoryCompletedTask(DateTimeHelper.getTodayDateForQuery());
        } else {
            getParcelHistoryQuery = remoteProvider.getHistoryProblematicParcelTask(DateTimeHelper.getTodayDateForQuery());
        }

        try {
            disposable.add(getParcelHistoryQuery
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<CompletedParcelData>() {
                        @Override
                        public void onSuccess(CompletedParcelData completedParcelData) {

                            if (completedParcelData != null &&
                                    completedParcelData.getStatus() == Constants.API_STATUS_SUCCESS) {

                                data.setValue(completedParcelData.getData());
                            } else {
                                error.setValue(completedParcelData.getMessage());
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