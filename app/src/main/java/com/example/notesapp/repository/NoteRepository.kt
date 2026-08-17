/*
*  The offline-first brain: reads always come from SQLite, refreshed from the server first when online; writes save to SQLite instantly and push to the server right away, or stay queued if you're offline
*/



package com.example.notesapp.repository

