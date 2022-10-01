package com.pingu.driverapp.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Task {
    private int orderId;
    private String name;
    private String address;
    private String contactNumber;
    private boolean isChecked;
    private String pickupBy;
    private List<Parcel> parcelList;

    public Task(final String name, final String address, final String contactNumber, final List<Parcel> parcelList, final String pickupBy) {
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
        this.parcelList = parcelList;
        this.pickupBy = pickupBy;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public List<Parcel> getParcelList() {
        return parcelList;
    }

    public void setParcelList(List<Parcel> parcelList) {
        this.parcelList = parcelList;
    }

    public String getPickupBy() {
        return pickupBy;
    }

    public void setPickupBy(String pickupBy) {
        this.pickupBy = pickupBy;
    }
}
