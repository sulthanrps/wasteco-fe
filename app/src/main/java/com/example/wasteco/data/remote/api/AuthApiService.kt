package com.example.wasteco.data.remote.api

import com.example.wasteco.data.remote.dto.AuthResponse
import com.example.wasteco.data.remote.dto.LoginRequest
import com.example.wasteco.data.remote.dto.MeResponse
import com.example.wasteco.data.remote.dto.RegisterRequest
import com.example.wasteco.data.remote.dto.UpdateProfileRequest
import retrofit2.http.PUT
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/v1/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @GET("api/v1/me")
    suspend fun getMe(): Response<MeResponse>

    @PUT("api/v1/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<Unit>
}
