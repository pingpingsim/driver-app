package com.pingu.driverapp.data;

import android.content.Context;

import com.pingu.driverapp.data.local.db.LocalRoomDatabase;
import com.pingu.driverapp.data.local.prefs.LocalCacheManager;
import com.pingu.driverapp.data.model.ActionStatusInfo;
import com.pingu.driverapp.data.model.AnnouncementData;
import com.pingu.driverapp.data.model.ApiResponse;
import com.pingu.driverapp.data.model.CompletedParcelData;
import com.pingu.driverapp.data.model.DashboardSummaryData;
import com.pingu.driverapp.data.model.OrderSummaryData;
import com.pingu.driverapp.data.model.OutForDeliveryParcelData;
import com.pingu.driverapp.data.model.ParcelData;
import com.pingu.driverapp.data.model.ParcelProblemData;
import com.pingu.driverapp.data.model.ParcelStatusData;
import com.pingu.driverapp.data.model.UpdateParcelStatusResponse;
import com.pingu.driverapp.data.model.account.Profile;
import com.pingu.driverapp.data.model.account.ProfileData;
import com.pingu.driverapp.data.model.local.PendingDeliveryOrder;
import com.pingu.driverapp.data.model.local.PendingPickupOrder;
import com.pingu.driverapp.data.model.local.PendingTask;
import com.pingu.driverapp.data.remote.RemoteDataManager;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import okhttp3.MultipartBody;

public class DataManager {
    private RemoteDataManager remoteDataManager;
    private LocalCacheManager localCacheManager;
    private LocalRoomDatabase offlineStorage;
    private Context context;

    @Inject
    public DataManager(RemoteDataManager remoteDataManager, LocalCacheManager localCacheManager, LocalRoomDatabase offlineStorage, Context context) {
        this.remoteDataManager = remoteDataManager;
        this.localCacheManager = localCacheManager;
        this.offlineStorage = offlineStorage;
        this.context = context;
    }

    public static class OfflineProvider {
        private LocalCacheManager localCacheManager;
        private LocalRoomDatabase offlineStorage;

        @Inject
        public OfflineProvider(LocalCacheManager localCacheManager, LocalRoomDatabase offlineStorage) {
            this.localCacheManager = localCacheManager;
            this.offlineStorage = offlineStorage;
        }

        public void setToken(final String token) {
            localCacheManager.setToken(token);
        }

        public String getToken() {
            return localCacheManager.getToken();
        }


        public void setProfile(final Profile profile) {
            localCacheManager.setProfile(profile);
        }

        public Profile getProfile() {
            return localCacheManager.getProfile();
        }

        public void setAvailableAddress(final int value) {
            localCacheManager.setAvailableAddress(value);
        }

        public int getAvailableAddress() {
            return localCacheManager.getAvailableAddress();
        }

        public void setPendingPickup(final int value) {
            localCacheManager.setPendingPickup(value);
        }

        public int getPendingPickup() {
            return localCacheManager.getPendingPickup();
        }

        public void setPendingDelivery(final int value) {
            localCacheManager.setPendingDelivery(value);
        }

        public int getPendingDelivery() {
            return localCacheManager.getPendingDelivery();
        }

        public void setTotalPendingArrivalAtHub(final int value) {
            localCacheManager.setTotalPendingArrivalAtHub(value);
        }

        public int getTotalPendingArrivalAtHub() {
            return localCacheManager.getTotalPendingArrivalAtHub();
        }

        public void setTodayPickup(final int value) {
            localCacheManager.setTodayPickup(value);
        }

        public int getTodayPickup() {
            return localCacheManager.getTodayPickup();
        }

        public void setTodayDelivery(final int value) {
            localCacheManager.setTodayDelivery(value);
        }

        public int getTodayDelivery() {
            return localCacheManager.getTodayDelivery();
        }

        public void setTodayCompleted(final int value) {
            localCacheManager.setTodayCompleted(value);
        }

        public int getTodayCompleted() {
            return localCacheManager.getTodayCompleted();
        }

        public void setTodayProblematic(final int value) {
            localCacheManager.setTodayProblematic(value);
        }

        public int getTodayProblematic() {
            return localCacheManager.getTodayProblematic();
        }

        public void setHistoryPickup(final int value) {
            localCacheManager.setHistoryPickup(value);
        }

        public int getHistoryPickup() {
            return localCacheManager.getHistoryPickup();
        }

        public void setHistoryDelivery(final int value) {
            localCacheManager.setHistoryDelivery(value);
        }

        public int getHistoryDelivery() {
            return localCacheManager.getHistoryDelivery();
        }

        public void setHistoryCompleted(final int value) {
            localCacheManager.setHistoryCompleted(value);
        }

        public int getHistoryCompleted() {
            return localCacheManager.getHistoryCompleted();
        }

        public void setHistoryProblematic(final int value) {
            localCacheManager.setHistoryProblematic(value);
        }

        public int getHistoryProblematic() {
            return localCacheManager.getHistoryProblematic();
        }

        public void insertPendingTask(PendingTask... pendingTasks) {
            offlineStorage.pendingTaskDao().insertTask(pendingTasks);
        }

        public void deleteAllPendingTask() {
            offlineStorage.pendingTaskDao().deleteAllPendingTask();
        }

        public void insertPendingPickupOrder(PendingPickupOrder... pendingPickupOrders) {
            offlineStorage.pendingPickupOrderDao().insertPendingPickupOrder(pendingPickupOrders);
        }

        public void deleteAllPendingPickupOrder() {
            offlineStorage.pendingPickupOrderDao().deleteAllPendingPickupOrder();
        }

        public Single<List<PendingPickupOrder>> getAllPendingPickupOrder() {
            return offlineStorage.pendingPickupOrderDao().getAllPendingPickupOrder();
        }

        public void insertPendingDeliveryOrder(PendingDeliveryOrder... pendingDeliveryOrders) {
            offlineStorage.pendingDeliveryOrderDao().insertPendingDeliveryOrder(pendingDeliveryOrders);
        }

        public void deleteAllPendingDeliveryOrder() {
            offlineStorage.pendingDeliveryOrderDao().deleteAllPendingDeliveryOrder();
        }

        public Single<List<PendingDeliveryOrder>> getAllPendingDeliveryOrder() {
            return offlineStorage.pendingDeliveryOrderDao().getAllPendingDeliveryOrder();
        }
    }

    public static class RemoteProvider {
        private RemoteDataManager remoteDataManager;
        private OfflineProvider offlineProvider;

        @Inject
        public RemoteProvider(RemoteDataManager remoteDataManager, OfflineProvider offlineProvider) {
            this.remoteDataManager = remoteDataManager;
            this.offlineProvider = offlineProvider;
        }

        public Single<ProfileData> getProfile() {
            return remoteDataManager.getProfile(getHeaderToken());
        }

        public Single<ProfileData> authenticateUser(final String username, final String password) {
            return remoteDataManager.authenticateUser(username, password);
        }

        public Single<ApiResponse> changePassword(
                final String currentPassword,
                final String newPassword) {
            return remoteDataManager.changePassword(getHeaderToken(), currentPassword, newPassword);
        }

        public Single<ApiResponse> resetPassword(final String email) {
            return remoteDataManager.resetPassword(email);
        }

        public Single<AnnouncementData> getAnnouncements() {
            return remoteDataManager.getAnnouncements(getHeaderToken());
        }

        public Single<ParcelData> getAllTasks() {
            return remoteDataManager.getAllTasks(getHeaderToken());
        }

        public Single<ParcelData> getAvailableTasks() {
            return remoteDataManager.getAvailableTasks(getHeaderToken());
        }

        public Single<ParcelData> getMyTasks() {
            return remoteDataManager.getMyTasks(getHeaderToken());
        }

        public Single<ParcelData> getPendingPickupTasks() {
            return remoteDataManager.getPendingPickupTasks(getHeaderToken());
        }

        public Single<OutForDeliveryParcelData> getPendingDeliveryTasks() {
            return remoteDataManager.getPendingDeliveryTasks(getHeaderToken());
        }

        public Single<OutForDeliveryParcelData> getPendingArrivalAtHubParcel() {
            return remoteDataManager.getPendingArrivalAtHubParcel(getHeaderToken());
        }

        public Single<ActionStatusInfo> acceptTask(final String... parcelIds) {
            return remoteDataManager.acceptTask(getHeaderToken(), parcelIds);
        }

        public Single<ActionStatusInfo> removeTask(final String... parcelIds) {
            return remoteDataManager.removeTask(getHeaderToken(), parcelIds);
        }

        public Single<UpdateParcelStatusResponse> updateParcelStatus(final List<MultipartBody.Part> parcelIds,
                                                                     final MultipartBody.Part status,
                                                                     final MultipartBody.Part recipientIC,
                                                                     final MultipartBody.Part recipientName,
                                                                     final MultipartBody.Part signature,
                                                                     final MultipartBody.Part photo,
                                                                     final MultipartBody.Part statusDateTime) {
            return remoteDataManager.updateParcelStatus(getHeaderToken(),
                    parcelIds, status, recipientIC, recipientName, signature, photo, statusDateTime);
        }

        public Single<ApiResponse> updateProblematicStatus(
                final MultipartBody.Part parcelId,
                final MultipartBody.Part problematicStatusId,
                final MultipartBody.Part problematicStatusDateTime,
                final MultipartBody.Part photo) {
            return remoteDataManager.updateProblematicStatus(getHeaderToken(), parcelId, problematicStatusId, problematicStatusDateTime, photo);
        }

        public Single<ParcelStatusData> trackParcel(String parcelID) {
            return remoteDataManager.trackParcel(getHeaderToken(), parcelID);
        }

        public Single<ApiResponse> markAnnouncementRead(int announcementID) {
            return remoteDataManager.markAnnouncementRead(getHeaderToken(), announcementID);
        }

        public Single<CompletedParcelData> getHistoryPickupTask(final String date) {
            return remoteDataManager.getHistoryPickupTask(getHeaderToken(), date);
        }

        public Single<CompletedParcelData> getHistoryDeliveryTask(final String date) {
            return remoteDataManager.getHistoryDeliveryTask(getHeaderToken(), date);
        }

        public Single<CompletedParcelData> getHistoryCompletedTask(final String date) {
            return remoteDataManager.getHistoryCompletedTask(getHeaderToken(), date);
        }

        public Single<CompletedParcelData> getHistoryProblematicParcelTask(final String date) {
            return remoteDataManager.getHistoryProblematicParcelTask(getHeaderToken(), date);
        }

        public Single<ParcelProblemData> getParcelProblemList() {
            return remoteDataManager.getParcelProblemList(getHeaderToken());
        }

        public Single<DashboardSummaryData> getDashboardSummary() {
            return remoteDataManager.getDashboardSummary(getHeaderToken());
        }

        public Single<OrderSummaryData> getOrdersSummary() {
            return remoteDataManager.getOrdersSummary(getHeaderToken());
        }

        private String getHeaderToken() {
            final String token = offlineProvider.getToken();
            final String headerToken = String.format("Bearer %1$s", token);

            return headerToken;
        }
    }
}