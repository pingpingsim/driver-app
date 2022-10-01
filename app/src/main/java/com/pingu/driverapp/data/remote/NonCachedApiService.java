package com.pingu.driverapp.data.remote;

import com.pingu.driverapp.data.model.ActionStatusInfo;
import com.pingu.driverapp.data.model.ApiResponse;
import com.pingu.driverapp.data.model.UpdateParcelStatusResponse;
import com.pingu.driverapp.data.model.account.ProfileData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface NonCachedApiService {

    @GET("api/rider-apps/login")
    Single<ProfileData> authenticateUser(@Query("username") String username, @Query("password") String password);

    @PATCH("api/rider-apps/change-password")
    Single<ApiResponse> changePassword(@Header("Authorization") String token,
                                       @Query("password") String currentPassword,
                                       @Query("new_password") String newPassword);

    @POST("api/rider-apps/reset-password")
    Single<ApiResponse> resetPassword(@Query("email") String email);

    @POST("api/rider-apps/parcel-delivery?is_pickup=1")
    Single<ActionStatusInfo> acceptTask(@Header("Authorization") String token, @Query("parcel_ids[]") String... parcelIds);

    @POST("api/rider-apps/parcel-delivery?is_not_pickup=1")
    Single<ActionStatusInfo> removeTask(@Header("Authorization") String token, @Query("parcel_ids[]") String... parcelIds);

    @POST("api/rider-apps/read-announcement")
    Single<ApiResponse> markAnnouncementRead(@Header("Authorization") String token, @Query("announcement_id") int announcementId);

    @Multipart
    @POST("api/rider-apps/update-status")
    Single<UpdateParcelStatusResponse> updateParcelStatus(@Header("Authorization") String token,
                                                          @Part List<MultipartBody.Part> parcelIds,
                                                          @Part MultipartBody.Part status,
                                                          @Part MultipartBody.Part recipientIC,
                                                          @Part MultipartBody.Part recipientName,
                                                          @Part MultipartBody.Part signature,
                                                          @Part MultipartBody.Part photo,
                                                          @Part MultipartBody.Part statusDateTime);

    @Multipart
    @POST("api/rider-apps/update-problematic-status")
    Single<ApiResponse> updateProblematicStatus(@Header("Authorization") String token,
                                                @Part MultipartBody.Part parcelId,
                                                @Part MultipartBody.Part problematicStatusId,
                                                @Part MultipartBody.Part problematicStatusDateTime,
                                                @Part MultipartBody.Part photo);
}