/*
* This class intercepts every outgoing HTTP request and automatically attaches the JWT token to the request header
* FastAPI equivalent: current_user: User = Depends(get_current_user)
*/

package com.example.notesapp.interceptor

import android.content.Context
import com.example.notesapp.datastore.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context): Interceptor {

    private val tokenManager = TokenManager(context)

    // This function is being called for each network request automatically
    override fun intercept(chain: Interceptor.Chain): Response {

        // It receives the requests send by Retrofit
        val request = chain.request()

        // It will send the request to the server
        return chain.proceed(request)
    }
}