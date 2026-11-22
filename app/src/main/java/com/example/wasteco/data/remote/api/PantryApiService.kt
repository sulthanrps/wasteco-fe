package com.example.wasteco.data.remote.api

import com.example.wasteco.data.remote.dto.AddPantryRequest
import com.example.wasteco.data.remote.dto.PantryItemResponse
import com.example.wasteco.data.remote.dto.PantryListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PantryApiService {

    @GET("api/v1/pantry")
    suspend fun getPantryIngredients(): Response<PantryListResponse>

    @POST("api/v1/pantry")
    suspend fun addPantryIngredient(
        @Body request: AddPantryRequest
    ): Response<PantryItemResponse>

    @DELETE("api/v1/pantry/{id}")
    suspend fun deletePantryIngredient(
        @Path("id") id: String
    ): Response<Unit>
}
