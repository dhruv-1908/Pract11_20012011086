package com.example.practical11

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.Serializable

class AlarmBroadcastReceiver : BroadcastReceiver() {
    val TAG = "AlarmBroadcastReceiver"
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent != null && context!=null) {
            val note = Note()
            note.id = intent.getIntExtra(Note.NOTE_ID_KEY, -1)
            note.title = intent.getStringExtra(Note.NOTE_TITLE_KEY)!!
            note.subTitle = intent.getStringExtra(Note.NOTE_SUBTITLE_KEY)!!
            note.Description = intent.getStringExtra(Note.NOTE_DESCRIPTION_KEY)!!
            note.modifiedTime = intent.getStringExtra(Note.NOTE_MODIFIED_TIME_KEY)!!
            note.remindertime = intent.getLongExtra(Note.NOTE_REMINDER_TIME_KEY, 0)
            note.isReminder = true
            Log.i(TAG, "onReceive: Note: $note")
            notificationDialog(
                context,
                NoteViewActivity::class.java,
                note.title,
                note.Description,
                note
            )
        }
    }
    private fun notificationDialog(context: Context, cls: Class<*>, title: String, descr: String,note: Note) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_CHANNEL_ID = "hitensadani"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Note Application", NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = "Note Application description"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notificationIntent = Intent(context, cls)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        notificationIntent.putExtra("message", "This is a notification message")
        notificationIntent.putExtra("Object", note as Serializable)
        val pendingIntent = PendingIntent.getActivity(
            context, Note.REMINDER_REQUEST_CODE, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setTicker("NoteApplication-Nirmal Patel")
            .setContentTitle(title)
            .setContentText(descr)
            .setContentInfo(descr + "Information")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        notificationManager.notify(1, notificationBuilder.build())
    }
}