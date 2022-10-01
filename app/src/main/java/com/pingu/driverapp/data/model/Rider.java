package com.pingu.driverapp.data.model;

import com.google.gson.annotations.SerializedName;

public class Rider {
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
