package com.saitejajanjirala.reminder.db

import androidx.room.TypeConverter
import java.util.*

class LongToCalenderConverter {
    @TypeConverter
    fun longToCalender(calendar: Calendar) : Long{
        return calendar.timeInMillis
    }

    @TypeConverter
    fun calendarToLong( time : Long) : Calendar{
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        return calendar
    }
}