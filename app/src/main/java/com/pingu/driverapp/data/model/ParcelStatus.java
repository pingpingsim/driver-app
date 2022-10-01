package com.pingu.driverapp.data.model;

import com.google.gson.annotations.SerializedName;
import com.pingu.driverapp.data.model.Status;

import java.util.List;

public class ParcelStatus {
    @SerializedName("created_date")
    private String createdDate;
    @SerializedName("recipient_name")
    private String recipientName;
    @SerializedName("statuses")
    private List<Status> statusList;

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public List<Status> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Status> statusList) {
        this.statusList = statusList;
    }
}
