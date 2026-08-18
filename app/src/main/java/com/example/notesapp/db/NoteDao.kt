/*
*  This file will execute all the necessary database operations
*/


package com.example.notesapp.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.notesapp.db.NoteContract.COLUMN_CONTENT
import com.example.notesapp.db.NoteContract.COLUMN_CREATED_AT
import com.example.notesapp.db.NoteContract.COLUMN_ID
import com.example.notesapp.db.NoteContract.COLUMN_SYNC_STATUS
import com.example.notesapp.db.NoteContract.COLUMN_TITLE
import com.example.notesapp.db.NoteContract.COLUMN_UPDATED_AT
import com.example.notesapp.db.NoteContract.SyncStatus
import com.example.notesapp.db.NoteContract.TABLE_NAME


class NoteDao(context: Context) {

    private val dbHelper = NoteDbHelper(context.applicationContext)

    // Inserts a note, or overwrites it if a row with the same id already exists.
    fun upsert(note: NoteEntity) {
        val db = dbHelper.writableDatabase
        db.insertWithOnConflict(
            TABLE_NAME,
            null,
            note.toContentValues(),
            android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    // Convenience for caching a fresh page of results from the server. Only touches rows
    // that are already fully synced, so it never clobbers something the user edited offline.
    fun upsertSyncedNotes(notes: List<NoteEntity>) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            for (note in notes) {
                db.insertWithOnConflict(
                    TABLE_NAME,
                    null,
                    note.toContentValues(),
                    android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getAll(
        search: String? = null,
        sortBy: String = "created_at",
        order: String = "desc"
    ): List<NoteEntity> {
        val db = dbHelper.readableDatabase

        val selection: String?
        val selectionArgs: Array<String>?
        if (search.isNullOrBlank()) {
            selection = "$COLUMN_SYNC_STATUS != ?"
            selectionArgs = arrayOf(SyncStatus.PENDING_DELETE)
        } else {
            selection = "$COLUMN_SYNC_STATUS != ? AND ($COLUMN_TITLE LIKE ? OR $COLUMN_CONTENT LIKE ?)"
            val likeTerm = "%$search%"
            selectionArgs = arrayOf(SyncStatus.PENDING_DELETE, likeTerm, likeTerm)
        }

        val sortColumn = if (sortBy == "title") COLUMN_TITLE else COLUMN_CREATED_AT
        val sortOrder = if (order == "asc") "ASC" else "DESC"

        val cursor = db.query(
            TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            "$sortColumn COLLATE NOCASE $sortOrder"
        )

        return cursor.use { readAll(it) }
    }

    fun getPendingSync(): List<NoteEntity> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_SYNC_STATUS != ?",
            arrayOf(SyncStatus.SYNCED),
            null,
            null,
            null
        )
        return cursor.use { readAll(it) }
    }

    fun getById(id: Int): NoteEntity? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        return cursor.use {
            if (it.moveToFirst()) it.toNoteEntity() else null
        }
    }

    // Permanently removes a row, e.g. once a pending-delete has been confirmed by the server,
    // or when discarding a note that was never synced in the first place.
    fun deletePermanently(id: Int) {
        val db = dbHelper.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // Used after a successful create-sync to swap the temporary local id for the real server id.
    fun replaceId(oldId: Int, newNote: NoteEntity) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(oldId.toString()))
            db.insertWithOnConflict(
                TABLE_NAME,
                null,
                newNote.toContentValues(),
                android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
            )
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun readAll(cursor: Cursor): List<NoteEntity> {
        val results = mutableListOf<NoteEntity>()
        while (cursor.moveToNext()) {
            results.add(cursor.toNoteEntity())
        }
        return results
    }

    private fun Cursor.toNoteEntity(): NoteEntity {
        return NoteEntity(
            id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
            title = getString(getColumnIndexOrThrow(COLUMN_TITLE)),
            content = getString(getColumnIndexOrThrow(COLUMN_CONTENT)),
            createdAt = getString(getColumnIndexOrThrow(COLUMN_CREATED_AT)) ?: "",
            updatedAt = getString(getColumnIndexOrThrow(COLUMN_UPDATED_AT)) ?: "",
            syncStatus = getString(getColumnIndexOrThrow(COLUMN_SYNC_STATUS))
        )
    }

    private fun NoteEntity.toContentValues(): ContentValues {
        return ContentValues().apply {
            put(COLUMN_ID, id)
            put(COLUMN_TITLE, title)
            put(COLUMN_CONTENT, content)
            put(COLUMN_CREATED_AT, createdAt)
            put(COLUMN_UPDATED_AT, updatedAt)
            put(COLUMN_SYNC_STATUS, syncStatus)
        }
    }
}
