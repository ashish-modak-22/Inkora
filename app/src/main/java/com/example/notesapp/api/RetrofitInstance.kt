package com.example.notesapp.api

import com.example.notesapp.utils.Constants
import com.example.notesapp.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {

    val api: ApiService by lazy {

        // The Retrofit.Builder() works similar to that of the app = FastAPI()
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}