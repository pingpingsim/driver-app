package com.pingu.driverapp.data.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pingu.driverapp.data.model.local.PendingPickupOrder;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface PendingPickupOrderDao {
    @Query("SELECT * FROM tbl_pending_pickup_order")
    Single<List<PendingPickupOrder>> getAllPendingPickupOrder();

    @Query("DELETE FROM tbl_pending_pickup_order")
    void deleteAllPendingPickupOrder();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPendingPickupOrder(PendingPickupOrder... pendingPickupOrders);
}
