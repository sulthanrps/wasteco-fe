package com.example.wasteco.data.repository

import com.example.wasteco.data.local.AuthPreferences
import com.example.wasteco.data.mapper.toDomain
import com.example.wasteco.data.remote.api.AuthApiService
import com.example.wasteco.data.remote.dto.ErrorResponse
import com.example.wasteco.data.remote.dto.LoginRequest
import com.example.wasteco.data.remote.dto.RegisterRequest
import com.example.wasteco.data.remote.dto.UpdateProfileRequest
import com.example.wasteco.domain.model.SessionState
import com.example.wasteco.domain.model.User
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.IOException

class DefaultAuthRepository(
    private val authApiService: AuthApiService,
    private val authPreferences: AuthPreferences,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : AuthRepository {

    override val userTokenStream: Flow<String?> = authPreferences.authTokenStream

    override suspend fun login(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = authApiService.login(LoginRequest(email = email, password = password))
            if (response.isSuccessful && response.body() != null) {
                val payload = response.body()!!.payload
                val user = payload.user.toDomain()
                authPreferences.saveAuth(payload.token, user)
                Result.success(user)
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Koneksi gagal. Periksa jaringan internet Anda."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan yang tidak terduga"))
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        phoneNumber: String,
        areaName: String,
        latitude: Double,
        longitude: Double,
        sharingRadiusKm: Int
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            val request = RegisterRequest(
                name = name,
                email = email,
                password = password,
                phoneNumber = phoneNumber,
                areaName = areaName,
                latitude = latitude,
                longitude = longitude,
                sharingRadiusKm = sharingRadiusKm
            )
            val response = authApiService.register(request)
            if (response.isSuccessful && response.body() != null) {
                val payload = response.body()!!.payload
                val user = payload.user.toDomain()
                authPreferences.saveAuth(payload.token, user)
                Result.success(user)
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Koneksi gagal. Periksa jaringan internet Anda."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan yang tidak terduga"))
        }
    }

    override suspend fun getMe(): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = authApiService.getMe()
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!.payload.user.toDomain()
                Result.success(user)
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Koneksi gagal. Periksa jaringan internet Anda."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan yang tidak terduga"))
        }
    }

    override suspend fun updateProfile(user: User): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = authApiService.updateProfile(
                UpdateProfileRequest(
                    name = user.name,
                    phoneNumber = user.phoneNumber,
                    areaName = user.areaName,
                    latitude = user.latitude,
                    longitude = user.longitude,
                    sharingRadiusKm = user.sharingRadiusKm
                )
            )
            if (response.isSuccessful) Result.success(Unit) else {
                Result.failure(IllegalStateException(parseErrorMessage(response.errorBody()?.string())))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun validateSession(): SessionState = withContext(Dispatchers.IO) {
        val token = authPreferences.getAuthToken()
        if (token.isNullOrBlank()) return@withContext SessionState.Unauthenticated

        try {
            val response = authApiService.getMe()
            if (response.code() == HTTP_UNAUTHORIZED) {
                authPreferences.clearAuthIfTokenMatches(token)
                SessionState.Unauthenticated
            } else {
                // Success, or a non-auth server error: keep the user signed in.
                SessionState.Authenticated
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // Offline / timeout / parse error: don't log the user out.
            SessionState.Authenticated
        }
    }

    override suspend fun logout() = withContext(Dispatchers.IO) {
        authPreferences.clearAuth()
    }

    private fun parseErrorMessage(errorBody: String?): String {
        if (errorBody.isNullOrBlank()) return "Terjadi kesalahan pada server"
        return try {
            val errorResponse = json.decodeFromString<ErrorResponse>(errorBody)
            errorResponse.message ?: errorResponse.error ?: "Terjadi kesalahan"
        } catch (_: Exception) {
            "Terjadi kesalahan format dari server"
        }
    }

    private companion object {
        const val HTTP_UNAUTHORIZED = 401
    }
}
