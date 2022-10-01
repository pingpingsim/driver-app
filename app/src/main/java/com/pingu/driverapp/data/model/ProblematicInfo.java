package com.pingu.driverapp.data.model;

import com.google.gson.annotations.SerializedName;

public class ProblematicInfo {
    @SerializedName("info")
    private String info;
    @SerializedName("remark_photo")
    private String remarkPhoto;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getRemarkPhoto() {
        return remarkPhoto;
    }

    public void setRemarkPhoto(String remarkPhoto) {
        this.remarkPhoto = remarkPhoto;
    }
}
