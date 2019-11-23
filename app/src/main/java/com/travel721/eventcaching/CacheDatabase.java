package com.travel721.eventcaching;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.travel721.card.EventCard;

/**
 * This class provides access to the Cache Database
 */
@Database(entities = {EventCard.class}, version = 2)
@TypeConverters(com.travel721.eventcaching.TypeConverters.class)
public abstract class CacheDatabase extends RoomDatabase {
    // Singleton class structure for database access
    private static CacheDatabase thisInstance = null;

    /*
     * As per guidelines, it is less expensive to keep
     * one database connection alive; than it is to continually
     * open and close connections. This explains why 'leaks' may occur
     */

    public static CacheDatabase getInstance(Context context) {
        return thisInstance == null ? Room.databaseBuilder(context, CacheDatabase.class, "cache-database").fallbackToDestructiveMigration().build() : thisInstance;
    }

    public abstract EventCardDao eventCardDao();
}