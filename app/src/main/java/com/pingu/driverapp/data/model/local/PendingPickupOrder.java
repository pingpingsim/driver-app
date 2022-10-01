package com.pingu.driverapp.data.model.local;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_pending_pickup_order")
public class PendingPickupOrder {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @NonNull
    private Integer id;

    @ColumnInfo(name = "order")
    private Integer order;

    @ColumnInfo(name = "sender_address")
    private String senderAddress;

    @ColumnInfo(name = "sender_contact_number")
    private String senderContactNumber;

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getSenderContactNumber() {
        return senderContactNumber;
    }

    public void setSenderContactNumber(String senderContactNumber) {
        this.senderContactNumber = senderContactNumber;
    }
}
