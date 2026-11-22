package com.example.wasteco.data.remote.interceptor

import com.example.wasteco.data.local.AuthPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Clears the stored session when the server rejects the bearer token (HTTP 401).
 * The token stream then emits null and the UI navigates back to Login.
 *
 * Must be registered AFTER [AuthTokenInterceptor] so the Authorization header is present.
 */
class UnauthorizedInterceptor(
    private val authPreferences: AuthPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code == HTTP_UNAUTHORIZED && !isAuthEndpoint(request.url.encodedPath)) {
            val sentToken = request.header(HEADER_AUTHORIZATION)
                ?.removePrefix(BEARER_PREFIX)
                ?.takeIf { it.isNotBlank() }
            if (sentToken != null) {
                runBlocking { authPreferences.clearAuthIfTokenMatches(sentToken) }
            }
        }
        return response
    }

    private fun isAuthEndpoint(path: String): Boolean =
        AUTH_PATHS.any { path.endsWith(it) }

    private companion object {
        const val HTTP_UNAUTHORIZED = 401
        const val HEADER_AUTHORIZATION = "Authorization"
        const val BEARER_PREFIX = "Bearer "
        val AUTH_PATHS = listOf("/auth/login", "/auth/register")
    }
}
