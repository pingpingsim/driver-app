package com.pingu.driverapp.ui.home.task.my_tasks;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pingu.driverapp.R;
import com.pingu.driverapp.data.DataManager;
import com.pingu.driverapp.data.model.ActionStatus;
import com.pingu.driverapp.data.model.ActionStatusInfo;
import com.pingu.driverapp.data.model.Parcel;
import com.pingu.driverapp.data.model.ParcelData;
import com.pingu.driverapp.data.model.Task;
import com.pingu.driverapp.data.model.local.PendingTask;
import com.pingu.driverapp.util.APIHelper;
import com.pingu.driverapp.util.Constants;
import com.pingu.driverapp.util.DateTimeHelper;
import com.pingu.driverapp.util.Variables;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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

public class PickupMyTasksViewModel extends ViewModel {
    private DataManager.RemoteProvider remoteProvider;
    private DataManager.OfflineProvider offlineProvider;
    private CompositeDisposable disposable;
    private Context context;

    // task list
    private final MutableLiveData<List<Task>> data = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    // accept task
    private final MutableLiveData<Map<String, ActionStatus>> removeTaskSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> removeTaskError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> removeTaskLoading = new MutableLiveData<>();

    public boolean hasLoadedOnce;
    public boolean isLoadingInProgress;

    @Inject
    public PickupMyTasksViewModel(Context context, DataManager.RemoteProvider remoteProvider, DataManager.OfflineProvider offlineProvider) {
        this.context = context;
        this.disposable = new CompositeDisposable();
        this.remoteProvider = remoteProvider;
        this.offlineProvider = offlineProvider;
    }

    public void loadMyTasks(final boolean showLoading) {
        if (showLoading)
            loading.setValue(true);

        try {
            if (this.disposable == null)
                this.disposable = new CompositeDisposable();

            hasLoadedOnce = true;
            isLoadingInProgress = true;
            disposable.add(remoteProvider.getMyTasks()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ParcelData>() {
                        @Override
                        public void onSuccess(ParcelData parcelData) {

                            if (parcelData != null && parcelData.getStatus() == Constants.API_STATUS_SUCCESS) {
                                data.setValue(convertToTaskList(parcelData.getData()));
                            } else {
                                error.setValue(parcelData.getMessage());
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

    public void removeTask(final String[] parcelIds) {
        removeTaskLoading.setValue(true);
        try {
            disposable.add(remoteProvider.removeTask(parcelIds)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ActionStatusInfo>() {
                        @Override
                        public void onSuccess(ActionStatusInfo actionStatusInfo) {

                            if (actionStatusInfo != null && actionStatusInfo.getStatus() == Constants.API_STATUS_SUCCESS) {
                                removeTaskSuccess.setValue(actionStatusInfo.getActionStatusData());
                            } else {
                                removeTaskError.setValue(actionStatusInfo.getMessage());
                            }
                            removeTaskLoading.setValue(false);
                        }

                        @Override
                        public void onError(Throwable e) {
                            removeTaskError.setValue(APIHelper.handleExceptionMessage(e));
                            removeTaskLoading.setValue(false);
                            if (!Variables.isNetworkConnected) {
                                handlePendingTask(parcelIds);
                            }
                        }
                    }));

        } catch (Exception ex) {
            removeTaskError.setValue(context.getString(R.string.error_unexpected));
            removeTaskLoading.setValue(false);
        }
    }

    /**
     * Convert the map to list structure for easy handling in frontend
     *
     * @return
     */
    private List<Task> convertToTaskList(LinkedHashMap<String, List<Parcel>> taskMap) {
        List<Task> taskList = new ArrayList<>();
        Task task = null;
        if (taskMap != null && taskMap.size() > 0) {
            for (Map.Entry<String, List<Parcel>> taskEntry : taskMap.entrySet()) {
                final String address = taskEntry.getKey();
                final List<Parcel> parcelList = taskEntry.getValue();
                if (parcelList != null && parcelList.size() > 0) {
                    final Parcel parcel = parcelList.get(0);
                    task = new Task(parcel.getSenderName(), parcel.getSenderAddress(),
                            parcel.getSenderContactNumber(), parcelList,
                            parcel.getRider() != null ? parcel.getRider().getName() : null);
                    taskList.add(task);
                }
            }
        }
        return taskList;
    }

    public MutableLiveData<Map<String, ActionStatus>> getRemoveTaskSuccess() {
        return removeTaskSuccess;
    }

    public MutableLiveData<String> getRemoveTaskError() {
        return removeTaskError;
    }

    public MutableLiveData<Boolean> getRemoveTaskLoading() {
        return removeTaskLoading;
    }

    public MutableLiveData<List<Task>> getData() {
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
        cleanUp();
    }

    public void cleanUp() {
        hasLoadedOnce = false;
        if (disposable != null) {
            disposable.clear();
            disposable = null;
        }
    }

    private void handlePendingTask(String[] parcelIdList) {
        if (parcelIdList != null && parcelIdList.length > 0) {
            for (final String parcelId : parcelIdList) {
                createPendingJobOnFailure(parcelId);
            }
        }
    }

    public void createPendingJobOnFailure(final String parcelId) {
        PendingTask pendingTask = new PendingTask();
        pendingTask.setParcelId(parcelId);
        pendingTask.setStatusDateTime(DateTimeHelper.getNowDateTime());
        pendingTask.setActionType(PendingTask.ACTION_TYPE_REMOVE_TASK);

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