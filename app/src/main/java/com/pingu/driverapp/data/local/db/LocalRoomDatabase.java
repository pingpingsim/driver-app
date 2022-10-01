package com.pingu.driverapp.data.local.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.pingu.driverapp.data.local.db.dao.PendingDeliveryOrderDao;
import com.pingu.driverapp.data.local.db.dao.PendingPickupOrderDao;
import com.pingu.driverapp.data.local.db.dao.PendingTaskDao;
import com.pingu.driverapp.data.model.local.PendingDeliveryOrder;
import com.pingu.driverapp.data.model.local.PendingPickupOrder;
import com.pingu.driverapp.data.model.local.PendingTask;

@Database(entities =
        {PendingTask.class, PendingPickupOrder.class, PendingDeliveryOrder.class},
        version = 1, exportSchema = false)
public abstract class LocalRoomDatabase extends RoomDatabase {

    public abstract PendingTaskDao pendingTaskDao();

    public abstract PendingPickupOrderDao pendingPickupOrderDao();

    public abstract PendingDeliveryOrderDao pendingDeliveryOrderDao();

    private static LocalRoomDatabase instance;

    public static LocalRoomDatabase getDatabase(Context context) {
        if (instance == null) {
            synchronized (LocalRoomDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            LocalRoomDatabase.class, "driverapp")
                            .build();
                }
            }
        }
        return instance;
    }

//    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {                             //.addMigrations(MIGRATION_1_2)
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE 'tbl_pending_task' ADD COLUMN 'is_synced' INTEGER DEFAULT 0 NOT NULL;");
//        }
//    };
}
