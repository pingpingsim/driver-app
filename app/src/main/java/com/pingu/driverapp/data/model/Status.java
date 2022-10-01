package com.pingu.driverapp.data.model;

import com.google.gson.annotations.SerializedName;

public class Status {
    @SerializedName("status")
    private int status;
    @SerializedName("status_name")
    private String statusName;
    @SerializedName("status_date")
    private String statusDate;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }
}
