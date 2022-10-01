package com.pingu.driverapp.ui.home.pending.delivery;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pingu.driverapp.R;
import com.pingu.driverapp.data.DataManager;
import com.pingu.driverapp.data.model.OutForDeliveryParcelData;
import com.pingu.driverapp.data.model.Parcel;
import com.pingu.driverapp.data.model.Task;
import com.pingu.driverapp.data.model.local.PendingDeliveryOrder;
import com.pingu.driverapp.data.model.local.PendingPickupOrder;
import com.pingu.driverapp.util.APIHelper;
import com.pingu.driverapp.util.Constants;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

public class PendingDeliveryViewModel extends ViewModel {
    private CompositeDisposable disposable;
    private Context context;
    private DataManager.RemoteProvider remoteProvider;
    private DataManager.OfflineProvider offlineProvider;

    private final MutableLiveData<List<Parcel>> data = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public boolean isLoadingInProgress;
    private Map<String, Integer> pendingDeliveryOrderListRef = new HashMap<>();

    @Inject
    public PendingDeliveryViewModel(final Context context, final DataManager.RemoteProvider remoteProvider,
                                    final DataManager.OfflineProvider offlineProvider) {
        this.context = context;
        this.disposable = new CompositeDisposable();
        this.remoteProvider = remoteProvider;
        this.offlineProvider = offlineProvider;
        loadPendingDeliveryOrderList();
        loadPendingDeliveryTasks(true);
    }

    public void loadPendingDeliveryTasks(final boolean showLoading) {
        if (showLoading)
            loading.setValue(true);
        isLoadingInProgress = true;

        try {
            disposable.add(remoteProvider.getPendingDeliveryTasks()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<OutForDeliveryParcelData>() {
                        @Override
                        public void onSuccess(OutForDeliveryParcelData outForDeliveryParcelData) {

                            if (outForDeliveryParcelData != null && outForDeliveryParcelData.getStatus() == Constants.API_STATUS_SUCCESS) {
                                sortParcelListBySavedOrder(outForDeliveryParcelData.getData());
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

    public void savePendingDeliveryOrder(final List<Parcel> parcelList) {
        if (!(parcelList != null && parcelList.size() > 0))
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
                insertPendingDeliveryOrderList(parcelList);
            }

            @Override
            public void onError(Throwable e) {
            }
        });
    }

    private void insertPendingDeliveryOrderList(final List<Parcel> parcelList) {
        if (!(parcelList != null && parcelList.size() > 0))
            return;

        PendingDeliveryOrder[] pendingDeliveryOrderList = new PendingDeliveryOrder[parcelList.size()];
        for (int i = 0; i < parcelList.size(); i++) {
            final PendingDeliveryOrder pendingDeliveryOrder = new PendingDeliveryOrder();
            pendingDeliveryOrder.setOrder(i);
            pendingDeliveryOrder.setParcelId(parcelList.get(i).getParcelId());
            pendingDeliveryOrderList[i] = pendingDeliveryOrder;
        }

        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                offlineProvider.insertPendingDeliveryOrder(pendingDeliveryOrderList);
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

    private void loadPendingDeliveryOrderList() {
        disposable.add(offlineProvider.getAllPendingDeliveryOrder()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<PendingDeliveryOrder>>() {
                    @Override
                    public void onSuccess(List<PendingDeliveryOrder> pendingDeliveryOrderList) {
                        if (pendingDeliveryOrderList != null && pendingDeliveryOrderList.size() > 0) {
                            for (final PendingDeliveryOrder pendingDeliveryOrder : pendingDeliveryOrderList) {
                                pendingDeliveryOrderListRef.put(pendingDeliveryOrder.getParcelId().toLowerCase(), pendingDeliveryOrder.getOrder());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                }));
    }

    private void sortParcelListBySavedOrder(final List<Parcel> parcelList) {
        try {
            if ((parcelList != null && parcelList.size() > 0) && pendingDeliveryOrderListRef.size() > 0) {
                for (final Parcel parcel : parcelList) {
                    int order = -1;
                    if (pendingDeliveryOrderListRef.containsKey(parcel.getParcelId().toLowerCase()))
                        order = pendingDeliveryOrderListRef.get(parcel.getParcelId().toLowerCase());

                    parcel.setOrderId(order);
                }

                Collections.sort(parcelList, new Comparator<Parcel>() {
                    @Override
                    public int compare(Parcel parcel1, Parcel parcel2) {
                        return parcel1.getOrderId() - parcel2.getOrderId();
                    }
                });
            }
        } catch (Exception ex) {
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