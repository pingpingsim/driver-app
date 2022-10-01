package com.pingu.driverapp.worker;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.pingu.driverapp.BuildConfig;
import com.pingu.driverapp.data.local.db.LocalRoomDatabase;
import com.pingu.driverapp.data.local.db.dao.PendingTaskDao;
import com.pingu.driverapp.data.model.ActionStatusInfo;
import com.pingu.driverapp.data.model.ApiResponse;
import com.pingu.driverapp.data.model.UpdateParcelStatusResponse;
import com.pingu.driverapp.data.model.local.PendingTask;
import com.pingu.driverapp.data.remote.NonCachedApiService;
import com.pingu.driverapp.util.APIHelper;
import com.pingu.driverapp.util.Constants;
import com.pingu.driverapp.util.FileHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetryWorker extends Worker {
    private static final String TAG = RetryWorker.class.getSimpleName();
    private static int REQUEST_TIMEOUT = 10;
    private Context context;
    private Retrofit retrofit;
    private PendingTaskDao pendingTaskDao;
    private SharedPreferences sharedPreferences;

    public RetryWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(okHttpClient)
                .build();

        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        pendingTaskDao = LocalRoomDatabase.getDatabase(context).pendingTaskDao();
    }

    @NonNull
    @Override
    public Result doWork() {
        retryPendingTask();
        return Result.Success.success();
    }

    public void retryPendingTask() {
        final CompositeDisposable disposable = new CompositeDisposable();

        if (disposable != null) {
            disposable.add(pendingTaskDao.getAllTask()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<List<PendingTask>>() {
                        @Override
                        public void onSuccess(List<PendingTask> pendingTaskList) {
                            if (pendingTaskList != null && pendingTaskList.size() > 0) {
                                for (PendingTask pendingTask : pendingTaskList) {
                                    if (pendingTask.getActionType() == PendingTask.ACTION_TYPE_ACCEPT_TASK) {
                                        executeAcceptTaskRequest(pendingTask);

                                    } else if (pendingTask.getActionType() == PendingTask.ACTION_TYPE_REMOVE_TASK) {
                                        executeRemoveTaskRequest(pendingTask);

                                    } else if (pendingTask.getActionType() == PendingTask.ACTION_TYPE_OPERATION_PICKUP) {
                                        executeParcelPickupRequest(pendingTask);

                                    } else if (pendingTask.getActionType() == PendingTask.ACTION_TYPE_OPERATION_OUT_FOR_DELIVERY) {
                                        executeParcelOutForDeliveryRequest(pendingTask);

                                    } else if (pendingTask.getActionType() == PendingTask.ACTION_TYPE_OPERATION_COMPLETED) {
                                        executeCParcelDeliveryCompletedRequest(pendingTask);

                                    } else if (pendingTask.getActionType() == PendingTask.ACTION_TYPE_OPERATION_PROBLEMATIC_PARCEL) {
                                        executeSubmitProblematicParcelRequest(pendingTask);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                        }
                    }));
        }
    }

    private void executeAcceptTaskRequest(final PendingTask pendingTask) {
        try {
            final CompositeDisposable disposable = new CompositeDisposable();
            final NonCachedApiService apiService = retrofit.create(NonCachedApiService.class);

            disposable.add(apiService.acceptTask(getHeaderToken(), pendingTask.getParcelId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ActionStatusInfo>() {
                        @Override
                        public void onSuccess(ActionStatusInfo apiResponse) {

                            if (apiResponse != null && apiResponse.getStatus() == Constants.API_STATUS_SUCCESS) {

                            } else {
                                //api error, remove from pending task?
                            }
                            removePendingTask(pendingTask.getParcelId());
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    }));

        } catch (Exception ex) {

        }
    }

    private void executeRemoveTaskRequest(final PendingTask pendingTask) {
        try {
            final CompositeDisposable disposable = new CompositeDisposable();
            final NonCachedApiService apiService = retrofit.create(NonCachedApiService.class);

            disposable.add(apiService.removeTask(getHeaderToken(), pendingTask.getParcelId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ActionStatusInfo>() {
                        @Override
                        public void onSuccess(ActionStatusInfo apiResponse) {

                            if (apiResponse != null && apiResponse.getStatus() == Constants.API_STATUS_SUCCESS) {
                            } else {
                                //api error, remove from pending task?
                            }
                            removePendingTask(pendingTask.getParcelId());
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    }));

        } catch (Exception ex) {

        }
    }

    private void executeParcelPickupRequest(final PendingTask pendingTask) {
        try {
            final CompositeDisposable disposable = new CompositeDisposable();
            final NonCachedApiService apiService = retrofit.create(NonCachedApiService.class);
            final ArrayList<String> parcelIdList = new ArrayList<>();
            parcelIdList.add(pendingTask.getParcelId());

            disposable.add(apiService.updateParcelStatus(getHeaderToken(),
                    APIHelper.getParcelIdsMultipartDataFromIdList(parcelIdList),
                    APIHelper.getStatusMultipartData(Constants.PARCEL_STATUS_PICKUP),
                    null, null, null, null,
                    APIHelper.getStatusDateTimeMultipartData(pendingTask.getStatusDateTime()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<UpdateParcelStatusResponse>() {
                        @Override
                        public void onSuccess(UpdateParcelStatusResponse apiResponse) {

                            if (apiResponse != null && apiResponse.getStatus() == Constants.API_STATUS_SUCCESS) {
                            } else {
                            }
                            removePendingTask(pendingTask.getParcelId());
                        }

                        @Override
                        public void onError(Throwable e) {
                        }
                    }));

        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    private void executeParcelOutForDeliveryRequest(final PendingTask pendingTask) {
        try {
            final CompositeDisposable disposable = new CompositeDisposable();
            final NonCachedApiService apiService = retrofit.create(NonCachedApiService.class);
            final ArrayList<String> parcelIdList = new ArrayList<>();
            parcelIdList.add(pendingTask.getParcelId());

            disposable.add(apiService.updateParcelStatus(getHeaderToken(),
                    APIHelper.getParcelIdsMultipartDataFromIdList(parcelIdList),
                    APIHelper.getStatusMultipartData(Constants.PARCEL_STATUS_OUT_FOR_DELIVERY),
                    null, null, null, null,
                    APIHelper.getStatusDateTimeMultipartData(pendingTask.getStatusDateTime()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<UpdateParcelStatusResponse>() {
                        @Override
                        public void onSuccess(UpdateParcelStatusResponse apiResponse) {

                            if (apiResponse != null && apiResponse.getStatus() == Constants.API_STATUS_SUCCESS) {
                            } else {
                            }
                            removePendingTask(pendingTask.getParcelId());
                        }

                        @Override
                        public void onError(Throwable e) {
                            removePendingTask(pendingTask.getParcelId());
                        }
                    }));

        } catch (Exception ex) {

        }
    }

    private void executeCParcelDeliveryCompletedRequest(final PendingTask pendingTask) {
        try {
            final CompositeDisposable disposable = new CompositeDisposable();
            final NonCachedApiService apiService = retrofit.create(NonCachedApiService.class);
            final ArrayList<String> parcelIdList = new ArrayList<>();
            parcelIdList.add(pendingTask.getParcelId());
            final File parcelSignatureFile = FileHelper.getFileFromName(pendingTask.getSignatureFileName(), context);
            final File parcelPhotoFile = FileHelper.getFileFromName(pendingTask.getPhotoFileName(), context);

            disposable.add(apiService.updateParcelStatus(getHeaderToken(), APIHelper.getParcelIdsMultipartDataFromIdList(parcelIdList),
                    APIHelper.getStatusMultipartData(Constants.PARCEL_STATUS_COMPLETE),
                    APIHelper.getRecipientICMultipartData(pendingTask.getRecipientIc()),
                    APIHelper.getRecipientNameMultipartData(pendingTask.getRecipientName()),
                    APIHelper.getSignatureMultipartData(parcelSignatureFile),
                    APIHelper.getPhotoMultipartData(parcelPhotoFile),
                    APIHelper.getStatusDateTimeMultipartData(pendingTask.getStatusDateTime()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<UpdateParcelStatusResponse>() {
                        @Override
                        public void onSuccess(UpdateParcelStatusResponse apiResponse) {

                            if (apiResponse != null && apiResponse.getStatus() == Constants.API_STATUS_SUCCESS) {

                            } else {
                            }
                            removePendingTask(pendingTask.getParcelId());
                            FileHelper.deleteFile(parcelPhotoFile);
                            FileHelper.deleteFile(parcelSignatureFile);
                        }

                        @Override
                        public void onError(Throwable e) {
                            removePendingTask(pendingTask.getParcelId());
                            FileHelper.deleteFile(parcelPhotoFile);
                            FileHelper.deleteFile(parcelSignatureFile);
                        }
                    }));

        } catch (Exception ex) {
        }
    }

    private void executeSubmitProblematicParcelRequest(final PendingTask pendingTask) {
        try {
            final CompositeDisposable disposable = new CompositeDisposable();
            final NonCachedApiService apiService = retrofit.create(NonCachedApiService.class);
            final File parcelPhotoFile = FileHelper.getFileFromName(pendingTask.getPhotoFileName(), context);

            disposable.add(apiService.updateProblematicStatus(getHeaderToken(),
                    APIHelper.getParcelIdMultipartData(pendingTask.getParcelId()),
                    APIHelper.getParcelProblemIdMultipartData(pendingTask.getProblematicStatusId()),
                    APIHelper.getProblematicStatusDateTimeMultipartData(pendingTask.getStatusDateTime()),
                    APIHelper.getPhotoMultipartData(parcelPhotoFile))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ApiResponse>() {
                        @Override
                        public void onSuccess(ApiResponse apiResponse) {

                            if (apiResponse != null && apiResponse.getStatus() == Constants.API_STATUS_SUCCESS) {
                            } else {
                            }
                            removePendingTask(pendingTask.getParcelId());
                            FileHelper.deleteFile(parcelPhotoFile);
                        }

                        @Override
                        public void onError(Throwable e) {
                            removePendingTask(pendingTask.getParcelId());
                            FileHelper.deleteFile(parcelPhotoFile);
                        }
                    }));

        } catch (Exception ex) {
        }
    }

    private String getHeaderToken() {
        final String token = sharedPreferences.getString(Constants.SHARED_PREF_KEY_TOKEN, null);
        final String headerToken = String.format("Bearer %1$s", token);

        return headerToken;
    }

    public void removePendingTask(final String parcelID) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                pendingTaskDao.deleteTaskByParcelId(parcelID);
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
}