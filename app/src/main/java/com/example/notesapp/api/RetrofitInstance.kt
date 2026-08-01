//package com.example.notesapp.api
//
//import com.example.notesapp.utils.Constants
//import com.example.notesapp.api.ApiService
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import android.content.Context
//import okhttp3.OkHttpClient
//
//
//object RetrofitInstance {
//
//    fun getApi(context: Context): ApiService {
//
//        // The Retrofit.Builder() works similar to that of the app = FastAPI()
//        return Retrofit.Builder()
//            .baseUrl(Constants.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(getClient(context))
//            .build()
//            .create(ApiService::class.java)
//    }
//
//    // The HTTP client used by retrofit
//    private fun getClient(context: Context): OkHttpClient {
//
//        return OkHttpClient.Builder()
//            .addInterceptor (
//                AuthInterceptor(context)
//                ).build()
//    }
//}