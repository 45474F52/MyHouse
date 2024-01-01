package com.aes.myhome.storage.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aes.myhome.storage.database.entities.ScheduleEvent

@Dao
interface ScheduleEventDAO {
    @Query("SELECT * FROM ScheduleEvent")
    suspend fun getAll(): List<ScheduleEvent>

    @Query("SELECT * FROM ScheduleEvent WHERE eventId = :id")
    suspend fun findById(id: Int) : ScheduleEvent?

    @Query("SELECT * FROM ScheduleEvent WHERE category LIKE :category")
    suspend fun getByCategory(category: String) : List<ScheduleEvent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(vararg events: ScheduleEvent)

    @Update
    suspend fun updateEvents(vararg events: ScheduleEvent)

    @Delete
    suspend fun deleteEvents(vararg events: ScheduleEvent)
}