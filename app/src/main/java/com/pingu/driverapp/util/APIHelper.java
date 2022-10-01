package com.pingu.driverapp.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pingu.driverapp.BaseApplication;
import com.pingu.driverapp.R;
import com.pingu.driverapp.data.model.Parcel;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.HttpException;

public class APIHelper {

    public static String handleExceptionMessage(final Throwable e) {
        try {
            final String jsonResponse = ((HttpException) e).response().errorBody().string();
            final JSONObject errorJsonObj = new JSONObject(jsonResponse);
            final String errorMsg = errorJsonObj.has("message") ? errorJsonObj.getString("message") : "";
            final String errorData = errorJsonObj.has("data") ? errorJsonObj.getString("data") : "";

            return ((!TextUtils.isEmpty(errorData) && errorData.length() > 4) ? formatErrorData(errorData) : errorMsg);
        } catch (UnknownHostException ex) {
            return BaseApplication.getInstance().getString(R.string.error_server);

        } catch (Exception ex) {
            if (!NetworkHelper.isConnected()) {
                return BaseApplication.getInstance().getString(R.string.error_no_internet);
            } else {
                return BaseApplication.getInstance().getString(R.string.error_server);
            }
        }
    }

    private static String formatErrorData(final String errorData) {
        if (!TextUtils.isEmpty(errorData) && (errorData.contains("{") || errorData.contains("["))) {
            try {
                String errorMsg = "";
                Type mapType = new TypeToken<Map<String, String[]>>() {
                }.getType();
                Map<String, String[]> errorDataMap = new Gson().fromJson(errorData, mapType);
                for (Map.Entry<String, String[]> entry : errorDataMap.entrySet()) {
                    final String key = entry.getKey();
                    final String[] values = entry.getValue();
                    if (values != null && values.length > 0) {
                        for (final String value : values) {
                            errorMsg += value + "\n";
                        }
                    }
                }
                return errorMsg.trim();

            } catch (Exception ex) {
                return errorData;
            }
        } else
            return errorData;
    }

    public static MultipartBody.Part getStatusMultipartData(final String status) {
        return MultipartBody.Part.createFormData("status", status);
    }

    public static MultipartBody.Part getRecipientICMultipartData(final String recipientIC) {
        return MultipartBody.Part.createFormData("recipient_ic", recipientIC);
    }

    public static MultipartBody.Part getRecipientNameMultipartData(final String recipientName) {
        return MultipartBody.Part.createFormData("recipient_name", recipientName);
    }

    public static MultipartBody.Part getPhotoMultipartData(final File file) {
        if (file != null && file.exists()) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            return MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
        } else {
            return null;
        }
    }

    public static MultipartBody.Part getSignatureMultipartData(final File file) {
        if (file != null && file.exists()) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            return MultipartBody.Part.createFormData("signature", file.getName(), requestFile);
        } else {
            return null;
        }
    }

    public static List<MultipartBody.Part> getParcelIdsMultipartData(List<Parcel> parcelList) {
        List<MultipartBody.Part> multipartBodyContent = new ArrayList<MultipartBody.Part>();
        if (parcelList != null && parcelList.size() > 0) {
            for (Parcel parcel : parcelList) {
                multipartBodyContent.add(MultipartBody.Part.createFormData("parcel_ids[]", parcel.getParcelId()));

            }
        }
        return multipartBodyContent;
    }

    public static List<MultipartBody.Part> getParcelIdsMultipartDataFromIdList(List<String> parcelIdList) {
        List<MultipartBody.Part> multipartBodyContent = new ArrayList<MultipartBody.Part>();
        if (parcelIdList != null && parcelIdList.size() > 0) {
            for (String parcelId : parcelIdList) {
                multipartBodyContent.add(MultipartBody.Part.createFormData("parcel_ids[]", parcelId));

            }
        }
        return multipartBodyContent;
    }

    public static MultipartBody.Part getStatusDateTimeMultipartData(final String statusDateTime) {
        return MultipartBody.Part.createFormData("status_date_time[]", statusDateTime);
    }

    // Problematic Parcel
    public static MultipartBody.Part getParcelIdMultipartData(final String parcelId) {
        return MultipartBody.Part.createFormData("parcel_id", parcelId);
    }

    public static MultipartBody.Part getParcelProblemIdMultipartData(final int parcelProblemId) {
        return MultipartBody.Part.createFormData("problematic_status_id", String.valueOf(parcelProblemId));
    }

    public static MultipartBody.Part getProblematicStatusDateTimeMultipartData(final String statusDateTime) {
        return MultipartBody.Part.createFormData("problematic_status_date_time", statusDateTime);
    }
}
