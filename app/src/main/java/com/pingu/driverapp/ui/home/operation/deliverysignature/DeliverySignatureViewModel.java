package com.pingu.driverapp.ui.home.operation.deliverysignature;

import android.content.Context;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pingu.driverapp.R;
import com.pingu.driverapp.data.DataManager;
import com.pingu.driverapp.data.model.ApiResponse;
import com.pingu.driverapp.data.model.Parcel;
import com.pingu.driverapp.data.model.ParcelStatusData;
import com.pingu.driverapp.data.model.UpdateParcelStatusResponse;
import com.pingu.driverapp.data.model.local.PendingTask;
import com.pingu.driverapp.util.APIHelper;
import com.pingu.driverapp.util.Constants;
import com.pingu.driverapp.util.DateTimeHelper;
import com.pingu.driverapp.util.FileHelper;
import com.pingu.driverapp.util.Variables;

import java.io.File;
import java.util.ArrayList;
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

public class DeliverySignatureViewModel extends ViewModel {
    private DataManager.RemoteProvider remoteProvider;
    private DataManager.OfflineProvider offlineProvider;
    private CompositeDisposable disposable;
    private Context context;

    // task list
    private final MutableLiveData<Boolean> data = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isRetryTriggered = new MutableLiveData<>();

    private final MutableLiveData<String> parcelRecipientName = new MutableLiveData<>();
    private final MutableLiveData<String> parcelDataError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> parcelDataLoading = new MutableLiveData<>();

    @Inject
    public DeliverySignatureViewModel(Context context,
                                      DataManager.RemoteProvider remoteProvider,
                                      DataManager.OfflineProvider offlineProvider) {
        this.disposable = new CompositeDisposable();
        this.remoteProvider = remoteProvider;
        this.context = context;
        this.offlineProvider = offlineProvider;
    }

    public void getParcelInfo(final String parcelID) {
        parcelDataLoading.setValue(true);
        try {
            disposable.add(remoteProvider.trackParcel(parcelID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ParcelStatusData>() {
                        @Override
                        public void onSuccess(ParcelStatusData parcelStatusData) {

                            if (parcelStatusData != null && parcelStatusData.getStatus() == Constants.API_STATUS_SUCCESS) {
                                parcelRecipientName.setValue(parcelStatusData.getData().getRecipientName());
                            } else {
                                //parcelDataError.setValue(parcelStatusData.getMessage());
                            }
                            parcelDataLoading.setValue(false);
                        }

                        @Override
                        public void onError(Throwable e) {
                            //parcelDataError.setValue(APIHelper.handleExceptionMessage(e));
                            parcelDataLoading.setValue(false);
                        }
                    }));

        } catch (Exception ex) {
            //parcelDataError.setValue(ex.getMessage());
            parcelDataLoading.setValue(false);
        }
    }


    public void completeParcelDelivery(final String parcelID, final String recipientIC,
                                       final String recipientName, final File parcelPhoto, final File parcelSignature) {
        loading.setValue(true);
        final List<Parcel> parcelList = new ArrayList<>();
        final Parcel parcel = new Parcel();
        parcel.setParcelId(parcelID);
        parcelList.add(parcel);
        try {
            disposable.add(remoteProvider.updateParcelStatus(APIHelper.getParcelIdsMultipartData(parcelList),
                    APIHelper.getStatusMultipartData(Constants.PARCEL_STATUS_COMPLETE),
                    APIHelper.getRecipientICMultipartData(recipientIC),
                    APIHelper.getRecipientNameMultipartData(recipientName),
                    APIHelper.getSignatureMultipartData(parcelSignature),
                    APIHelper.getPhotoMultipartData(parcelPhoto), null)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<UpdateParcelStatusResponse>() {
                        @Override
                        public void onSuccess(UpdateParcelStatusResponse apiResponse) {

                            if (apiResponse != null && apiResponse.getStatus() == Constants.API_STATUS_SUCCESS) {
                                data.setValue(true);
                                FileHelper.deleteFile(parcelPhoto);
                                FileHelper.deleteFile(parcelSignature);
                            } else {
                                String errorMsg = apiResponse.getMessage();
                                if (apiResponse.getData() != null && !apiResponse.getData().isEmpty()) {
                                    Map.Entry<String, ApiResponse> entry = apiResponse.getData().entrySet().iterator().next();
                                    String parcelId = entry.getKey();
                                    ApiResponse errorResponse = entry.getValue();
                                    errorMsg = !TextUtils.isEmpty(errorResponse.getMessage()) ?
                                            parcelId + ": " + errorResponse.getMessage() :
                                            context.getString(R.string.error_unexpected);
                                }
                                error.setValue(errorMsg);
                            }
                            loading.setValue(false);
                            isRetryTriggered.setValue(false);
                        }

                        @Override
                        public void onError(Throwable e) {
                            error.setValue(APIHelper.handleExceptionMessage(e));
                            loading.setValue(false);
                            if (!Variables.isNetworkConnected) {
                                isRetryTriggered.setValue(true);
                                handlePendingTask(parcelList, recipientIC, recipientName, parcelPhoto, parcelSignature);
                            } else {
                                isRetryTriggered.setValue(false);
                            }
                        }
                    }));

        } catch (Exception ex) {
            isRetryTriggered.setValue(false);
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

    public MutableLiveData<Boolean> getIsRetryTriggered() {
        return isRetryTriggered;
    }

    public MutableLiveData<String> getParcelRecipientName() {
        return parcelRecipientName;
    }

    public MutableLiveData<String> getParcelDataError() {
        return parcelDataError;
    }

    public MutableLiveData<Boolean> getParcelDataLoading() {
        return parcelDataLoading;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null) {
            disposable.clear();
            disposable = null;
        }
    }

    private void handlePendingTask(final List<Parcel> parcelList, final String recipientIC,
                                   final String recipientName, final File parcelPhoto, final File parcelSignature) {
        if (parcelList != null && parcelList.size() > 0) {
            for (final Parcel parcel : parcelList) {
                createPendingJobOnFailure(parcel.getParcelId(), recipientIC, recipientName,
                        parcelSignature != null ? parcelSignature.getName() : "",
                        parcelPhoto != null ? parcelPhoto.getName() : "");
            }
        }
    }

    public void createPendingJobOnFailure(final String parcelId, final String recipientIC,
                                          final String recipientName, final String signatureFileName,
                                          final String photoFileName) {
        PendingTask pendingTask = new PendingTask();
        pendingTask.setParcelId(parcelId);
        pendingTask.setStatusDateTime(DateTimeHelper.getNowDateTime());
        pendingTask.setActionType(PendingTask.ACTION_TYPE_OPERATION_COMPLETED);
        pendingTask.setRecipientIc(recipientIC);
        pendingTask.setRecipientName(recipientName);
        pendingTask.setSignatureFileName(signatureFileName);
        pendingTask.setPhotoFileName(photoFileName);

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