/*
*  This class is responsible for managing the JWT token using Jetpack Datastore inside the Android app.
*/


package com.example.notesapp.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey


private val Context.dataStore by preferencesDataStore(
    name = "user_preferences"
)

class TokenManager(private val context: Context) {

    // Preference key used to store the JWT token
    private val TOKEN_KEY = stringPreferencesKey("jwt_token")

    // With this function, the JWT token will be saved into DataStore
    suspend fun saveToken(token: String){
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }
}