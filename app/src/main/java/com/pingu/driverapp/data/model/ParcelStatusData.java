package com.pingu.driverapp.data.model;

import com.google.gson.annotations.SerializedName;

public class ParcelStatusData {
    @SerializedName("status")
    private int status;
    @SerializedName("code")
    private String code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private ParcelStatus data;

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

    public ParcelStatus getData() {
        return data;
    }

    public void setData(ParcelStatus data) {
        this.data = data;
    }
}
