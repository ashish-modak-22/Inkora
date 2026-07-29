/*
* This file is going to define all backend API endpoints used by the Android apk
* This file describes how to communicate with the backend
*/


package com.example.notesapp.api

import com.example.notesapp.model.RegisterRequest
import com.example.notesapp.model.RegisterResponse
import com.example.notesapp.model.LoginRequest
import com.example.notesapp.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {

    @POST("/auth/register")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("/auth/login")
    suspend fun loginUser(
        @Body request: LoginRequest
    ): Response<LoginResponse>
}