package com.pingu.driverapp.data.model;

import com.google.gson.annotations.SerializedName;

public class OrderTotal {
    @SerializedName("pick_up")
    private int totalPickupParcel;
    @SerializedName("delivery")
    private int totalDeliveryParcel;
    @SerializedName("completed")
    private int totalCompletedParcel;
    @SerializedName("problematic")
    private int totalProblematicParcel;

    public int getTotalPickupParcel() {
        return totalPickupParcel;
    }

    public void setTotalPickupParcel(int totalPickupParcel) {
        this.totalPickupParcel = totalPickupParcel;
    }

    public int getTotalDeliveryParcel() {
        return totalDeliveryParcel;
    }

    public void setTotalDeliveryParcel(int totalDeliveryParcel) {
        this.totalDeliveryParcel = totalDeliveryParcel;
    }

    public int getTotalCompletedParcel() {
        return totalCompletedParcel;
    }

    public void setTotalCompletedParcel(int totalCompletedParcel) {
        this.totalCompletedParcel = totalCompletedParcel;
    }

    public int getTotalProblematicParcel() {
        return totalProblematicParcel;
    }

    public void setTotalProblematicParcel(int totalProblematicParcel) {
        this.totalProblematicParcel = totalProblematicParcel;
    }
}
