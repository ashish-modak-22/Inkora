/*
*  Local representation of a note row inside SQLite.
* It is different from the NoteResponse.kt since this file stores an extra "syncStatus" field
*/

package com.example.notesapp.db

import com.example.notesapp.model.NoteResponse


data class NoteEntity(
    val id: Int,
    val title: String,
    val content: String,
    val createdAt: String,
    val updatedAt: String,
    val syncStatus: String
) {
    fun toNoteResponse(): NoteResponse {

        return NoteResponse(
            id = id,
            title = title,
            content = content,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromNoteResponse(note: NoteResponse, syncStatus: String): NoteEntity {
            return NoteEntity(
                id = note.id,
                title = note.title,
                content = note.content,
                createdAt = note.createdAt,
                updatedAt = note.updatedAt,
                syncStatus = syncStatus
            )
        }
    }
}