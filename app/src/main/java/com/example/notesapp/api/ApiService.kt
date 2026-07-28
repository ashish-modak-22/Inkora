package com.example.notesapp.api

import com.example.notesapp.model.RegisterRequest
import com.example.notesapp.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {

    @POST("/auth/register")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>
}