package com.pingu.driverapp.data.model.local;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_pending_task",
        indices = {@Index(value = {"parcel_id"}, unique = true)})
public class PendingTask {
    public static final int ACTION_TYPE_ACCEPT_TASK = 1;
    public static final int ACTION_TYPE_REMOVE_TASK = 2;
    public static final int ACTION_TYPE_OPERATION_PICKUP = 3;
    public static final int ACTION_TYPE_OPERATION_OUT_FOR_DELIVERY = 4;
    public static final int ACTION_TYPE_OPERATION_COMPLETED = 5;
    public static final int ACTION_TYPE_OPERATION_PROBLEMATIC_PARCEL = 6;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @NonNull
    private Integer id;

    @ColumnInfo(name = "parcel_id")
    private String parcelId;

    @ColumnInfo(name = "recipient_ic")
    private String recipientIc;

    @ColumnInfo(name = "recipient_name")
    private String recipientName;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "status_date_time")
    private String statusDateTime;

    @ColumnInfo(name = "problematic_status_id")
    private int problematicStatusId;

    @ColumnInfo(name = "signature_file_name")
    private String signatureFileName;

    @ColumnInfo(name = "photo_file_name")
    private String photoFileName;

    @ColumnInfo(name = "action_type")
    private int actionType;

    @ColumnInfo(name = "user_id")
    private int userId;

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public String getParcelId() {
        return parcelId;
    }

    public void setParcelId(String parcelId) {
        this.parcelId = parcelId;
    }

    public String getRecipientIc() {
        return recipientIc;
    }

    public void setRecipientIc(String recipientIc) {
        this.recipientIc = recipientIc;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDateTime() {
        return statusDateTime;
    }

    public void setStatusDateTime(String statusDateTime) {
        this.statusDateTime = statusDateTime;
    }

    public int getProblematicStatusId() {
        return problematicStatusId;
    }

    public void setProblematicStatusId(int problematicStatusId) {
        this.problematicStatusId = problematicStatusId;
    }

    public String getSignatureFileName() {
        return signatureFileName;
    }

    public void setSignatureFileName(String signatureFileName) {
        this.signatureFileName = signatureFileName;
    }

    public String getPhotoFileName() {
        return photoFileName;
    }

    public void setPhotoFileName(String photoFileName) {
        this.photoFileName = photoFileName;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
