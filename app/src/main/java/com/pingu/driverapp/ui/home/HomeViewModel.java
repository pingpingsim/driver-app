package com.pingu.driverapp.ui.home;

import android.content.Context;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pingu.driverapp.R;
import com.pingu.driverapp.data.DataManager;
import com.pingu.driverapp.data.model.DashboardSummary;
import com.pingu.driverapp.data.model.DashboardSummaryData;
import com.pingu.driverapp.data.model.account.Profile;
import com.pingu.driverapp.data.model.account.ProfileData;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class HomeViewModel extends ViewModel {
    private CompositeDisposable disposable;
    private DataManager.RemoteProvider remoteProvider;
    private DataManager.OfflineProvider offlineProvider;
    private Context context;

    private final MutableLiveData<String> zone = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalPendingPickupAddress = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalPendingArrivalAtHubParcel = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalPendingDeliveryParcel = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalAvailableAddress = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadSummaryDone = new MutableLiveData<>();

    @Inject
    public HomeViewModel(Context context, DataManager.RemoteProvider remoteProvider, DataManager.OfflineProvider offlineProvider) {
        this.disposable = new CompositeDisposable();
        this.context = context;
        this.remoteProvider = remoteProvider;
        this.offlineProvider = offlineProvider;

        loadRiderZone();
        loadOfflineParcelSummary();
        refreshParcelSummary();
        refreshRiderProfile();
    }

    private void loadOfflineParcelSummary() {
        totalAvailableAddress.setValue(offlineProvider.getAvailableAddress());
        totalPendingPickupAddress.setValue(offlineProvider.getPendingPickup());
        totalPendingDeliveryParcel.setValue(offlineProvider.getPendingDelivery());
        totalPendingArrivalAtHubParcel.setValue(offlineProvider.getTotalPendingArrivalAtHub());
    }

    public void refreshParcelSummary() {
        disposable.add(remoteProvider.getDashboardSummary()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<DashboardSummaryData>() {
                    @Override
                    public void onSuccess(DashboardSummaryData dashboardSummaryData) {
                        if (dashboardSummaryData != null && dashboardSummaryData.getData() != null) {
                            final DashboardSummary dashboardSummary = dashboardSummaryData.getData();
                            totalAvailableAddress.setValue(dashboardSummary.getTotalAvailableParcel());
                            totalPendingPickupAddress.setValue(dashboardSummary.getTotalPendingPickupParcel());
                            totalPendingDeliveryParcel.setValue(dashboardSummary.getTotalPendingDeliveryParcel());
                            totalPendingArrivalAtHubParcel.setValue(dashboardSummary.getTotalPendingArrivalAtHub());

                            saveOfflineParcelSummary(dashboardSummary);
                            loadSummaryDone.setValue(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }));
    }

    private void saveOfflineParcelSummary(final DashboardSummary dashboardSummary) {
        if (dashboardSummary != null) {
            offlineProvider.setAvailableAddress(dashboardSummary.getTotalAvailableParcel());
            offlineProvider.setPendingPickup(dashboardSummary.getTotalPendingPickupParcel());
            offlineProvider.setPendingDelivery(dashboardSummary.getTotalPendingDeliveryParcel());
            offlineProvider.setTotalPendingArrivalAtHub(dashboardSummary.getTotalPendingArrivalAtHub());
        }
    }

    private void loadRiderZone() {
        final Profile profile = offlineProvider.getProfile();
        if (profile != null && !TextUtils.isEmpty(profile.getZone())) {
            zone.setValue(profile.getZone());
        }
    }

    public MutableLiveData<Integer> getTotalAvailableAddress() {
        return totalAvailableAddress;
    }

    public MutableLiveData<String> getZone() {
        return zone;
    }

    public MutableLiveData<Integer> getTotalPendingPickupAddress() {
        return totalPendingPickupAddress;
    }

    public MutableLiveData<Integer> getTotalPendingArrivalAtHubParcel() {
        return totalPendingArrivalAtHubParcel;
    }

    public MutableLiveData<Integer> getTotalPendingDeliveryParcel() {
        return totalPendingDeliveryParcel;
    }

    public MutableLiveData<Boolean> getLoadSummaryDone() {
        return loadSummaryDone;
    }

    public void refreshRiderProfile() {
        disposable.add(remoteProvider.getProfile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ProfileData>() {
                    @Override
                    public void onSuccess(ProfileData profileData) {
                        if (profileData != null && profileData.getProfile() != null) {
                            if (profileData.getProfile() != null) {
                                offlineProvider.setProfile(profileData.getProfile());
                                zone.setValue(profileData.getProfile().getZone());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                }));
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