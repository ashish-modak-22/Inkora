/*
* This data class file will return the JWT token after successful login
*/


package com.example.notesapp.model

data class LoginResponse (

    val accessToken: String,
    val tokenType: String
)