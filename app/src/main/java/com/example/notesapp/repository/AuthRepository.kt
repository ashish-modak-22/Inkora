///*
//* This repository is responsible for all authentication related API operations
//*/
//
//
//package com.example.notesapp.repository
//
//import android.content.Context
//import com.example.notesapp.api.RetrofitInstance
//import com.example.notesapp.model.RegisterRequest
//import com.example.notesapp.model.RegisterResponse
//import com.example.notesapp.model.LoginRequest
//import com.example.notesapp.model.LoginResponse
//import retrofit2.Response
//
//class AuthRepository(private val context: Context) {
//
//    suspend fun registerUser(
//        request: RegisterRequest
//    ): Response<RegisterResponse> {
//        return RetrofitInstance.getApi(context).registerUser(request)
//    }
//
//    suspend fun loginUser(
//        request: LoginRequest
//    ): Response<LoginResponse> {
//        return RetrofitInstance.getApi(context).loginUser(request)
//    }
//}