package com.pingu.driverapp.data.model;

import com.google.gson.annotations.SerializedName;
import com.pingu.driverapp.util.DateTimeHelper;

public class Parcel {
    @SerializedName("parcel_id")
    private String parcelId;
    @SerializedName("sender_name")
    private String senderName;
    @SerializedName("sender_contact_number")
    private String senderContactNumber;
    @SerializedName("sender_address")
    private String senderAddress;
    @SerializedName("recipient_name")
    private String recipientName;
    @SerializedName("recipient_contact_number")
    private String recipientContactNumber;
    @SerializedName("recipient_address")
    private String recipientAddress;
    @SerializedName("pickup_by")
    private Rider rider;
    @SerializedName("deliver_by")
    private Rider deliverBy;
    @SerializedName("status_date")
    private String statusDate;
    @SerializedName("completed_info")
    private CompletedInfo completedInfo;
    @SerializedName("problematic_info")
    private ProblematicInfo problematicInfo;
    private int rowDataType;
    private int index;
    private int totalParcels;
    private int orderId;

    public String getParcelId() {
        return parcelId;
    }

    public void setParcelId(String parcelId) {
        this.parcelId = parcelId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderContactNumber() {
        return senderContactNumber;
    }

    public void setSenderContactNumber(String senderContactNumber) {
        this.senderContactNumber = senderContactNumber;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientContactNumber() {
        return recipientContactNumber;
    }

    public void setRecipientContactNumber(String recipientContactNumber) {
        this.recipientContactNumber = recipientContactNumber;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public Rider getRider() {
        return rider;
    }

    public void setRider(Rider rider) {
        this.rider = rider;
    }

    public String getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }

    public Rider getDeliverBy() {
        return deliverBy;
    }

    public void setDeliverBy(Rider deliverBy) {
        this.deliverBy = deliverBy;
    }

    public ProblematicInfo getProblematicInfo() {
        return problematicInfo;
    }

    public void setProblematicInfo(ProblematicInfo problematicInfo) {
        this.problematicInfo = problematicInfo;
    }

    public CompletedInfo getCompletedInfo() {
        return completedInfo;
    }

    public void setCompletedInfo(CompletedInfo completedInfo) {
        this.completedInfo = completedInfo;
    }

    public int getRowDataType() {
        return rowDataType;
    }

    public void setRowDataType(int rowDataType) {
        this.rowDataType = rowDataType;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTotalParcels() {
        return totalParcels;
    }

    public void setTotalParcels(int totalParcels) {
        this.totalParcels = totalParcels;
    }

    public String getParcelStatusDate(){
        return DateTimeHelper.getDateOnly(statusDate);
    }

    public String getParcelStatusTime(){
        return DateTimeHelper.getTimeOnly(statusDate);
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true; //if both pointing towards same object on heap

        Parcel a = (Parcel) obj;
        return (this.parcelId != null && a.parcelId != null && this.parcelId.equals(a.parcelId));
    }
}
