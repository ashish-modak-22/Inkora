/*
* This class is responsible for creating and upgrading the local SQLite database
*/


package com.example.notesapp.db


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.notesapp.db.NoteContract.COLUMN_CONTENT
import com.example.notesapp.db.NoteContract.COLUMN_CREATED_AT
import com.example.notesapp.db.NoteContract.COLUMN_ID
import com.example.notesapp.db.NoteContract.COLUMN_SYNC_STATUS
import com.example.notesapp.db.NoteContract.COLUMN_TITLE
import com.example.notesapp.db.NoteContract.COLUMN_UPDATED_AT
import com.example.notesapp.db.NoteContract.TABLE_NAME

class NoteDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "inkora.db"
        const val DATABASE_VERSION = 1

        private const val CREATE_NOTES_TABLE = """
            CREATE TABLE $TABLE_NAME (
               $COLUMN_ID INTEGER PRIMARY KEY,
               $COLUMN_TITLE TEXT NOT NULL,
               $COLUMN_CONTENT TEXT NOT NULL,
               $COLUMN_CREATED_AT TEXT,
               $COLUMN_UPDATED_AT TEXT,
               $COLUMN_SYNC_STATUS TEXT NOT NULL
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_NOTES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}