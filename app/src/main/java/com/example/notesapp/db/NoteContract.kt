/*
* This file simply defines the table name and column names used for the "notes" tables in SQLite
*/


package com.example.notesapp.db


object NoteContract{

    const val TABLE_NAME = "notes"

    const val COLUMN_ID = "id"
    const val COLUMN_TITLE = "title"
    const val COLUMN_CONTENT = "content"
    const val COLUMN_CREATED_AT = "created_at"
    const val COLUMN_UPDATED_AT = "updated_at"
    const val COLUMN_SYNC_STATUS = "sync_status"

    object SyncStatus {
        const val SYNCED = "synced"
        const val PENDING_CREATE = "pending_create"       // Offline creation --> server has no idea about this
        const val PENDING_UPDATE = "pending_update"       // Offline editing --> Server has its own previous data
        const val PENDING_DELETE = "pending_delete"       // Offline deletion --> Server still has the deleted data
    }
}