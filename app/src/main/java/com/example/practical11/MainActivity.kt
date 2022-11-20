package com.example.practical11

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practical11.databinding.ActivityMainBinding
import com.example.practical11.databinding.NoteEditViewBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"
    private var listener: ((note: Note, baseListAdapter: NotesAdapter, mode: NoteMode, position: Int)->Unit)? =
        { note: Note, _: NotesAdapter, noteMode: NoteMode, pos: Int ->
            note.modifiedTime = Note.getCurrentDateTime()
            if (noteMode == NoteMode.add) {
                if (!createNote(note))
                    Toast.makeText(this, "Enter Valid Note", Toast.LENGTH_SHORT).show()
            } else if (noteMode == NoteMode.edit) {
                Log.i(TAG, "listener: Note:$note")
                if (!updateNote(note, pos))
                    Toast.makeText(this, "Enter Valid Note", Toast.LENGTH_SHORT).show()
            }
        }
    lateinit var db: DatabaseHelper
    private val notesList: ArrayList<Note> = ArrayList<Note>()
    lateinit var notesRecycleAdapter: NotesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)
        notesList.addAll(db.allNotes)

        notesRecycleAdapter = NotesAdapter(this, notesList)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        binding.listView1.layoutManager = mLayoutManager
        binding.listView1.itemAnimator = DefaultItemAnimator()
        binding.listView1.setHasFixedSize(true)
        binding.listView1.adapter = notesRecycleAdapter
        setSupportActionBar(binding.toolbar)
        binding.fab.setOnClickListener {
            showAlertDialog(
                NoteMode.add, "Add Note",
                Note("", "", "", Note.getCurrentDateTime()), -1, notesRecycleAdapter
            )
        }
    }
    private fun createNote(note: Note):Boolean {
        if(!note.isValid())
            return false
        // inserting note in db and getting
        // newly inserted note id
        val id = db.insertNote(note)

        // get the newly inserted note from db
        val n: Note = db.getNote(id)
        // adding new note to array list at 0 position
        notesList.add(0, n)

        // refreshing the list
        notesRecycleAdapter.notifyItemInserted(0)
        note.saveNote(this)
        return true
    }
    private fun updateNote(note: Note, position: Int):Boolean {
        if(!note.isValid())
            return false
        val n: Note = notesList[position]
        n.changeValue(note)
        Log.i(TAG, "updateNote: note:: $n")
        // updating note in db
        db.updateNote(n)

        // refreshing the list
        notesList[position] = n
        notesRecycleAdapter.notifyItemChanged(position)
        note.saveNote(this)
        return true
    }
    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    fun deleteNote(position: Int) {
        // deleting the note from db
        db.deleteNote(notesList[position])

        // removing the note from the list
        notesList.removeAt(position)
        notesRecycleAdapter.notifyItemRemoved(position)
    }
    fun showAlertDialog(
        mode: NoteMode,
        dialogTitle: String,
        note: Note,
        position: Int,
        baseListAdapter: NotesAdapter
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        // set the custom layout
        val binding = NoteEditViewBinding.inflate(LayoutInflater.from(this))
        binding.noteTitle.setText(note.title)
        binding.noteSubTitle.setText(note.subTitle)
        binding.noteDescription.setText(note.Description)
        binding.reminderSwitch.isChecked = note.isReminder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.reminderTime.hour = note.getHour()
            binding.reminderTime.minute = note.getMinute()
        }
        else{
            binding.reminderTime.currentHour = note.getHour()
            binding.reminderTime.currentMinute = note.getMinute()
        }

        builder.setView(binding.root)
        // add a button
        builder.setPositiveButton(
            "OK"
        ) { _, _ ->
            val newNote = Note(note)
            newNote.title = binding.noteTitle.text.toString()
            newNote.subTitle = binding.noteSubTitle.text.toString()
            newNote.Description = binding.noteDescription.text.toString()
            newNote.isReminder = binding.reminderSwitch.isChecked

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                newNote.remindertime =
                    Note.getMillis(binding.reminderTime.hour, binding.reminderTime.minute)
            else
                newNote.remindertime = Note.getMillis(
                    binding.reminderTime.currentHour,
                    binding.reminderTime.currentMinute
                )
            Log.i(TAG, "showAlertDialog: OK Button:: Note:$newNote")
            listener?.invoke(newNote, baseListAdapter, mode, position)
        }
        // create and show the alert dialog
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}