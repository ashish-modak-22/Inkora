/*
* This is the single place the rest of the app should go through to read/write notes.
*
* Strategy (offline-first):
*  - Reads always come from the local SQLite cache, which is refreshed from the server first
*    whenever we're online.
*  - Writes (create/update/delete) are applied to SQLite immediately so the UI updates instantly
*    and works offline, and are pushed to the server right away if we're online. If we're offline,
*    they're left flagged as "pending" and get pushed the next time getNotes()/syncPendingChanges()
*    runs with a connection available.
*/

package com.example.notesapp.repository

import android.content.Context
import com.example.notesapp.api.RetrofitInstance
import com.example.notesapp.db.NoteContract.SyncStatus
import com.example.notesapp.db.NoteDao
import com.example.notesapp.db.NoteEntity
import com.example.notesapp.model.NoteRequest
import com.example.notesapp.model.NoteResponse
import com.example.notesapp.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Thrown when the server tells us the token is no longer valid, so the caller can log the user out.
class UnauthorizedException : Exception()

class NoteRepository(context: Context) {

    private val appContext = context.applicationContext
    private val dao = NoteDao(appContext)
    private val api = RetrofitInstance.api

    // Reads: refresh from the server when possible, then always return what's in SQLite so
    // the list shows up instantly and still works with no connection.
    suspend fun getNotes(
        token: String,
        search: String? = null,
        sortBy: String = "created_at",
        order: String = "desc"
    ): List<NoteResponse> = withContext(Dispatchers.IO) {

        if (NetworkUtils.isOnline(appContext)) {
            try {
                val response = api.getAllNotes("Bearer $token", search = search, sortBy = sortBy, order = order)
                if (response.isSuccessful) {
                    val serverNotes = response.body() ?: emptyList()
                    dao.upsertSyncedNotes(serverNotes.map { NoteEntity.fromNoteResponse(it, SyncStatus.SYNCED) })
                } else if (response.code() == 401) {
                    throw UnauthorizedException()
                }
                // Push anything the user created/edited/deleted while offline.
                syncPendingChanges(token)
            } catch (e: UnauthorizedException) {
                throw e
            } catch (e: Exception) {
                // No connection, timeout, etc. -- fall through and serve the local cache below.
            }
        }

        dao.getAll(search, sortBy, order).map { it.toNoteResponse() }
    }

    suspend fun createNote(token: String, title: String, content: String): NoteResponse =
        withContext(Dispatchers.IO) {

            val now = isoNow()
            val tempId = generateTempId()
            val localNote = NoteEntity(tempId, title, content, now, now, SyncStatus.PENDING_CREATE)
            dao.upsert(localNote)

            if (NetworkUtils.isOnline(appContext)) {
                try {
                    val response = api.createNote("Bearer $token", NoteRequest(title, content))
                    if (response.isSuccessful) {
                        response.body()?.let { serverNote ->
                            dao.replaceId(tempId, NoteEntity.fromNoteResponse(serverNote, SyncStatus.SYNCED))
                            return@withContext serverNote
                        }
                    } else if (response.code() == 401) {
                        throw UnauthorizedException()
                    }
                } catch (e: UnauthorizedException) {
                    throw e
                } catch (e: Exception) {
                    // Stays queued as PENDING_CREATE, will be pushed on the next sync.
                }
            }
            localNote.toNoteResponse()
        }

    suspend fun updateNote(token: String, id: Int, title: String, content: String): NoteResponse =
        withContext(Dispatchers.IO) {

            val existing = dao.getById(id)
            val now = isoNow()
            val createdAt = existing?.createdAt ?: now

            // A note that was never synced stays PENDING_CREATE -- there's nothing to "update"
            // on the server yet, it just needs to be created with the latest content.
            val stillUnsynced = existing?.syncStatus == SyncStatus.PENDING_CREATE
            val newStatus = if (stillUnsynced) SyncStatus.PENDING_CREATE else SyncStatus.PENDING_UPDATE
            val updated = NoteEntity(id, title, content, createdAt, now, newStatus)
            dao.upsert(updated)

            if (NetworkUtils.isOnline(appContext)) {
                try {
                    if (stillUnsynced) {
                        val response = api.createNote("Bearer $token", NoteRequest(title, content))
                        if (response.isSuccessful) {
                            response.body()?.let { serverNote ->
                                dao.replaceId(id, NoteEntity.fromNoteResponse(serverNote, SyncStatus.SYNCED))
                                return@withContext serverNote
                            }
                        } else if (response.code() == 401) {
                            throw UnauthorizedException()
                        }
                    } else {
                        val response = api.updateNote("Bearer $token", id, NoteRequest(title, content))
                        if (response.isSuccessful) {
                            response.body()?.let { serverNote ->
                                dao.upsert(NoteEntity.fromNoteResponse(serverNote, SyncStatus.SYNCED))
                                return@withContext serverNote
                            }
                        } else if (response.code() == 401) {
                            throw UnauthorizedException()
                        }
                    }
                } catch (e: UnauthorizedException) {
                    throw e
                } catch (e: Exception) {
                    // Stays queued, will be pushed on the next sync.
                }
            }
            updated.toNoteResponse()
        }

    suspend fun deleteNote(token: String, id: Int) = withContext(Dispatchers.IO) {

        val existing = dao.getById(id)

        // Never made it to the server in the first place -- just drop it locally.
        if (existing?.syncStatus == SyncStatus.PENDING_CREATE) {
            dao.deletePermanently(id)
            return@withContext
        }

        // Flag it first so the delete survives even if we lose connection mid-request.
        existing?.let { dao.upsert(it.copy(syncStatus = SyncStatus.PENDING_DELETE)) }

        if (NetworkUtils.isOnline(appContext)) {
            try {
                val response = api.deleteNote("Bearer $token", id)
                if (response.isSuccessful) {
                    dao.deletePermanently(id)
                } else if (response.code() == 401) {
                    throw UnauthorizedException()
                }
            } catch (e: UnauthorizedException) {
                throw e
            } catch (e: Exception) {
                // Stays queued as PENDING_DELETE, will be pushed on the next sync.
            }
        }
    }

    // Pushes every locally-queued create/update/delete to the server. Safe to call often --
    // it's a no-op when there's nothing pending or when we're offline.
    suspend fun syncPendingChanges(token: String) = withContext(Dispatchers.IO) {
        if (!NetworkUtils.isOnline(appContext)) return@withContext

        for (note in dao.getPendingSync()) {
            try {
                when (note.syncStatus) {
                    SyncStatus.PENDING_CREATE -> {
                        val response = api.createNote("Bearer $token", NoteRequest(note.title, note.content))
                        if (response.isSuccessful) {
                            response.body()?.let {
                                dao.replaceId(note.id, NoteEntity.fromNoteResponse(it, SyncStatus.SYNCED))
                            }
                        }
                    }
                    SyncStatus.PENDING_UPDATE -> {
                        val response = api.updateNote("Bearer $token", note.id, NoteRequest(note.title, note.content))
                        if (response.isSuccessful) {
                            response.body()?.let {
                                dao.upsert(NoteEntity.fromNoteResponse(it, SyncStatus.SYNCED))
                            }
                        }
                    }
                    SyncStatus.PENDING_DELETE -> {
                        val response = api.deleteNote("Bearer $token", note.id)
                        if (response.isSuccessful) {
                            dao.deletePermanently(note.id)
                        }
                    }
                }
            } catch (e: Exception) {
                // Leave it queued, we'll retry next time syncPendingChanges runs.
            }
        }
    }

    // Negative so a locally-created note can never collide with a real (positive) server id.
    private fun generateTempId(): Int {
        return -(System.currentTimeMillis() % Int.MAX_VALUE).toInt()
    }

    private fun isoNow(): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        return format.format(Date())
    }
}
