package com.pingu.driverapp.ui.home.operation.pickup;

import android.content.Context;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pingu.driverapp.R;
import com.pingu.driverapp.data.DataManager;
import com.pingu.driverapp.data.model.ApiResponse;
import com.pingu.driverapp.data.model.Parcel;
import com.pingu.driverapp.data.model.UpdateParcelStatusResponse;
import com.pingu.driverapp.data.model.local.PendingTask;
import com.pingu.driverapp.util.APIHelper;
import com.pingu.driverapp.util.Constants;
import com.pingu.driverapp.util.DateTimeHelper;
import com.pingu.driverapp.util.Variables;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class PickupViewModel extends ViewModel {
    private DataManager.RemoteProvider remoteProvider;
    private DataManager.OfflineProvider offlineProvider;
    private CompositeDisposable disposable;
    private Context context;

    // task list
    private final MutableLiveData<Boolean> data = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    @Inject
    public PickupViewModel(Context context, DataManager.RemoteProvider remoteProvider, DataManager.OfflineProvider offlineProvider) {
        this.disposable = new CompositeDisposable();
        this.context = context;
        this.remoteProvider = remoteProvider;
        this.offlineProvider = offlineProvider;
    }

    public void pickupOrDeliverParcels(final List<Parcel> parcelList, final int type) {
        loading.setValue(true);
        try {
            final String actionType = (type == 1) ? Constants.PARCEL_STATUS_PICKUP : Constants.PARCEL_STATUS_OUT_FOR_DELIVERY;
            disposable.add(remoteProvider.updateParcelStatus(APIHelper.getParcelIdsMultipartData(parcelList),
                    APIHelper.getStatusMultipartData(actionType), null, null,
                    null, null, null)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<UpdateParcelStatusResponse>() {
                        @Override
                        public void onSuccess(UpdateParcelStatusResponse apiResponse) {

                            if (apiResponse != null && apiResponse.getStatus() == Constants.API_STATUS_SUCCESS) {
                                data.setValue(true);
                            } else {
                                String errorMsg = "";
                                if (apiResponse.getData() != null && !apiResponse.getData().isEmpty()) {
                                    for (Map.Entry<String, ApiResponse> entry : apiResponse.getData().entrySet()) {
                                        String parcelId = entry.getKey();
                                        ApiResponse errorResponse = entry.getValue();
                                        if (errorResponse.getStatus() == 0) {
                                            errorMsg += !TextUtils.isEmpty(errorResponse.getMessage()) ?
                                                    parcelId + ": " + errorResponse.getMessage() + "\n" :
                                                    context.getString(R.string.error_unexpected) + "\n";
                                        }

                                    }
                                }
                                if (TextUtils.isEmpty(errorMsg)) {
                                    errorMsg = apiResponse.getMessage();
                                }
                                error.setValue(errorMsg.trim());
                            }
                            loading.setValue(false);
                        }

                        @Override
                        public void onError(Throwable e) {
                            error.setValue(APIHelper.handleExceptionMessage(e));
                            loading.setValue(false);
                            if (!Variables.isNetworkConnected) {
                                handlePendingTask(parcelList, type);
                            }
                        }
                    }));

        } catch (Exception ex) {
            error.setValue(context.getString(R.string.error_unexpected));
            loading.setValue(false);
        }
    }

    public MutableLiveData<Boolean> getData() {
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

    private void handlePendingTask(final List<Parcel> parcelList, final int type) {
        if (parcelList != null && parcelList.size() > 0) {
            for (final Parcel parcel : parcelList) {
                createPendingJobOnFailure(parcel.getParcelId(), type);
            }
        }
    }

    public void createPendingJobOnFailure(final String parcelId, final int type) {
        PendingTask pendingTask = new PendingTask();
        pendingTask.setParcelId(parcelId);
        pendingTask.setStatusDateTime(DateTimeHelper.getNowDateTime());
        pendingTask.setActionType((type == 1) ?
                PendingTask.ACTION_TYPE_OPERATION_PICKUP :
                PendingTask.ACTION_TYPE_OPERATION_OUT_FOR_DELIVERY);

        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                offlineProvider.insertPendingTask(pendingTask);
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
}