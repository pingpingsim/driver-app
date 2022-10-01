package com.pingu.driverapp.data.model;

import com.google.gson.annotations.SerializedName;

public class DashboardSummary {
    @SerializedName("available")
    private int totalAvailableParcel;
    @SerializedName("pending_pickup")
    private int totalPendingPickupParcel;
    @SerializedName("pending_delivery")
    private int totalPendingDeliveryParcel;
    @SerializedName("pending_arrival_at_hub")
    private int totalPendingArrivalAtHub;

    public int getTotalAvailableParcel() {
        return totalAvailableParcel;
    }

    public void setTotalAvailableParcel(int totalAvailableParcel) {
        this.totalAvailableParcel = totalAvailableParcel;
    }

    public int getTotalPendingPickupParcel() {
        return totalPendingPickupParcel;
    }

    public void setTotalPendingPickupParcel(int totalPendingPickupParcel) {
        this.totalPendingPickupParcel = totalPendingPickupParcel;
    }

    public int getTotalPendingDeliveryParcel() {
        return totalPendingDeliveryParcel;
    }

    public void setTotalPendingDeliveryParcel(int totalPendingDeliveryParcel) {
        this.totalPendingDeliveryParcel = totalPendingDeliveryParcel;
    }

    public int getTotalPendingArrivalAtHub() {
        return totalPendingArrivalAtHub;
    }

    public void setTotalPendingArrivalAtHub(int totalPendingArrivalAtHub) {
        this.totalPendingArrivalAtHub = totalPendingArrivalAtHub;
    }
}
