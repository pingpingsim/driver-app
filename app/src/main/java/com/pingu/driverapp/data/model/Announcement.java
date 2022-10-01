package com.pingu.driverapp.data.model;

import com.google.gson.annotations.SerializedName;
import com.pingu.driverapp.util.DateTimeHelper;

public class Announcement {
    @SerializedName("id")
    private int id;
    @SerializedName("description")
    private String description;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("read")
    private int read;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public String messageHistory() {
        final String timeAgo = DateTimeHelper.getTimeAgo(createdAt);

        return timeAgo;
    }
}
