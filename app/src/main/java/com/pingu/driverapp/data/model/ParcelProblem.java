package com.pingu.driverapp.data.model;

import com.google.gson.annotations.SerializedName;

public class ParcelProblem {
    @SerializedName("id")
    private int id;
    @SerializedName("category")
    private String category;
    @SerializedName("description")
    private String reason;
    private boolean isChecked;
    public int rowDataType;

    public ParcelProblem() {

    }

    public ParcelProblem(final int id, final String reason, final boolean isChecked) {
        this.id = id;
        this.reason = reason;
        this.isChecked = isChecked;
    }

    public ParcelProblem(final int id, final String category, final String reason, final boolean isChecked) {
        this.id = id;
        this.category = category;
        this.reason = reason;
        this.isChecked = isChecked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getRowDataType() {
        return rowDataType;
    }

    public void setRowDataType(int rowDataType) {
        this.rowDataType = rowDataType;
    }
}
