package com.example.wasteco.data.remote.api

import com.example.wasteco.data.remote.dto.ConfirmCookRequest
import com.example.wasteco.data.remote.dto.ConfirmCookResponse
import com.example.wasteco.data.remote.dto.GenerateRecipeRequest
import com.example.wasteco.data.remote.dto.RecipeListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RecipeApiService {

    @POST("api/v1/recipes/generate")
    suspend fun generateRecipes(
        @Body request: GenerateRecipeRequest
    ): Response<RecipeListResponse>

    @POST("api/v1/recipes/cook-confirm")
    suspend fun confirmCooking(
        @Body request: ConfirmCookRequest
    ): Response<ConfirmCookResponse>
}
