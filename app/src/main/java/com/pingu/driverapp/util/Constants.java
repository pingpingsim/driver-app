package com.pingu.driverapp.util;

public interface Constants {
    //Shared preferences
    public static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    public static final String PREF_NAME = "DriverAppCache";
    public static final String SHARED_PREF_KEY_TOKEN = "token";
    public static final String SHARED_PREF_KEY_PROFILE = "profile";

    //Shared preference for dashboard data
    public static final String SHARED_PREF_KEY_TOTAL_AVAILABLE_ADDRESS = "AvailableAddress";
    public static final String SHARED_PREF_KEY_TOTAL_PENDING_PICKUP = "PendingPickup";
    public static final String SHARED_PREF_KEY_TOTAL_PENDING_DELIVERY = "PendingDelivery";
    public static final String SHARED_PREF_KEY_TOTAL_PENDING_ARRIVAL_AT_HUB = "PendingArrivalAtHub";
    public static final String SHARED_PREF_KEY_TOTAL_TODAY_PICKUP = "TodayPickup";
    public static final String SHARED_PREF_KEY_TOTAL_TODAY_DELIVERY = "TodayDelivery";
    public static final String SHARED_PREF_KEY_TOTAL_TODAY_COMPLETED = "TodayCompleted";
    public static final String SHARED_PREF_KEY_TOTAL_TODAY_PROBLEMATIC = "TodayProblematic";
    public static final String SHARED_PREF_KEY_TOTAL_HISTORY_PICKUP = "HistoryPickup";
    public static final String SHARED_PREF_KEY_TOTAL_HISTORY_DELIVERY = "HistoryDelivery";
    public static final String SHARED_PREF_KEY_TOTAL_HISTORY_COMPLETED = "HistoryCompleted";
    public static final String SHARED_PREF_KEY_TOTAL_HISTORY_PROBLEMATIC = "HistoryProblematic";

    // Extra key
    public static final String INTENT_EXTRA_TASK_COMPLETED_TYPE = "IntentExtraTaskCompletedType";
    public static final String INTENT_EXTRA_TASK_HISTORY_TYPE = "IntentExtraTaskHistoryType";
    public static final String INTENT_EXTRA_BARCODE_PARCEL_ID = "Scan";
    public static final String INTENT_EXTRA_PARCEL_DELIVERED_SIGNATURE = "IntentExtraSignature";
    public static final String INTENT_EXTRA_DELIVERY_ITEM = "DeliveryItem";
    public static final String INTENT_EXTRA_TASK_ACTION_TYPE = "IntentExtraTaskActionType";
    public static final String INTENT_EXTRA_PARCEL_DELIVERED_PHOTO = "IntentExtraPhoto";
    public static final String INTENT_EXTRA_PARCEL_ID = "IntentExtraParcelID";
    public static final String INTENT_EXTRA_RECEIVER_NAME = "IntentExtraReceiverName";
    public static final String INTENT_EXTRA_SUCCESS = "Success";

    public static final String ALL_TASK_CHANGE_INTENT = "";
    public static final String AVAILABLE_TASK_CHANGE_INTENT = "";
    public static final String MY_TASK_CHANGE_INTENT = "";

    // API response status code
    public static final int API_STATUS_FAILURE = 0;
    public static final int API_STATUS_SUCCESS = 1;

    public static final String PARCEL_STATUS_PICKUP = "pickup";
    public static final String PARCEL_STATUS_OUT_FOR_DELIVERY = "out_for_deliver";
    public static final String PARCEL_STATUS_COMPLETE = "complete";

    public static final int SYNC_INTERVAL_IN_MINUTES = 15;

    public static final int TYPE_PICKUP = 1;
    public static final int TYPE_DELIVERY = 2;
    public static final int TYPE_DELIVERY_AND_SIGNATURE = 3;
    public static final int TYPE_PROBLEMATIC_PARCEL = 4;

    public static final String PERIODIC_PENDING_TASK_CHECKER = "PeriodicPendingTaskChecker";
}