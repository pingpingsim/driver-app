package com.pingu.driverapp.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedHashMap;

public class UpdateParcelStatusResponse {
    @SerializedName("status")
    private int status;
    @SerializedName("code")
    private String code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private LinkedHashMap<String, ApiResponse> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LinkedHashMap<String, ApiResponse> getData() {
        return data;
    }

    public void setData(LinkedHashMap<String, ApiResponse> data) {
        this.data = data;
    }
}
