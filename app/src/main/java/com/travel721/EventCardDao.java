package com.travel721;

import android.provider.ContactsContract;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EventCardDao {

    @Query("SELECT * FROM eventcard")
    List<EventCard> getAll();


    @Insert
    void insertAll(EventCard... eventCards);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EventCard eventCards);

    @Delete
    void delete(EventCard eventCard);
}
