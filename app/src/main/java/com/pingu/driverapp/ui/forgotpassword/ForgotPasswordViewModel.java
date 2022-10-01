package com.pingu.driverapp.ui.forgotpassword;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pingu.driverapp.data.DataManager;
import com.pingu.driverapp.data.model.ApiResponse;
import com.pingu.driverapp.util.APIHelper;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ForgotPasswordViewModel extends ViewModel {
    private DataManager.RemoteProvider remoteProvider;
    private CompositeDisposable disposable;
    private Context context;

    private MutableLiveData<ApiResponse> resetPwdResponse = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<String> errorMsg = new MutableLiveData<>();

    @Inject
    public ForgotPasswordViewModel(Context context, DataManager.RemoteProvider remoteProvider) {
        this.context = context;
        this.remoteProvider = remoteProvider;
        disposable = new CompositeDisposable();
    }

    public void resetPassword(final String email) {
        loading.setValue(true);

        disposable.add(remoteProvider.resetPassword(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ApiResponse>() {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {
                        resetPwdResponse.setValue(apiResponse);
                        loading.setValue(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorMsg.setValue(APIHelper.handleExceptionMessage(e));
                        loading.setValue(false);
                    }
                }));
    }

    public MutableLiveData<ApiResponse> getResetPwdResponse() {
        return resetPwdResponse;
    }

    public void setResetPwdResponse(MutableLiveData<ApiResponse> resetPwdResponse) {
        this.resetPwdResponse = resetPwdResponse;
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