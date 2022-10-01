package com.pingu.driverapp.ui.notification;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pingu.driverapp.data.DataManager;
import com.pingu.driverapp.data.model.Announcement;
import com.pingu.driverapp.data.model.AnnouncementData;
import com.pingu.driverapp.data.model.ApiResponse;
import com.pingu.driverapp.util.APIHelper;
import com.pingu.driverapp.util.Constants;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class NotificationViewModel extends ViewModel {
    private DataManager.RemoteProvider remoteProvider;
    private CompositeDisposable disposable;
    private Context context;

    private final MutableLiveData<List<Announcement>> data = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public boolean isLoadingInProgress;

    @Inject
    public NotificationViewModel(Context context, DataManager.RemoteProvider remoteProvider) {
        this.disposable = new CompositeDisposable();
        this.remoteProvider = remoteProvider;
        this.context = context;
        loadNotification(true);
    }

    public void loadNotification(final boolean showLoading) {
        if (showLoading)
            loading.setValue(true);
        isLoadingInProgress = true;
        try {
            disposable.add(remoteProvider.getAnnouncements()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<AnnouncementData>() {
                        @Override
                        public void onSuccess(AnnouncementData announcementData) {

                            if (announcementData != null && announcementData.getStatus() == Constants.API_STATUS_SUCCESS) {
                                data.setValue(announcementData.getData());
                                markAllLoadedNotificationRead(announcementData.getData());
                            } else {
                                error.setValue(announcementData.getMessage());
                            }
                            isLoadingInProgress = false;
                            if (showLoading)
                                loading.setValue(false);
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
            error.setValue(ex.getMessage());
            isLoadingInProgress = false;
            if (showLoading)
                loading.setValue(false);
        }
    }

    private void markAllLoadedNotificationRead(List<Announcement> allAnnouncements) {
        if (allAnnouncements != null && allAnnouncements.size() > 0) {
            for (Announcement announcement : allAnnouncements) {
                markNotificationRead(announcement.getId());
            }
        }
    }

    public void markNotificationRead(final int announcementId) {
        try {
            disposable.add(remoteProvider.markAnnouncementRead(announcementId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ApiResponse>() {
                        @Override
                        public void onSuccess(ApiResponse apiResponse) {

                        }

                        @Override
                        public void onError(Throwable e) {
                        }
                    }));

        } catch (Exception ex) {
        }
    }

    public MutableLiveData<List<Announcement>> getData() {
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