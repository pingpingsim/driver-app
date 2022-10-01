package com.pingu.driverapp.ui.home.pending.pickup;

import android.content.Context;
import android.text.style.TabStopSpan;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pingu.driverapp.BaseApplication;
import com.pingu.driverapp.R;
import com.pingu.driverapp.data.DataManager;
import com.pingu.driverapp.data.model.ApiResponse;
import com.pingu.driverapp.data.model.Parcel;
import com.pingu.driverapp.data.model.ParcelData;
import com.pingu.driverapp.data.model.ParcelProblemData;
import com.pingu.driverapp.data.model.Task;
import com.pingu.driverapp.data.model.local.PendingPickupOrder;
import com.pingu.driverapp.data.model.local.PendingTask;
import com.pingu.driverapp.util.APIHelper;
import com.pingu.driverapp.util.Constants;
import com.pingu.driverapp.util.DateTimeHelper;
import com.pingu.driverapp.util.Variables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class PendingPickupViewModel extends ViewModel {
    private DataManager.RemoteProvider remoteProvider;
    private DataManager.OfflineProvider offlineProvider;
    private CompositeDisposable disposable;
    private Context context;

    //load pending pickup list
    private final MutableLiveData<List<Task>> data = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    //load pending pickup list
    private final MutableLiveData<String> problematicParcelSubmitSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> problematicParcelSubmitError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> problematicParcelSubmitLoading = new MutableLiveData<>();

    public boolean isLoadingInProgress;
    private Map<String, Integer> pendingPickupOrderListRef = new HashMap<>();

    @Inject
    public PendingPickupViewModel(Context context, DataManager.RemoteProvider remoteProvider, DataManager.OfflineProvider offlineProvider) {
        this.disposable = new CompositeDisposable();
        this.remoteProvider = remoteProvider;
        this.offlineProvider = offlineProvider;
        this.context = context;
        getParcelProblemStatusList();
        loadPendingPickupOrderList();
        loadPendingPickupTasks(true);
    }

    public void savePendingPickupOrder(final List<Task> taskList) {
        if (!(taskList != null && taskList.size() > 0))
            return;

        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                offlineProvider.deleteAllPendingPickupOrder();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                insertPendingPickupOrderList(taskList);
            }

            @Override
            public void onError(Throwable e) {
            }
        });
    }

    private void insertPendingPickupOrderList(final List<Task> taskList) {
        if (!(taskList != null && taskList.size() > 0))
            return;

        PendingPickupOrder[] pendingPickupOrderList = new PendingPickupOrder[taskList.size()];
        for (int i = 0; i < taskList.size(); i++) {
            final PendingPickupOrder pendingPickupOrder = new PendingPickupOrder();
            pendingPickupOrder.setOrder(i);
            pendingPickupOrder.setSenderAddress(taskList.get(i).getAddress());
            pendingPickupOrderList[i] = pendingPickupOrder;
        }

        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                offlineProvider.insertPendingPickupOrder(pendingPickupOrderList);
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

    private void loadPendingPickupOrderList() {
        disposable.add(offlineProvider.getAllPendingPickupOrder()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<PendingPickupOrder>>() {
                    @Override
                    public void onSuccess(List<PendingPickupOrder> pendingPickupOrderList) {
                        if (pendingPickupOrderList != null && pendingPickupOrderList.size() > 0) {
                            for (final PendingPickupOrder pendingPickupOrder : pendingPickupOrderList) {
                                pendingPickupOrderListRef.put(pendingPickupOrder.getSenderAddress().toLowerCase(), pendingPickupOrder.getOrder());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                }));
    }

    private void sortTaskBySavedOrder(final List<Task> taskList) {
        try {
            if ((taskList != null && taskList.size() > 0) && pendingPickupOrderListRef.size() > 0) {
                for (final Task task : taskList) {
                    int order = -1;
                    if (pendingPickupOrderListRef.containsKey(task.getAddress().toLowerCase()))
                        order = pendingPickupOrderListRef.get(task.getAddress().toLowerCase());

                    task.setOrderId(order);
                }

                Collections.sort(taskList, new Comparator<Task>() {
                    @Override
                    public int compare(Task task1, Task task2) {
                        return task1.getOrderId() - task2.getOrderId();
                    }
                });
            }
        } catch (Exception ex) {
        }
    }

    public void loadPendingPickupTasks(final boolean showLoading) {
        if (showLoading)
            loading.setValue(true);
        isLoadingInProgress = true;
        try {
            disposable.add(remoteProvider.getPendingPickupTasks()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ParcelData>() {
                        @Override
                        public void onSuccess(ParcelData parcelData) {

                            if (parcelData != null && parcelData.getStatus() == Constants.API_STATUS_SUCCESS) {
                                final List<Task> taskList = convertToTaskList(parcelData.getData());
                                sortTaskBySavedOrder(taskList);

                                data.setValue(taskList);
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
                    task = new Task(parcel.getSenderName(), parcel.getSenderAddress(),
                            parcel.getSenderContactNumber(), parcelList,
                            parcel.getRider() != null ? parcel.getRider().getName() : null);
                    taskList.add(task);
                }
            }
        }
        return taskList;
    }

    public void submitProblematicParcel(final String parcelID, final int parcelProblemId) {
        problematicParcelSubmitLoading.setValue(true);
        try {
            disposable.add(remoteProvider.updateProblematicStatus(APIHelper.getParcelIdMultipartData(parcelID),
                    APIHelper.getParcelProblemIdMultipartData(parcelProblemId),
                    null,
                    null)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ApiResponse>() {
                        @Override
                        public void onSuccess(ApiResponse apiResponse) {

                            if (apiResponse != null && apiResponse.getStatus() == Constants.API_STATUS_SUCCESS) {
                                problematicParcelSubmitSuccess.setValue(parcelID);
                            } else {
                            }

                            problematicParcelSubmitLoading.setValue(false);
                        }

                        @Override
                        public void onError(Throwable e) {
                            problematicParcelSubmitError.setValue(parcelID);
                            problematicParcelSubmitLoading.setValue(false);
                            if (!Variables.isNetworkConnected) {
                                createPendingJobOnFailure(parcelID, parcelProblemId, null);
                            }
                        }
                    }));

        } catch (Exception ex) {
            problematicParcelSubmitError.setValue(parcelID);
            problematicParcelSubmitLoading.setValue(false);
        }
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


    public MutableLiveData<String> getProblematicParcelSubmitSuccess() {
        return problematicParcelSubmitSuccess;
    }

    public MutableLiveData<String> getProblematicParcelSubmitError() {
        return problematicParcelSubmitError;
    }

    public MutableLiveData<Boolean> getProblematicParcelSubmitLoading() {
        return problematicParcelSubmitLoading;
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