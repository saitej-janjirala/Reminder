package com.saitejajanjirala.reminder.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.util.*

@Entity(tableName = "reminders")
data class Reminder (
    @PrimaryKey
    var id :Long,
    var name :String,
    var time : Long,
    var shown : Boolean,
) : Serializable{

}
