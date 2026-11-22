package com.example.wasteco.data.remote.interceptor

import com.example.wasteco.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestWithHeaders = originalRequest.newBuilder()
            .header("x-api-key", BuildConfig.WASTECO_API_KEY)
            .header("User-Agent", "WasteCo-Android/1.0")
            .build()
        return chain.proceed(requestWithHeaders)
    }
}
