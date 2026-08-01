/*
* This file is going to define all backend API endpoints used by the Android apk
* This file describes how to communicate with the backend
*/


package com.example.notesapp.api

import com.example.notesapp.model.RegisterRequest
import com.example.notesapp.model.RegisterResponse
import com.example.notesapp.model.LoginRequest
import com.example.notesapp.model.LoginResponse
import com.example.notesapp.model.NoteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header


interface ApiService {

    // New user registration API
    @POST("/auth/register")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    // User login API ---> This format is used because the backend API uses "form type credentials" instead of JSON
    @FormUrlEncoded
    @POST("/auth/login")
    suspend fun loginUser(
        @Field("username")
        email: String,
        @Field("password")
        password: String
    ): Response<LoginResponse>

    @GET("/notes/")
    suspend fun getAllNotes(
        @Header("Authorization")
        token: String
    ): Response<List<NoteResponse>>

}