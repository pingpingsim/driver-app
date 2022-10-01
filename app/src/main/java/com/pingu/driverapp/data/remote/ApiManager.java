package com.pingu.driverapp.data.remote;

import com.pingu.driverapp.data.model.ActionStatusInfo;
import com.pingu.driverapp.data.model.AnnouncementData;
import com.pingu.driverapp.data.model.ApiResponse;
import com.pingu.driverapp.data.model.CompletedParcelData;
import com.pingu.driverapp.data.model.DashboardSummary;
import com.pingu.driverapp.data.model.DashboardSummaryData;
import com.pingu.driverapp.data.model.OrderSummaryData;
import com.pingu.driverapp.data.model.OutForDeliveryParcelData;
import com.pingu.driverapp.data.model.ParcelData;
import com.pingu.driverapp.data.model.ParcelProblemData;
import com.pingu.driverapp.data.model.ParcelStatusData;
import com.pingu.driverapp.data.model.UpdateParcelStatusResponse;
import com.pingu.driverapp.data.model.account.ProfileData;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MultipartBody;
import retrofit2.http.Header;


public class ApiManager {
    private CompositeDisposable disposable = new CompositeDisposable();
    private CachedApiService apiService;
    private NonCachedApiService nonCachedApiService;

    @Inject
    public ApiManager(CachedApiService apiService, NonCachedApiService nonCachedApiService) {
        this.apiService = apiService;
        this.nonCachedApiService = nonCachedApiService;
    }

    public Single<ProfileData> authenticateUser(final String username, final String password) {
        return nonCachedApiService.authenticateUser(username, password);
    }

    public Single<ApiResponse> changePassword(final String token,
                                              final String currentPassword,
                                              final String newPassword) {
        return nonCachedApiService.changePassword(token, currentPassword, newPassword);
    }

    public Single<ApiResponse> resetPassword(final String email) {
        return nonCachedApiService.resetPassword(email);
    }

    public Single<ActionStatusInfo> acceptTask(final String token, String... parcelIds) {
        return nonCachedApiService.acceptTask(token, parcelIds);
    }

    public Single<ActionStatusInfo> removeTask(final String token, String... parcelIds) {
        return nonCachedApiService.removeTask(token, parcelIds);
    }

    public Single<UpdateParcelStatusResponse> updateParcelStatus(final String token,
                                                                 final List<MultipartBody.Part> parcelIds,
                                                                 final MultipartBody.Part status,
                                                                 final MultipartBody.Part recipientIC,
                                                                 final MultipartBody.Part recipientName,
                                                                 final MultipartBody.Part signature,
                                                                 final MultipartBody.Part photo,
                                                                 final MultipartBody.Part statusDateTime) {
        return nonCachedApiService.updateParcelStatus(token, parcelIds, status, recipientIC,
                recipientName, signature, photo, statusDateTime);
    }

    public Single<ApiResponse> updateProblematicStatus(final String token,
                                                       final MultipartBody.Part parcelId,
                                                       final MultipartBody.Part problematicStatusId,
                                                       final MultipartBody.Part problematicStatusDateTime,
                                                       final MultipartBody.Part photo) {
        return nonCachedApiService.updateProblematicStatus(token, parcelId,
                problematicStatusId, problematicStatusDateTime, photo);
    }


    public Single<ParcelStatusData> trackParcel(String token, String parcelID) {
        return apiService.trackParcel(token, parcelID);
    }

    public Single<ApiResponse> markAnnouncementRead(String token, int announcementID) {
        return nonCachedApiService.markAnnouncementRead(token, announcementID);
    }

    public Single<ProfileData> getProfile(final String token) {
        return apiService.getProfile(token);
    }

    public Single<AnnouncementData> getAnnouncements(final String token) {
        return apiService.getAnnouncements(token);
    }

    public Single<ParcelData> getAllTasks(final String token) {
        return apiService.getAllTasks(token);
    }

    public Single<ParcelData> getAvailableTasks(final String token) {
        return apiService.getAvailableTasks(token);
    }

    public Single<ParcelData> getMyTasks(final String token) {
        return apiService.getMyTasks(token);
    }

    public Single<ParcelData> getPendingPickupTasks(final String token) {
        return apiService.getPendingPickupTasks(token);
    }

    public Single<OutForDeliveryParcelData> getPendingDeliveryTasks(final String token) {
        return apiService.getPendingDeliveryTasks(token);
    }

    public Single<OutForDeliveryParcelData> getPendingArrivalAtHubParcel(final String token) {
        return apiService.getPendingArrivalAtHubParcel(token);
    }

    public Single<CompletedParcelData> getHistoryPickupTask(final String token, final String date) {
        return apiService.getHistoryPickupTask(token, date);
    }

    public Single<CompletedParcelData> getHistoryDeliveryTask(final String token, final String date) {
        return apiService.getHistoryDeliveryTask(token, date);
    }

    public Single<CompletedParcelData> getHistoryCompletedTask(final String token, final String date) {
        return apiService.getHistoryCompletedTask(token, date);
    }

    public Single<CompletedParcelData> getHistoryProblematicParcelTask(final String token, final String date) {
        return apiService.getHistoryProblematicParcelTask(token, date);
    }

    public Single<ParcelProblemData> getParcelProblemList(final String token) {
        return apiService.getParcelProblemList(token);
    }

    public Single<DashboardSummaryData> getDashboardSummary(final String token) {
        return apiService.getDashboardSummary(token);
    }

    public Single<OrderSummaryData> getOrdersSummary(final String token) {
        return apiService.getOrdersSummary(token);
    }
}