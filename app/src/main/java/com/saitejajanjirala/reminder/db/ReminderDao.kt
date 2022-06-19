package com.saitejajanjirala.reminder.db

import androidx.room.*

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(reminder: Reminder) : Long

    @Query("SELECT * FROM reminders ORDER BY id DESC")
    fun retrieveAllReminders() : List<Reminder>

    @Update
    fun updateReminder(reminder: Reminder) : Int
}