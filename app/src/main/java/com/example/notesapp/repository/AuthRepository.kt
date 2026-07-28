package com.example.notesapp.repository

import com.example.notesapp.api.RetrofitInstance
import com.example.notesapp.model.RegisterRequest
import com.example.notesapp.model.RegisterResponse
import retrofit2.Response

class AuthRepository {

    suspend fun registerUser(
        request: RegisterRequest
    ): Response<RegisterResponse> {

        return RetrofitInstance.api.registerUser(request)
    }
}