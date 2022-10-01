package com.pingu.driverapp.ui.home.task.all_tasks;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pingu.driverapp.R;
import com.pingu.driverapp.data.DataManager;
import com.pingu.driverapp.data.model.Parcel;
import com.pingu.driverapp.data.model.ParcelData;
import com.pingu.driverapp.data.model.Task;
import com.pingu.driverapp.util.APIHelper;
import com.pingu.driverapp.util.Constants;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class PickupAllTasksViewModel extends ViewModel {
    private CompositeDisposable disposable;
    private Context context;
    private DataManager.RemoteProvider remoteProvider;

    private final MutableLiveData<List<Task>> data = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public boolean hasLoadedOnce;
    public boolean isLoadingInProgress;

    @Inject
    public PickupAllTasksViewModel(Context context, DataManager.RemoteProvider remoteProvider) {
        this.context = context;
        this.disposable = new CompositeDisposable();
        this.remoteProvider = remoteProvider;
    }

    public void loadAllTasks(final boolean showLoading) {
        if (showLoading)
            loading.setValue(true);
        try {
            if (this.disposable == null)
                this.disposable = new CompositeDisposable();

            hasLoadedOnce = true;
            isLoadingInProgress = true;
            disposable.add(remoteProvider.getAllTasks()
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

    private List<Task> convertToTaskList(LinkedHashMap<String, List<Parcel>> taskMap) {
        List<Task> taskList = new ArrayList<>();
        Task task = null;
        if (taskMap != null && taskMap.size() > 0) {
            for (Map.Entry<String, List<Parcel>> taskEntry : taskMap.entrySet()) {
                final String address = taskEntry.getKey();
                final List<Parcel> parcelList = taskEntry.getValue();
                if (parcelList != null && parcelList.size() > 0) {
                    final Parcel parcel = parcelList.get(0);
                    task = new Task(parcel.getSenderName(), parcel.getSenderAddress(), parcel.getSenderContactNumber(), parcelList,
                            parcel.getRider() != null ? parcel.getRider().getName() : null);
                    taskList.add(task);
                }
            }
        }
        return taskList;
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
}