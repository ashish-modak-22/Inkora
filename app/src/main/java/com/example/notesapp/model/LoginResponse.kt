/*
* This data class file will return the JWT token after successful login
*/


package com.example.notesapp.model

import com.google.gson.annotations.SerializedName

data class LoginResponse (

    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("token_type")
    val tokenType: String
)