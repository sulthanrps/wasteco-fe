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
        phoneNumber: String,
        areaName: String,
        latitude: Double = 0.0,
        longitude: Double = 0.0,
        sharingRadiusKm: Int = 3
    ): Result<User>
    suspend fun getMe(): Result<User>
    suspend fun updateProfile(user: User): Result<Unit>

    /**
     * Validates the stored token against the server.
     * Clears the session on HTTP 401; keeps it on network/server errors (offline tolerance).
     */
    suspend fun validateSession(): SessionState
    suspend fun logout()
}

fun String.normalizedWhatsAppNumber(): String? {
    val compact = trim().replace(Regex("[\\s()-]"), "")
    val international = when {
        compact.startsWith("0") -> "+62${compact.drop(1)}"
        compact.startsWith("+") -> compact
        else -> "+$compact"
    }
    return international.takeIf { Regex("\\+[1-9][0-9]{7,14}").matches(it) }
}
