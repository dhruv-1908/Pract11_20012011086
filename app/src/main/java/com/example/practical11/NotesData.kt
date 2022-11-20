package com.example.practical11

class NotesData {
    companion object{
        const val TABLE_NAME = "notes"

        const val COLUMN_ID = "id"
        const val COLUMN_NOTE_TITLE = "note_title"
        const val COLUMN_NOTE_SUB_TITLE = "note_sub_title"
        const val COLUMN_NOTE_DESCRIPTION = "note_description"
        const val COLUMN_NOTE_SET_REMINDER = "note_set_reminder"
        const val COLUMN_NOTE_REMINDER_TIME = "note_set_reminder_time"
        const val COLUMN_TIMESTAMP = "note_modified_timestamp"

        // Create table SQL query
        val CREATE_TABLE = ("CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOTE_TITLE + " TEXT,"
                + COLUMN_NOTE_SUB_TITLE + " TEXT,"
                + COLUMN_NOTE_DESCRIPTION + " TEXT,"
                + COLUMN_NOTE_SET_REMINDER + " INTEGER,"
                + COLUMN_NOTE_REMINDER_TIME + " INTEGER,"
                + COLUMN_TIMESTAMP + " TEXT"
                + ")")
    }
}