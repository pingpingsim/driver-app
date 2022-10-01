package com.pingu.driverapp.ui.orders;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pingu.driverapp.data.DataManager;
import com.pingu.driverapp.data.model.OrderSummary;
import com.pingu.driverapp.data.model.OrderSummaryData;
import com.pingu.driverapp.util.Constants;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class OrdersViewModel extends ViewModel {
    private DataManager.RemoteProvider remoteProvider;
    private DataManager.OfflineProvider offlineProvider;
    private CompositeDisposable disposable;
    private Context context;

    private final MutableLiveData<Integer> totalTodayPickupParcel = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalTodayDeliveryParcel = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalTodayCompletedParcel = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalTodayProblematicParcel = new MutableLiveData<>();

    private final MutableLiveData<Integer> totalHistoryPickupParcel = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalHistoryDeliveryParcel = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalHistoryCompletedParcel = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalHistoryProblematicParcel = new MutableLiveData<>();

    private final MutableLiveData<Boolean> loadSummaryDone = new MutableLiveData<>();

    @Inject
    public OrdersViewModel(final Context context, final DataManager.RemoteProvider remoteProvider,
                           final DataManager.OfflineProvider offlineProvider) {
        this.context = context;
        this.disposable = new CompositeDisposable();
        this.context = context;
        this.remoteProvider = remoteProvider;
        this.offlineProvider = offlineProvider;

        loadOfflineOrderSummary();
        loadOrderSummaryData();
    }

    private void loadOfflineOrderSummary() {
        totalTodayPickupParcel.setValue(offlineProvider.getTodayPickup());
        totalTodayDeliveryParcel.setValue(offlineProvider.getTodayDelivery());
        totalTodayCompletedParcel.setValue(offlineProvider.getTodayCompleted());
        totalTodayProblematicParcel.setValue(offlineProvider.getTodayProblematic());

        totalHistoryPickupParcel.setValue(offlineProvider.getHistoryPickup());
        totalHistoryDeliveryParcel.setValue(offlineProvider.getHistoryDelivery());
        totalHistoryCompletedParcel.setValue(offlineProvider.getHistoryCompleted());
        totalHistoryProblematicParcel.setValue(offlineProvider.getHistoryProblematic());
    }

    public void loadOrderSummaryData() {
        disposable.add(remoteProvider.getOrdersSummary()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<OrderSummaryData>() {
                    @Override
                    public void onSuccess(OrderSummaryData orderSummaryData) {
                        if (orderSummaryData.getStatus() == Constants.API_STATUS_SUCCESS &&
                                orderSummaryData != null &&
                                orderSummaryData.getData() != null) {
                            if (orderSummaryData.getData().getOrderTotalToday() != null) {
                                totalTodayPickupParcel.setValue(orderSummaryData.getData().getOrderTotalToday().getTotalPickupParcel());
                                totalTodayDeliveryParcel.setValue(orderSummaryData.getData().getOrderTotalToday().getTotalDeliveryParcel());
                                totalTodayCompletedParcel.setValue(orderSummaryData.getData().getOrderTotalToday().getTotalCompletedParcel());
                                totalTodayProblematicParcel.setValue(orderSummaryData.getData().getOrderTotalToday().getTotalProblematicParcel());
                            }

                            if (orderSummaryData.getData().getOrderTotalHistory() != null) {
                                totalHistoryPickupParcel.setValue(orderSummaryData.getData().getOrderTotalHistory().getTotalPickupParcel());
                                totalHistoryDeliveryParcel.setValue(orderSummaryData.getData().getOrderTotalHistory().getTotalDeliveryParcel());
                                totalHistoryCompletedParcel.setValue(orderSummaryData.getData().getOrderTotalHistory().getTotalCompletedParcel());
                                totalHistoryProblematicParcel.setValue(orderSummaryData.getData().getOrderTotalHistory().getTotalProblematicParcel());
                            }

                            saveOfflineOrderSummary(orderSummaryData.getData());
                            loadSummaryDone.setValue(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                }));
    }

    private void saveOfflineOrderSummary(final OrderSummary orderSummary) {
        if (orderSummary.getOrderTotalToday() != null) {
            offlineProvider.setTodayPickup(orderSummary.getOrderTotalToday().getTotalPickupParcel());
            offlineProvider.setTodayDelivery(orderSummary.getOrderTotalToday().getTotalDeliveryParcel());
            offlineProvider.setTodayCompleted(orderSummary.getOrderTotalToday().getTotalCompletedParcel());
            offlineProvider.setTodayProblematic(orderSummary.getOrderTotalToday().getTotalProblematicParcel());
        }

        if (orderSummary.getOrderTotalHistory() != null) {
            offlineProvider.setHistoryPickup(orderSummary.getOrderTotalHistory().getTotalPickupParcel());
            offlineProvider.setHistoryDelivery(orderSummary.getOrderTotalHistory().getTotalDeliveryParcel());
            offlineProvider.setHistoryCompleted(orderSummary.getOrderTotalHistory().getTotalCompletedParcel());
            offlineProvider.setHistoryProblematic(orderSummary.getOrderTotalHistory().getTotalProblematicParcel());
        }
    }

    public MutableLiveData<Integer> getTotalTodayPickupParcel() {
        return totalTodayPickupParcel;
    }

    public MutableLiveData<Integer> getTotalTodayDeliveryParcel() {
        return totalTodayDeliveryParcel;
    }

    public MutableLiveData<Integer> getTotalTodayCompletedParcel() {
        return totalTodayCompletedParcel;
    }

    public MutableLiveData<Integer> getTotalTodayProblematicParcel() {
        return totalTodayProblematicParcel;
    }

    public MutableLiveData<Integer> getTotalHistoryPickupParcel() {
        return totalHistoryPickupParcel;
    }

    public MutableLiveData<Integer> getTotalHistoryDeliveryParcel() {
        return totalHistoryDeliveryParcel;
    }

    public MutableLiveData<Integer> getTotalHistoryCompletedParcel() {
        return totalHistoryCompletedParcel;
    }

    public MutableLiveData<Integer> getTotalHistoryProblematicParcel() {
        return totalHistoryProblematicParcel;
    }

    public MutableLiveData<Boolean> getLoadSummaryDone() {
        return loadSummaryDone;
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