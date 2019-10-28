package com.travel721;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {EventCard.class}, version = 1)
public abstract class CacheDatabase extends RoomDatabase {
    public abstract EventCardDao eventCardDao();

    static CacheDatabase thisInstance = null;


    public static CacheDatabase getInstance(Context context) {
        return thisInstance == null ? Room.databaseBuilder(context, CacheDatabase.class, "database-name").build() : thisInstance;
    }
}