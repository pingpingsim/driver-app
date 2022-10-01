package com.pingu.driverapp.data.model;

import com.google.gson.annotations.SerializedName;

public class OrderSummaryData {
    @SerializedName("status")
    private int status;
    @SerializedName("code")
    private String code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private OrderSummary data;

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

    public OrderSummary getData() {
        return data;
    }

    public void setData(OrderSummary data) {
        this.data = data;
    }
}
