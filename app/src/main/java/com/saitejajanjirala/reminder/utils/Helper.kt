package com.saitejajanjirala.reminder.utils

import com.google.gson.Gson
import com.saitejajanjirala.reminder.db.Reminder

object Helper {
    fun reminderToString(reminder: Reminder): String {
        val  gson = Gson()
        return gson.toJson(reminder)
    }

    fun stringToReminder(str : String) : Reminder{
        val gson = Gson()
        return gson.fromJson(str,Reminder::class.java)
    }
}