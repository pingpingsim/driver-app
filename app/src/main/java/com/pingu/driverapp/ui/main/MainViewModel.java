package com.pingu.driverapp.ui.main;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pingu.driverapp.BaseApplication;
import com.pingu.driverapp.R;
import com.pingu.driverapp.data.DataManager;
import com.pingu.driverapp.data.model.ParcelProblemData;
import com.pingu.driverapp.data.model.account.Profile;
import com.pingu.driverapp.data.model.account.ProfileData;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel {
    private DataManager.RemoteProvider remoteProvider;
    private DataManager.OfflineProvider offlineProvider;
    private CompositeDisposable disposable;
    private Context context;

    private MutableLiveData<String> greeting = new MutableLiveData<>();
    private MutableLiveData<String> accountNo = new MutableLiveData<>();

    @Inject
    public MainViewModel(Context context, DataManager.RemoteProvider remoteProvider, DataManager.OfflineProvider offlineProvider) {
        this.context = context;
        this.remoteProvider = remoteProvider;
        this.offlineProvider = offlineProvider;
        this.disposable = new CompositeDisposable();

        loadOfflineProfile();
        refreshRiderProfile();
        getParcelProblemStatusList();
    }

    private void loadOfflineProfile() {
        final Profile profile = offlineProvider.getProfile();
        if (profile != null) {
            greeting.setValue(String.format(context.getString(R.string.home_greeting),
                    profile.getName()));
            accountNo.setValue(profile.getLoginId());
        }
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
                                greeting.setValue(String.format(context.getString(R.string.home_greeting),
                                        profileData.getProfile().getName()));
                                accountNo.setValue(profileData.getProfile().getLoginId());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                }));
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

    public LiveData<String> getGreeting() {
        return greeting;
    }

    public LiveData<String> getAccountNo() {
        return accountNo;
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