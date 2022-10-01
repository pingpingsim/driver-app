package com.pingu.driverapp.data.model;

import com.google.gson.annotations.SerializedName;

public class CompletedInfo {
    @SerializedName("ic")
    private String ic;
    @SerializedName("name")
    private String name;
    @SerializedName("signature")
    private String signature;

    public String getIc() {
        return ic;
    }

    public void setIc(String ic) {
        this.ic = ic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
