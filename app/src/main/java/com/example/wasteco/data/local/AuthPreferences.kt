package com.example.wasteco.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.wasteco.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException

class AuthPreferences(private val dataStore: DataStore<Preferences>) {

    private companion object {
        val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
        val KEY_USER_ID = stringPreferencesKey("user_id")
        val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_USER_AREA = stringPreferencesKey("user_area")
    }

    val authTokenStream: Flow<String?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[KEY_AUTH_TOKEN]
        }

    suspend fun getAuthToken(): String? {
        return authTokenStream.firstOrNull()
    }

    suspend fun saveAuth(token: String, user: User) {
        dataStore.edit { preferences ->
            preferences[KEY_AUTH_TOKEN] = token
            preferences[KEY_USER_ID] = user.id
            preferences[KEY_USER_EMAIL] = user.email
            preferences[KEY_USER_NAME] = user.name
            preferences[KEY_USER_AREA] = user.areaName
        }
    }

    suspend fun clearAuth() {
        dataStore.edit { preferences -> preferences.removeAuth() }
    }

    /**
     * Clears the session only if the stored token is still [token].
     * Prevents a late 401 from an old token wiping a freshly saved login.
     */
    suspend fun clearAuthIfTokenMatches(token: String) {
        dataStore.edit { preferences ->
            if (preferences[KEY_AUTH_TOKEN] == token) {
                preferences.removeAuth()
            }
        }
    }

    private fun MutablePreferences.removeAuth() {
        remove(KEY_AUTH_TOKEN)
        remove(KEY_USER_ID)
        remove(KEY_USER_EMAIL)
        remove(KEY_USER_NAME)
        remove(KEY_USER_AREA)
    }
}
