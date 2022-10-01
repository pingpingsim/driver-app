package com.pingu.driverapp.data.local.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.pingu.driverapp.data.model.account.Profile;
import com.pingu.driverapp.util.Constants;

import javax.inject.Inject;

public class SharedPrefManager {
    private static final String DEFAULT_LANGUAGE = "en";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private Context context;

    @Inject
    public SharedPrefManager(Context context, Gson gson) {
        this.gson = gson;
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    public void setToken(final String token) {
        editor.putString(Constants.SHARED_PREF_KEY_TOKEN, token);
        editor.commit();
    }

    public String getToken() {
        return sharedPreferences.getString(Constants.SHARED_PREF_KEY_TOKEN, null);
    }

    public void setProfile(final Profile profile) {
        editor.putString(Constants.SHARED_PREF_KEY_PROFILE, new Gson().toJson(profile));
        editor.commit();
    }

    public Profile getProfile() {
        final String profileJsonStr = sharedPreferences.getString(Constants.SHARED_PREF_KEY_PROFILE, null);
        if (!TextUtils.isEmpty(profileJsonStr)) {
            return new Gson().fromJson(profileJsonStr, Profile.class);
        }
        return null;
    }

    //Dashboard

    public void setAvailableAddress(final int value) {
        editor.putInt(Constants.SHARED_PREF_KEY_TOTAL_AVAILABLE_ADDRESS, value);
        editor.commit();
    }

    public int getAvailableAddress() {
        return sharedPreferences.getInt(Constants.SHARED_PREF_KEY_TOTAL_AVAILABLE_ADDRESS, 0);
    }

    public void setPendingPickup(final int value) {
        editor.putInt(Constants.SHARED_PREF_KEY_TOTAL_PENDING_PICKUP, value);
        editor.commit();
    }

    public int getPendingPickup() {
        return sharedPreferences.getInt(Constants.SHARED_PREF_KEY_TOTAL_PENDING_PICKUP, 0);
    }

    public void setPendingDelivery(final int value) {
        editor.putInt(Constants.SHARED_PREF_KEY_TOTAL_PENDING_DELIVERY, value);
        editor.commit();
    }

    public int getPendingDelivery() {
        return sharedPreferences.getInt(Constants.SHARED_PREF_KEY_TOTAL_PENDING_DELIVERY, 0);
    }

    public void setTotalPendingArrivalAtHub(final int value) {
        editor.putInt(Constants.SHARED_PREF_KEY_TOTAL_PENDING_ARRIVAL_AT_HUB, value);
        editor.commit();
    }

    public int getTotalPendingArrivalAtHub() {
        return sharedPreferences.getInt(Constants.SHARED_PREF_KEY_TOTAL_PENDING_ARRIVAL_AT_HUB, 0);
    }

    public void setTodayPickup(final int value) {
        editor.putInt(Constants.SHARED_PREF_KEY_TOTAL_TODAY_PICKUP, value);
        editor.commit();
    }

    public int getTodayPickup() {
        return sharedPreferences.getInt(Constants.SHARED_PREF_KEY_TOTAL_TODAY_PICKUP, 0);
    }

    public void setTodayDelivery(final int value) {
        editor.putInt(Constants.SHARED_PREF_KEY_TOTAL_TODAY_DELIVERY, value);
        editor.commit();
    }

    public int getTodayDelivery() {
        return sharedPreferences.getInt(Constants.SHARED_PREF_KEY_TOTAL_TODAY_DELIVERY, 0);
    }

    public void setTodayCompleted(final int value) {
        editor.putInt(Constants.SHARED_PREF_KEY_TOTAL_TODAY_COMPLETED, value);
        editor.commit();
    }

    public int getTodayCompleted() {
        return sharedPreferences.getInt(Constants.SHARED_PREF_KEY_TOTAL_TODAY_COMPLETED, 0);
    }

    public void setTodayProblematic(final int value) {
        editor.putInt(Constants.SHARED_PREF_KEY_TOTAL_TODAY_PROBLEMATIC, value);
        editor.commit();
    }

    public int getTodayProblematic() {
        return sharedPreferences.getInt(Constants.SHARED_PREF_KEY_TOTAL_TODAY_PROBLEMATIC, 0);
    }

    public void setHistoryPickup(final int value) {
        editor.putInt(Constants.SHARED_PREF_KEY_TOTAL_HISTORY_PICKUP, value);
        editor.commit();
    }

    public int getHistoryPickup() {
        return sharedPreferences.getInt(Constants.SHARED_PREF_KEY_TOTAL_HISTORY_PICKUP, 0);
    }

    public void setHistoryDelivery(final int value) {
        editor.putInt(Constants.SHARED_PREF_KEY_TOTAL_HISTORY_DELIVERY, value);
        editor.commit();
    }

    public int getHistoryDelivery() {
        return sharedPreferences.getInt(Constants.SHARED_PREF_KEY_TOTAL_HISTORY_DELIVERY, 0);
    }

    public void setHistoryCompleted(final int value) {
        editor.putInt(Constants.SHARED_PREF_KEY_TOTAL_HISTORY_COMPLETED, value);
        editor.commit();
    }

    public int getHistoryCompleted() {
        return sharedPreferences.getInt(Constants.SHARED_PREF_KEY_TOTAL_HISTORY_COMPLETED, 0);
    }

    public void setHistoryProblematic(final int value) {
        editor.putInt(Constants.SHARED_PREF_KEY_TOTAL_HISTORY_PROBLEMATIC, value);
        editor.commit();
    }

    public int getHistoryProblematic() {
        return sharedPreferences.getInt(Constants.SHARED_PREF_KEY_TOTAL_HISTORY_PROBLEMATIC, 0);
    }
}
