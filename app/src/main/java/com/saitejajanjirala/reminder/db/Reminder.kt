package com.saitejajanjirala.reminder.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(tableName = "reminders")
data class Reminder (
    @PrimaryKey
    val id :Long,
    val name :String,
    val time : Calendar,
    var shown : Boolean,
) : Serializable{

}
