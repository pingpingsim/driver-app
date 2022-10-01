package com.pingu.driverapp.data.local.prefs;

import com.pingu.driverapp.data.model.account.Profile;

import javax.inject.Inject;

public class LocalCacheManager {
    private SharedPrefManager prefManager;

    @Inject
    public LocalCacheManager(SharedPrefManager manager) {
        this.prefManager = manager;
    }

    public void setToken(final String token) {
        prefManager.setToken(token);
    }

    public String getToken() {
        return prefManager.getToken();
    }

    public void setProfile(final Profile profile) {
        prefManager.setProfile(profile);
    }

    public Profile getProfile() {
        return prefManager.getProfile();
    }

    public void setAvailableAddress(final int value) {
        prefManager.setAvailableAddress(value);
    }

    public int getAvailableAddress() {
        return prefManager.getAvailableAddress();
    }

    public void setPendingPickup(final int value) {
        prefManager.setPendingPickup(value);
    }

    public int getPendingPickup() {
        return prefManager.getPendingPickup();
    }

    public void setPendingDelivery(final int value) {
        prefManager.setPendingDelivery(value);
    }

    public int getPendingDelivery() {
        return prefManager.getPendingDelivery();
    }

    public void setTotalPendingArrivalAtHub(final int value) {
        prefManager.setTotalPendingArrivalAtHub(value);
    }

    public int getTotalPendingArrivalAtHub() {
        return prefManager.getTotalPendingArrivalAtHub();
    }

    public void setTodayPickup(final int value) {
        prefManager.setTodayPickup(value);
    }

    public int getTodayPickup() {
        return prefManager.getTodayPickup();
    }

    public void setTodayDelivery(final int value) {
        prefManager.setTodayDelivery(value);
    }

    public int getTodayDelivery() {
        return prefManager.getTodayDelivery();
    }

    public void setTodayCompleted(final int value) {
        prefManager.setTodayCompleted(value);
    }

    public int getTodayCompleted() {
        return prefManager.getTodayCompleted();
    }

    public void setTodayProblematic(final int value) {
        prefManager.setTodayProblematic(value);
    }

    public int getTodayProblematic() {
        return prefManager.getTodayProblematic();
    }

    public void setHistoryPickup(final int value) {
        prefManager.setHistoryPickup(value);
    }

    public int getHistoryPickup() {
        return prefManager.getHistoryPickup();
    }

    public void setHistoryDelivery(final int value) {
        prefManager.setHistoryDelivery(value);
    }

    public int getHistoryDelivery() {
        return prefManager.getHistoryDelivery();
    }

    public void setHistoryCompleted(final int value) {
        prefManager.setHistoryCompleted(value);
    }

    public int getHistoryCompleted() {
        return prefManager.getHistoryCompleted();
    }

    public void setHistoryProblematic(final int value) {
        prefManager.setHistoryProblematic(value);
    }

    public int getHistoryProblematic() {
        return prefManager.getHistoryProblematic();
    }
}
