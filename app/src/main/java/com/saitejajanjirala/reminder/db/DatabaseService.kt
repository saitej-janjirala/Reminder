package com.saitejajanjirala.reminder.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.saitejajanjirala.reminder.utils.Keys

@Database(entities = [Reminder::class], exportSchema = false, version = 1)
@TypeConverters(
    LongToCalenderConverter::class
)
abstract class DatabaseService : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: DatabaseService? = null

        fun getInstance(context: Context): DatabaseService =
            INSTANCE
                ?: synchronized(this) {
                    INSTANCE
                        ?: buildDatabase(
                            context
                        ).also {
                            INSTANCE = it
                        }
                }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                DatabaseService::class.java, Keys.DATABASE_NAME
            ).build()
    }

}