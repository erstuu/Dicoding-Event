package com.restugedepurnama.event.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.restugedepurnama.event.data.local.entity.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM event WHERE isActive = 0 ORDER BY beginTime DESC")
    fun getFinishedEvent(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event WHERE isActive = 1 ORDER BY beginTime DESC")
    fun getUpcomingEvent(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event WHERE id = :id LIMIT 1")
    suspend fun getEventById(id: String): EventEntity?

    @Query("SELECT * FROM event WHERE isActive = :active AND name LIKE '%' || :query || '%' ORDER BY beginTime ASC")
    fun searchEvent(active: Int, query: String): LiveData<List<EventEntity>>

    @Query("SELECT EXISTS(SELECT * FROM event WHERE id = :id AND favorite = 1)")
    suspend fun isEventFavorite(id: String): Boolean

    @Query("SELECT * FROM event where favorite = 1")
    fun getFavoriteEvent(): LiveData<List<EventEntity>>

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Query("DELETE FROM event WHERE isActive = 0")
    suspend fun deleteInactiveEvents()

    @Query("DELETE FROM event WHERE isActive = 1")
    suspend fun deleteActiveEvents()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEvent(event: List<EventEntity>)

    @Query("SELECT * FROM event WHERE isActive = 1 AND date(beginTime) >= :currentTime ORDER BY date(beginTime) ASC LIMIT 1")
    suspend fun getNearestActiveEvent(currentTime: Long): EventEntity?
}