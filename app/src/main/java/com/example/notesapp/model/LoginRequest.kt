/*
* This file represents the request body that android will send to the FastAPI backend during user login
*/


package com.example.notesapp.model

data class LoginRequest (

    val email: String,
    val password: String
)