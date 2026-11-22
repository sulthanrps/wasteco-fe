package com.example.wasteco.data.repository

import com.example.wasteco.data.mapper.toDomain
import com.example.wasteco.data.remote.api.RecipeApiService
import com.example.wasteco.data.remote.dto.ConfirmCookRequest
import com.example.wasteco.data.remote.dto.ErrorResponse
import com.example.wasteco.data.remote.dto.GenerateRecipeRequest
import com.example.wasteco.data.remote.dto.UsedIngredientDto
import com.example.wasteco.domain.model.Recipe
import com.example.wasteco.domain.model.UsedIngredient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

class DefaultRecipeRepository(
    private val recipeApiService: RecipeApiService,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : RecipeRepository {

    private val recipeCache = ConcurrentHashMap<String, Recipe>()

    override suspend fun generateRecipes(
        ingredientIds: List<String>,
        customIngredients: List<String>
    ): Result<List<Recipe>> = withContext(Dispatchers.IO) {
        try {
            val request = GenerateRecipeRequest(
                ingredientIds = ingredientIds,
                customIngredients = customIngredients
            )
            val response = recipeApiService.generateRecipes(request)
            if (response.isSuccessful && response.body() != null) {
                val recipesDto = response.body()!!.payload.recipes
                val recipes = recipesDto.mapIndexed { index, dto ->
                    dto.toDomain(fallbackId = "recipe_$index")
                }
                recipeCache.clear()
                recipes.forEach { recipe ->
                    recipeCache[recipe.id] = recipe
                }
                Result.success(recipes)
            } else {
                Result.failure(Exception(parseErrorMessage(response.errorBody()?.string())))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Koneksi gagal saat merekomendasikan resep."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan pada Gemini AI"))
        }
    }

    override suspend fun confirmCooking(
        recipeName: String,
        portions: Int,
        estimatedSavings: Int,
        usedIngredients: List<UsedIngredient>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = ConfirmCookRequest(
                recipeName = recipeName,
                portions = portions,
                estimatedSavings = estimatedSavings,
                usedIngredients = usedIngredients.map {
                    UsedIngredientDto(
                        ingredientId = it.ingredientId,
                        name = it.name,
                        quantity = it.quantity,
                        unit = it.unit,
                        estimatedPrice = it.estimatedPrice
                    )
                }
            )
            val response = recipeApiService.confirmCooking(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(parseErrorMessage(response.errorBody()?.string())))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Koneksi gagal saat mengonfirmasi masakan."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan pada server"))
        }
    }

    override fun getCachedRecipe(id: String): Recipe? {
        return recipeCache[id]
    }

    private fun parseErrorMessage(errorBody: String?): String {
        if (errorBody.isNullOrBlank()) return "Terjadi kesalahan pada server"
        return try {
            val errorResponse = json.decodeFromString<ErrorResponse>(errorBody)
            errorResponse.message ?: errorResponse.error ?: "Terjadi kesalahan"
        } catch (_: Exception) {
            "Gagal menghasilkan resep dengan Gemini AI"
        }
    }
}
