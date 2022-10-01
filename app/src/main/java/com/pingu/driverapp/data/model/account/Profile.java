package com.pingu.driverapp.data.model.account;

import com.google.gson.annotations.SerializedName;

public class Profile {
    @SerializedName("id")
    private int id;
    @SerializedName("login_id")
    private String loginId;
    @SerializedName("identity_number")
    private String identityNumber;
    @SerializedName("name")
    private String name;
    @SerializedName("address")
    private String address;
    @SerializedName("contact_number")
    private String contactNumber;
    @SerializedName("email")
    private String email;
    @SerializedName("token")
    private String token;
    @SerializedName("zone")
    private String zone;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }
}
