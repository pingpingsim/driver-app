package com.pingu.driverapp.data.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pingu.driverapp.data.model.local.PendingTask;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface PendingTaskDao {
    @Query("SELECT * FROM tbl_pending_task")
    Single<List<PendingTask>> getAllTask();

    @Query("DELETE FROM tbl_pending_task")
    void deleteAllPendingTask();

    @Query("SELECT * FROM tbl_pending_task WHERE parcel_id = :parcelId")
    Single<PendingTask> getTaskByParcelId(String parcelId);

    @Query("DELETE FROM tbl_pending_task WHERE parcel_id = :parcelId")
    void deleteTaskByParcelId(String parcelId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(PendingTask... pendingTasks);

    @Update
    public void updateTask(PendingTask... pendingTasks);
}
