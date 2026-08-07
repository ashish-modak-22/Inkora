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
import com.example.notesapp.model.NoteRequest
import retrofit2.http.PUT
import retrofit2.http.Path


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

    @POST("/notes")
    suspend fun createNote(
        @Header("Authorization")
        token: String,
        @Body request: NoteRequest
    ): Response<NoteResponse>

    @PUT("/notes/{note_id}")
    suspend fun updateNote(
        @Header("Authorization")
        token: String,

        @Path("note_id")
        noteId: Int,

        @Body
        request: NoteRequest
    ): Response<NoteResponse>

}