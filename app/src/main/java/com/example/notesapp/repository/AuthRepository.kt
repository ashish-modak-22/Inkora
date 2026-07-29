/*
* This repository is responsible for all authentication related API operations
*/


package com.example.notesapp.repository

import com.example.notesapp.api.RetrofitInstance
import com.example.notesapp.model.RegisterRequest
import com.example.notesapp.model.RegisterResponse
import com.example.notesapp.model.LoginRequest
import com.example.notesapp.model.LoginResponse
import retrofit2.Response

class AuthRepository {

    suspend fun registerUser(
        request: RegisterRequest
    ): Response<RegisterResponse> {
        return RetrofitInstance.api.registerUser(request)
    }

    suspend fun loginUser(
        request: LoginRequest
    ): Response<LoginResponse> {
        return RetrofitInstance.api.loginUser(request)
    }
}