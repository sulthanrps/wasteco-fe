package com.example.wasteco.data.repository

import com.example.wasteco.domain.model.SessionState
import com.example.wasteco.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val userTokenStream: Flow<String?>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(
        name: String,
        email: String,
        password: String,
        areaName: String,
        latitude: Double = -7.9528,
        longitude: Double = 112.6074,
        sharingRadiusKm: Int = 3
    ): Result<User>
    suspend fun getMe(): Result<User>

    /**
     * Validates the stored token against the server.
     * Clears the session on HTTP 401; keeps it on network/server errors (offline tolerance).
     */
    suspend fun validateSession(): SessionState
    suspend fun logout()
}
