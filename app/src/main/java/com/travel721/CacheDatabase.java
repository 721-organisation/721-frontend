package com.travel721;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * This class provides access to the Cache Database
 */
@Database(entities = {EventCard.class}, version = 1)
public abstract class CacheDatabase extends RoomDatabase {
    // Singleton class structure for database access
    private static CacheDatabase thisInstance = null;

    /*
     * As per guidelines, it is less expensive to keep
     * one database connection alive; than it is to continually
     * open and close connections. This explains why 'leaks' may occur
     */

    public static CacheDatabase getInstance(Context context) {
        return thisInstance == null ? Room.databaseBuilder(context, CacheDatabase.class, "cache-database").build() : thisInstance;
    }

    public abstract EventCardDao eventCardDao();
}