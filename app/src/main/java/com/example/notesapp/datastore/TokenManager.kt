/*
*  This class is responsible for managing the JWT token using Jetpack Datastore inside the Android app.
*/


package com.example.notesapp.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore


private val Context.dataStore by preferencesDataStore(
    name = "user_preferences"
)

class TokenManager(
    private val context: Context
) {

}