package com.example.practical11

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.ArrayList

class DatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        // Database Version
        private const val DATABASE_VERSION = 1
        // Database Name
        private const val DATABASE_NAME = "notes_db"
    }
    // Creating Tables
    override fun onCreate(db: SQLiteDatabase) {
        // create notes table
        db.execSQL(NotesData.CREATE_TABLE)
    }
    // Upgrading database
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + NotesData.TABLE_NAME)
        // Create tables again
        onCreate(db)
    }
    fun insertNote(note: Note): Long {
        // get writable database as we want to write data
        val db = this.writableDatabase
        // insert row
        val id = db.insert(NotesData.TABLE_NAME, null, getValues(note))
        // close db connection
        db.close()
        // return newly inserted row id
        return id
    }
    private fun getValues(note: Note): ContentValues {
        val values = ContentValues()
        // `id` will be inserted automatically.
        // no need to add them
        values.put(NotesData.COLUMN_NOTE_TITLE, note.title)
        values.put(NotesData.COLUMN_NOTE_SUB_TITLE, note.subTitle)
        values.put(NotesData.COLUMN_NOTE_DESCRIPTION, note.Description)
        values.put(NotesData.COLUMN_NOTE_REMINDER_TIME, note.remindertime)
        values.put(NotesData.COLUMN_NOTE_SET_REMINDER, note.isReminder)
        values.put(NotesData.COLUMN_TIMESTAMP, note.modifiedTime)
        return values
    }
    fun getNote(id: Long): Note {
        // get readable database as we are not inserting anything
        val db = this.readableDatabase
        val cursor = db.query(
            NotesData.TABLE_NAME,
            arrayOf(
                NotesData.COLUMN_ID,
                NotesData.COLUMN_NOTE_TITLE,
                NotesData.COLUMN_NOTE_SUB_TITLE,
                NotesData.COLUMN_NOTE_DESCRIPTION,
                NotesData.COLUMN_NOTE_SET_REMINDER,
                NotesData.COLUMN_NOTE_REMINDER_TIME,
                NotesData.COLUMN_TIMESTAMP
            ),
            NotesData.COLUMN_ID.toString() + "=?",
            arrayOf(id.toString()),
            null,
            null,
            null,
            null
        )
        cursor?.moveToFirst()
        val note = getNote(cursor)

        // close the db connection
        cursor!!.close()
        return note
    }
    private fun getNote(cursor: Cursor): Note
    {
        // prepare note object
        val note = Note(
            cursor.getString(cursor.getColumnIndexOrThrow(NotesData.COLUMN_NOTE_TITLE)),
            cursor.getString(cursor.getColumnIndexOrThrow(NotesData.COLUMN_NOTE_SUB_TITLE)),
            cursor.getString(cursor.getColumnIndexOrThrow(NotesData.COLUMN_NOTE_DESCRIPTION)),
            cursor.getString(cursor.getColumnIndexOrThrow(NotesData.COLUMN_TIMESTAMP))
        )
        note.isReminder = (cursor.getInt(cursor.getColumnIndexOrThrow(NotesData.COLUMN_NOTE_SET_REMINDER))==1)
        note.remindertime = cursor.getLong(cursor.getColumnIndexOrThrow(NotesData.COLUMN_NOTE_REMINDER_TIME))
        note.id = cursor.getInt(cursor.getColumnIndexOrThrow(NotesData.COLUMN_ID))
        return note
    }
    // Select All Query
    val allNotes: ArrayList<Note>
        get() {
            val notes = ArrayList<Note>()
            // Select All Query
            val selectQuery = "SELECT  * FROM " + NotesData.TABLE_NAME.toString() + " ORDER BY " +
                    NotesData.COLUMN_TIMESTAMP.toString() + " DESC"
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    notes.add(getNote(cursor))
                } while (cursor.moveToNext())
            }
            // close db connection
            db.close()
            // return notes list
            return notes
        }
    val notesCount: Int
        get() {
            val countQuery = "SELECT  * FROM " + NotesData.TABLE_NAME
            val db = this.readableDatabase
            val cursor = db.rawQuery(countQuery, null)
            val count = cursor.count
            cursor.close()
            // return count
            return count
        }
    fun updateNote(note: Note): Int {
        val db = this.writableDatabase
        // updating row
        return db.update(
            NotesData.TABLE_NAME,
            getValues(note),
            NotesData.COLUMN_ID + " = ?",
            arrayOf(note.id.toString())
        )
    }
    fun deleteNote(note: Note) {
        val db = this.writableDatabase
        db.delete(
            NotesData.TABLE_NAME,
            NotesData.COLUMN_ID + " = ?",
            arrayOf(note.id.toString())
        )
        db.close()
    }
}