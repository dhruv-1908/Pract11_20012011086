package com.example.practical11

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import java.io.Serializable
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
enum class NoteMode{
    edit,
    add
}
class Note(var title:String, var subTitle:String, var Description:String, var modifiedTime:String, var isReminder: Boolean = false):Serializable
{
    var remindertime:Long = System.currentTimeMillis()
    var id = noteIdGeneration()
        set(value) {
            field = value
            if(idNote < value)
                idNote = value
        }

    constructor():this("","","","")
    {    }
    constructor(note: Note) : this(note.title,note.subTitle,note.Description,note.modifiedTime,note.isReminder) {
        remindertime = note.remindertime
    }

    fun isValid() :Boolean{
        if(title.isEmpty() || Description.isEmpty())
            return false
        return true
    }
    fun changeValue(newValue: Note)
    {
        title = newValue.title
        subTitle = newValue.subTitle
        Description = newValue.Description
        modifiedTime = newValue.modifiedTime
        isReminder = newValue.isReminder
        remindertime = newValue.remindertime
    }
    fun getReminderText() :String
    {
        return "Reminder: "+(SimpleDateFormat("MMM, dd yyyy hh:mm a") as DateFormat).format(
            Date(remindertime)
        )
    }
    fun saveNote(context: Context)
    {
        if(isReminder)
        {
            setReminder(context,this)
        }
    }
    fun getHour():Int{
        val cal = Calendar.getInstance()
        cal.time = Date(remindertime)
        return cal[Calendar.HOUR_OF_DAY]
    }
    fun getMinute():Int{
        val cal = Calendar.getInstance()
        cal.time = Date(remindertime)
        return cal[Calendar.MINUTE]
    }
    fun calcReminder()
    {
        if(remindertime < System.currentTimeMillis())
            isReminder = false
    }

    override fun toString(): String {
        return "$id\n"+title +"\n"+subTitle +"\n"+Description+"\nReminder:$isReminder" +"\n"+getReminderText()
    }


    companion object {
        var idNote = 0
        fun noteIdGeneration():Int
        {
            idNote++
            return idNote
        }
        val REMINDER_REQUEST_CODE = 1000
        val NOTE_ID_KEY = "Id"
        val NOTE_TITLE_KEY = "Title"
        val NOTE_SUBTITLE_KEY = "SubTitle"
        val NOTE_DESCRIPTION_KEY = "Description"
        val NOTE_MODIFIED_TIME_KEY = "ModifiedTime"
        val NOTE_REMINDER_TIME_KEY = "ReminderTime"

        fun getCurrentDateTime(): String {
            val cal = Calendar.getInstance()
            val df: DateFormat = SimpleDateFormat("MMM, dd yyyy hh:mm:ss a")
            return df.format(cal.time)
        }
        fun getMillis(hour:Int,min:Int):Long
        {
            val setcalendar = Calendar.getInstance()
            setcalendar[Calendar.HOUR_OF_DAY] = hour
            setcalendar[Calendar.MINUTE] = min
            setcalendar[Calendar.SECOND] = 0
            return setcalendar.timeInMillis
        }

        fun setReminder(context: Context, note: Note) {
            val intent = Intent(context, AlarmBroadcastReceiver::class.java)
            intent.putExtra(NOTE_ID_KEY, note.id)
            intent.putExtra(NOTE_TITLE_KEY, note.title)
            intent.putExtra(NOTE_SUBTITLE_KEY, note.subTitle)
            intent.putExtra(NOTE_DESCRIPTION_KEY, note.Description)
            intent.putExtra(NOTE_MODIFIED_TIME_KEY, note.modifiedTime)
            intent.putExtra(NOTE_REMINDER_TIME_KEY, note.remindertime)

            val pendingIntent =
                PendingIntent.getBroadcast(context, note.id, intent, PendingIntent.FLAG_CANCEL_CURRENT)
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

            if(note.isReminder) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    note.remindertime,
                    pendingIntent
                )
            }
            else
                alarmManager.cancel(pendingIntent)
        }
    }
}