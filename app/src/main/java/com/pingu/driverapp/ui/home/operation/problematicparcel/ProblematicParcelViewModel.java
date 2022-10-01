package com.pingu.driverapp.ui.home.operation.problematicparcel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pingu.driverapp.BaseApplication;
import com.pingu.driverapp.R;
import com.pingu.driverapp.data.DataManager;
import com.pingu.driverapp.data.model.ApiResponse;
import com.pingu.driverapp.data.model.ParcelProblemData;
import com.pingu.driverapp.data.model.local.PendingTask;
import com.pingu.driverapp.util.APIHelper;
import com.pingu.driverapp.util.Constants;
import com.pingu.driverapp.util.DateTimeHelper;
import com.pingu.driverapp.util.FileHelper;
import com.pingu.driverapp.util.Variables;

import java.io.File;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ProblematicParcelViewModel extends ViewModel {
    private DataManager.RemoteProvider remoteProvider;
    private DataManager.OfflineProvider offlineProvider;
    private CompositeDisposable disposable;
    private Context context;

    private final MutableLiveData<Boolean> data = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isRetryTriggered = new MutableLiveData<>();

    @Inject
    public ProblematicParcelViewModel(Context context, DataManager.RemoteProvider remoteProvider,
                                      DataManager.OfflineProvider offlineProvider) {
        this.disposable = new CompositeDisposable();
        this.remoteProvider = remoteProvider;
        this.context = context;
        this.offlineProvider = offlineProvider;
        getParcelProblemStatusList();
    }

    public void submitProblematicParcel(final String parcelID, final int parcelProblemId, final File parcelPhoto) {
        loading.setValue(true);
        try {
            disposable.add(remoteProvider.updateProblematicStatus(APIHelper.getParcelIdMultipartData(parcelID),
                    APIHelper.getParcelProblemIdMultipartData(parcelProblemId),
                    null,
                    APIHelper.getPhotoMultipartData(parcelPhoto))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ApiResponse>() {
                        @Override
                        public void onSuccess(ApiResponse apiResponse) {

                            if (apiResponse != null && apiResponse.getStatus() == Constants.API_STATUS_SUCCESS) {
                                data.setValue(true);
                                FileHelper.deleteFile(parcelPhoto);
                            } else {
                                error.setValue(apiResponse.getMessage());
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
                                createPendingJobOnFailure(parcelID, parcelProblemId,
                                        parcelPhoto != null ? parcelPhoto.getName() : "");
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

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null) {
            disposable.clear();
            disposable = null;
        }
    }

    public void createPendingJobOnFailure(final String parcelId, final int problemStatusId, final String photoFileName) {
        PendingTask pendingTask = new PendingTask();
        pendingTask.setParcelId(parcelId);
        pendingTask.setProblematicStatusId(problemStatusId);
        pendingTask.setPhotoFileName(photoFileName);
        pendingTask.setStatusDateTime(DateTimeHelper.getNowDateTime());
        pendingTask.setActionType(PendingTask.ACTION_TYPE_OPERATION_PROBLEMATIC_PARCEL);

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

    public void getParcelProblemStatusList() {
        disposable.add(remoteProvider.getParcelProblemList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ParcelProblemData>() {
                    @Override
                    public void onSuccess(ParcelProblemData parcelProblemData) {
                        if (parcelProblemData != null && parcelProblemData.getData() != null) {
                            ((BaseApplication) context.getApplicationContext()).parcelProblemReference =
                                    parcelProblemData.getData();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                }));
    }
}