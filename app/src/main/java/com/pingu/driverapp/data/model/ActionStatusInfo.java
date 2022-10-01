package com.pingu.driverapp.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class ActionStatusInfo {
    @SerializedName("status")
    private int status;
    @SerializedName("code")
    private String code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private Map<String, ActionStatus> actionStatusData;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, ActionStatus> getActionStatusData() {
        return actionStatusData;
    }

    public void setActionStatusData(Map<String, ActionStatus> actionStatusData) {
        this.actionStatusData = actionStatusData;
    }
}
