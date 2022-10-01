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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import retrofit2.Call;

public class RemoteDataManager {
    private ApiManager apiManager;

    @Inject
    public RemoteDataManager(ApiManager apiManager) {
        this.apiManager = apiManager;
    }

    public Single<ProfileData> getProfile(final String token) {
        return this.apiManager.getProfile(token);
    }

    public Single<ProfileData> authenticateUser(final String username, final String password) {
        return this.apiManager.authenticateUser(username, password);
    }

    public Single<ApiResponse> changePassword(final String token,
                                              final String currentPassword,
                                              final String newPassword) {
        return this.apiManager.changePassword(token, currentPassword, newPassword);
    }

    public Single<ApiResponse> resetPassword(final String email) {
        return this.apiManager.resetPassword(email);
    }

    public Single<AnnouncementData> getAnnouncements(final String token) {
        return apiManager.getAnnouncements(token);
    }

    public Single<ParcelData> getAllTasks(final String token) {
        return apiManager.getAllTasks(token);
    }

    public Single<ParcelData> getAvailableTasks(final String token) {
        return apiManager.getAvailableTasks(token);
    }

    public Single<ParcelData> getMyTasks(final String token) {
        return apiManager.getMyTasks(token);
    }

    public Single<ParcelData> getPendingPickupTasks(final String token) {
        return apiManager.getPendingPickupTasks(token);
    }

    public Single<OutForDeliveryParcelData> getPendingDeliveryTasks(final String token) {
        return apiManager.getPendingDeliveryTasks(token);
    }

    public Single<OutForDeliveryParcelData> getPendingArrivalAtHubParcel(final String token) {
        return apiManager.getPendingArrivalAtHubParcel(token);
    }

    public Single<ActionStatusInfo> acceptTask(final String token, String... parcelIds) {
        return apiManager.acceptTask(token, parcelIds);
    }

    public Single<ActionStatusInfo> removeTask(final String token, String... parcelIds) {
        return apiManager.removeTask(token, parcelIds);
    }

    public Single<UpdateParcelStatusResponse> updateParcelStatus(final String token, List<MultipartBody.Part> parcelIds,
                                                                 final MultipartBody.Part status,
                                                                 final MultipartBody.Part recipientIC,
                                                                 final MultipartBody.Part recipientName,
                                                                 final MultipartBody.Part signature,
                                                                 final MultipartBody.Part photo,
                                                                 final MultipartBody.Part statusDateTime) {
        return apiManager.updateParcelStatus(token, parcelIds, status, recipientIC, recipientName,
                signature, photo, statusDateTime);
    }

    public Single<ApiResponse> updateProblematicStatus(final String token,
                                                       final MultipartBody.Part parcelId,
                                                       final MultipartBody.Part problematicStatusId,
                                                       final MultipartBody.Part problematicStatusDateTime,
                                                       final MultipartBody.Part photo) {
        return apiManager.updateProblematicStatus(token, parcelId, problematicStatusId, problematicStatusDateTime, photo);
    }

    public Single<ParcelStatusData> trackParcel(String token, String parcelID) {
        return apiManager.trackParcel(token, parcelID);
    }

    public Single<ApiResponse> markAnnouncementRead(String token, int announcementID) {
        return apiManager.markAnnouncementRead(token, announcementID);
    }

    public Single<CompletedParcelData> getHistoryPickupTask(final String token, final String date) {
        return apiManager.getHistoryPickupTask(token, date);
    }

    public Single<CompletedParcelData> getHistoryDeliveryTask(final String token, final String date) {
        return apiManager.getHistoryDeliveryTask(token, date);
    }

    public Single<CompletedParcelData> getHistoryCompletedTask(final String token, final String date) {
        return apiManager.getHistoryCompletedTask(token, date);
    }

    public Single<CompletedParcelData> getHistoryProblematicParcelTask(final String token, final String date) {
        return apiManager.getHistoryProblematicParcelTask(token, date);
    }

    public Single<ParcelProblemData> getParcelProblemList(final String token) {
        return apiManager.getParcelProblemList(token);
    }

    public Single<DashboardSummaryData> getDashboardSummary(final String token) {
        return apiManager.getDashboardSummary(token);
    }

    public Single<OrderSummaryData> getOrdersSummary(final String token) {
        return apiManager.getOrdersSummary(token);
    }
}