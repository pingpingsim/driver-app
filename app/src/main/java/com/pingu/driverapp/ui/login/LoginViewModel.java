package com.pingu.driverapp.ui.login;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pingu.driverapp.data.DataManager;
import com.pingu.driverapp.data.model.account.ProfileData;
import com.pingu.driverapp.util.APIHelper;
import com.pingu.driverapp.util.Constants;

import org.json.JSONObject;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.CacheControl;
import retrofit2.HttpException;

public class LoginViewModel extends ViewModel {
    private CompositeDisposable disposable;
    private DataManager.RemoteProvider remoteProvider;
    private DataManager.OfflineProvider offlineProvider;
    private Context context;

    private MutableLiveData<ProfileData> profileData = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<String> errorMsg = new MutableLiveData<>();


    @Inject
    public LoginViewModel(Context context, DataManager.RemoteProvider remoteProvider, DataManager.OfflineProvider offlineProvider) {
        this.context = context;
        this.remoteProvider = remoteProvider;
        this.offlineProvider = offlineProvider;
        disposable = new CompositeDisposable();
    }

    public void authenticateUser(final String username, final String password) {
        loading.setValue(true);

        disposable.add(remoteProvider.authenticateUser(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ProfileData>() {
                    @Override
                    public void onSuccess(ProfileData data) {
                        if (data.getStatus() == Constants.API_STATUS_SUCCESS && data.getProfile() != null) {
                            offlineProvider.setToken(data.getProfile().getToken());
                            profileData.setValue(data);
                        } else {
                            errorMsg.setValue(data.getMessage());
                        }
                        loading.setValue(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorMsg.setValue(APIHelper.handleExceptionMessage(e));
                        loading.setValue(false);
                    }
                }));
    }

    public MutableLiveData<ProfileData> getProfileResponse() {
        return profileData;
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    public MutableLiveData<String> getErrorMsg() {
        return errorMsg;
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