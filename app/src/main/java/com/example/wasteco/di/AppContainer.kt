package com.example.wasteco.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.wasteco.BuildConfig
import com.example.wasteco.data.local.AuthPreferences
import com.example.wasteco.data.remote.api.AuthApiService
import com.example.wasteco.data.remote.api.DonationApiService
import com.example.wasteco.data.remote.interceptor.ApiKeyInterceptor
import com.example.wasteco.data.remote.interceptor.AuthTokenInterceptor
import com.example.wasteco.data.remote.interceptor.UnauthorizedInterceptor
import com.example.wasteco.data.repository.AuthRepository
import com.example.wasteco.data.repository.DefaultAuthRepository
import com.example.wasteco.data.remote.api.PantryApiService
import com.example.wasteco.data.remote.api.RecipeApiService
import com.example.wasteco.data.repository.DefaultDonationRepository
import com.example.wasteco.data.repository.DefaultPantryRepository
import com.example.wasteco.data.repository.DefaultRecipeRepository
import com.example.wasteco.data.repository.DonationRepository
import com.example.wasteco.data.repository.PantryRepository
import com.example.wasteco.data.repository.RecipeRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

private val Context.dataStore by preferencesDataStore(name = "wasteco_prefs")

interface AppContainer {
    val authRepository: AuthRepository
    val pantryRepository: PantryRepository
    val recipeRepository: RecipeRepository
    val donationRepository: DonationRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    val authPreferences: AuthPreferences by lazy {
        AuthPreferences(context.dataStore)
    }

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor())
            .addInterceptor(AuthTokenInterceptor(authPreferences))
            .addInterceptor(UnauthorizedInterceptor(authPreferences))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    private val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    private val pantryApiService: PantryApiService by lazy {
        retrofit.create(PantryApiService::class.java)
    }

    private val recipeApiService: RecipeApiService by lazy {
        retrofit.create(RecipeApiService::class.java)
    }

    private val donationApiService: DonationApiService by lazy {
        retrofit.create(DonationApiService::class.java)
    }

    override val authRepository: AuthRepository by lazy {
        DefaultAuthRepository(
            authApiService = authApiService,
            authPreferences = authPreferences,
            json = json
        )
    }

    override val pantryRepository: PantryRepository by lazy {
        DefaultPantryRepository(
            pantryApiService = pantryApiService,
            json = json
        )
    }

    override val recipeRepository: RecipeRepository by lazy {
        DefaultRecipeRepository(
            recipeApiService = recipeApiService,
            json = json
        )
    }

    override val donationRepository: DonationRepository by lazy {
        DefaultDonationRepository(
            donationApiService = donationApiService,
            json = json
        )
    }
}
