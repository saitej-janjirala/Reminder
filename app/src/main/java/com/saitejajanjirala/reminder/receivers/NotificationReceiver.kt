package com.saitejajanjirala.reminder.receivers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.saitejajanjirala.reminder.R
import com.saitejajanjirala.reminder.db.DatabaseService
import com.saitejajanjirala.reminder.db.Reminder
import com.saitejajanjirala.reminder.services.NotificationService
import com.saitejajanjirala.reminder.ui.MainActivity
import com.saitejajanjirala.reminder.utils.Keys
import kotlin.random.Random

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if( p1 != null ) {
            Log.i("broadcast received","true")
            val intent = Intent(p0!!,NotificationService::class.java)
            intent.putExtras(p1)
            p0.startService(intent)
        }
    }

}