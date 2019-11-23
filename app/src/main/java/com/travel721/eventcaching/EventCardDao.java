package com.travel721.eventcaching;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.travel721.card.EventCard;

import java.util.List;

/**
 * Room interface to accessing EventCards
 * Provides compile-time safety for SQL queries
 */
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
