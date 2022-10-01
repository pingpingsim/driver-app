package com.pingu.driverapp.data.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pingu.driverapp.data.model.local.PendingDeliveryOrder;
import com.pingu.driverapp.data.model.local.PendingPickupOrder;
import com.pingu.driverapp.data.model.local.PendingTask;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface PendingDeliveryOrderDao {
    @Query("SELECT * FROM tbl_pending_delivery_order")
    Single<List<PendingDeliveryOrder>> getAllPendingDeliveryOrder();

    @Query("DELETE FROM tbl_pending_delivery_order")
    void deleteAllPendingDeliveryOrder();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPendingDeliveryOrder(PendingDeliveryOrder... pendingDeliveryOrders);
}
