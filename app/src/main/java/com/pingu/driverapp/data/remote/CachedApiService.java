package com.pingu.driverapp.data.remote;

import com.pingu.driverapp.data.model.AnnouncementData;
import com.pingu.driverapp.data.model.CompletedParcelData;
import com.pingu.driverapp.data.model.DashboardSummary;
import com.pingu.driverapp.data.model.DashboardSummaryData;
import com.pingu.driverapp.data.model.OrderSummaryData;
import com.pingu.driverapp.data.model.OutForDeliveryParcelData;
import com.pingu.driverapp.data.model.ParcelData;
import com.pingu.driverapp.data.model.ParcelProblemData;
import com.pingu.driverapp.data.model.ParcelStatusData;
import com.pingu.driverapp.data.model.account.ProfileData;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CachedApiService {

    @GET("api/rider-apps/profile")
    Single<ProfileData> getProfile(@Header("Authorization") String token);

    @GET("api/rider-apps/announcement")
    Single<AnnouncementData> getAnnouncements(@Header("Authorization") String token);

    @GET("api/rider-apps/parcel")
    Single<ParcelData> getAllTasks(@Header("Authorization") String token);

    @GET("api/rider-apps/parcel?is_available=1")
    Single<ParcelData> getAvailableTasks(@Header("Authorization") String token);

    @GET("api/rider-apps/parcel?is_my_task=1")
    Single<ParcelData> getMyTasks(@Header("Authorization") String token);

    @GET("api/rider-apps/parcel?is_pending_pickup=1")
    Single<ParcelData> getPendingPickupTasks(@Header("Authorization") String token);

    @GET("api/rider-apps/parcel?is_pending_delivery=1")
    Single<OutForDeliveryParcelData> getPendingDeliveryTasks(@Header("Authorization") String token);

    @GET("api/rider-apps/parcel?is_pending_arrival_at_hub=1")
    Single<OutForDeliveryParcelData> getPendingArrivalAtHubParcel(@Header("Authorization") String token);

    @POST("api/rider-apps/track")
    Single<ParcelStatusData> trackParcel(@Header("Authorization") String token, @Query("parcel_id") String parcelID);

    @GET("api/rider-apps/parcel-history?today=0&pickup=1")
    Single<CompletedParcelData> getHistoryPickupTask(@Header("Authorization") String token, @Query("date") String date);

    @GET("api/rider-apps/parcel-history?today=0&delivery=1")
    Single<CompletedParcelData> getHistoryDeliveryTask(@Header("Authorization") String token, @Query("date") String date);

    @GET("api/rider-apps/parcel-history?today=0&completed=1")
    Single<CompletedParcelData> getHistoryCompletedTask(@Header("Authorization") String token, @Query("date") String date);

    @GET("api/rider-apps/parcel-history?today=0&problematic=1")
    Single<CompletedParcelData> getHistoryProblematicParcelTask(@Header("Authorization") String token, @Query("date") String date);

    @GET("api/rider-apps/problematic-status-list")
    Single<ParcelProblemData> getParcelProblemList(@Header("Authorization") String token);

    @GET("api/rider-apps/dashboard")
    Single<DashboardSummaryData> getDashboardSummary(@Header("Authorization") String token);

    @GET("api/rider-apps/parcel-activity-dashboard")
    Single<OrderSummaryData> getOrdersSummary(@Header("Authorization") String token);
}