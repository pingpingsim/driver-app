package com.pingu.driverapp.ui.profile.changepassword;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pingu.driverapp.data.DataManager;
import com.pingu.driverapp.data.model.ApiResponse;
import com.pingu.driverapp.util.APIHelper;
import com.pingu.driverapp.util.Constants;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ChangePasswordViewModel extends ViewModel {
    private CompositeDisposable disposable;
    private DataManager.RemoteProvider remoteProvider;
    private Context context;

    private MutableLiveData<ApiResponse> changePwdResponse = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<String> errorMsg = new MutableLiveData<>();


    @Inject
    public ChangePasswordViewModel(Context context, DataManager.RemoteProvider remoteProvider) {
        this.context = context;
        this.remoteProvider = remoteProvider;
        disposable = new CompositeDisposable();
    }

    public void changePassword(final String currentPassword, final String newPassword) {
        loading.setValue(true);

        disposable.add(remoteProvider.changePassword(currentPassword, newPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ApiResponse>() {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {
                        if (apiResponse.getStatus() == Constants.API_STATUS_SUCCESS) {
                            changePwdResponse.setValue(apiResponse);
                        } else {
                            errorMsg.setValue(apiResponse.getMessage());
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

    public MutableLiveData<ApiResponse> getChangePwdResponse() {
        return changePwdResponse;
    }

    public void setChangePwdResponse(MutableLiveData<ApiResponse> changePwdResponse) {
        this.changePwdResponse = changePwdResponse;
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    public void setLoading(MutableLiveData<Boolean> loading) {
        this.loading = loading;
    }

    public MutableLiveData<String> getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(MutableLiveData<String> errorMsg) {
        this.errorMsg = errorMsg;
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