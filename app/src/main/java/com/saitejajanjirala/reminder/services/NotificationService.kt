package com.saitejajanjirala.reminder.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.saitejajanjirala.reminder.R
import com.saitejajanjirala.reminder.db.DatabaseService
import com.saitejajanjirala.reminder.db.Reminder
import com.saitejajanjirala.reminder.ui.MainActivity
import com.saitejajanjirala.reminder.utils.Helper
import com.saitejajanjirala.reminder.utils.Keys
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class NotificationService : IntentService("notification service") {

    override fun onHandleIntent(p0: Intent?) {
        scheduleNotification(p0!!)
    }
    private fun scheduleNotification(intent: Intent) {
        val remStr = intent.getStringExtra(Keys.REMINDER_EXTRA)
        val reminder = Helper.stringToReminder(remStr!!)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this,0,
                Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(this,0,
                Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
        }

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            Log.i("entered O condition","true")
            val channel= NotificationChannel(
                Keys.CHANNEL_ID, Keys.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableLights(false)
            channel.enableVibration(true)
            channel.lockscreenVisibility= Notification.VISIBILITY_PRIVATE
            notificationManager.createNotificationChannel(channel)

            val builder: NotificationCompat.Builder= NotificationCompat.Builder(this, Keys.CHANNEL_ID)
            val notification = builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(reminder.name)
                .setContentText("reminder for ${getTimeInSimpleDateFormat(reminder)}")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent).build()
            NotificationManagerCompat.from(this).notify(Random(3).nextInt(),notification)
        }
        else{
            val builder: NotificationCompat.Builder= NotificationCompat.Builder(this)

            val notification = builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(reminder.name)
                .setContentText("reminder for ${getTimeInSimpleDateFormat(reminder)}")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent).build()
            NotificationManagerCompat.from(this).notify(Random(3).nextInt(),notification)
        }
        updateReminder(reminder)
    }

    private fun updateReminder(reminder: Reminder) {
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        val appContext = this.applicationContext
        scope.launch {
            val dao =   DatabaseService.getInstance(appContext).reminderDao()
            reminder.shown = true
            dao.updateReminder(reminder)
        }
    }

    private fun getTimeInSimpleDateFormat(reminder: Reminder): String? {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(reminder.time)
    }


}