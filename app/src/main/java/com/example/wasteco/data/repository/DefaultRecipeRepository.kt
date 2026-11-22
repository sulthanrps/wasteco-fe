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
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import java.io.IOException
import java.util.UUID
import kotlinx.coroutines.CancellationException
import java.util.concurrent.ConcurrentHashMap

class DefaultRecipeRepository(
    private val recipeApiService: RecipeApiService,
    private val pantryRepository: PantryRepository,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : RecipeRepository {

    private val recipeCache = ConcurrentHashMap<String, Recipe>()

    override suspend fun generateRecipes(
        ingredientIds: List<String>,
        customIngredients: List<String>
    ): Result<List<Recipe>> = withContext(Dispatchers.IO) {
        try {
            val pantryItems = pantryRepository.getPantryIngredients().getOrThrow()
            val selectedItems = if (ingredientIds.isEmpty()) pantryItems else {
                pantryItems.filter { it.id in ingredientIds }
            }
            val request = GenerateRecipeRequest(
                ingredientIds = selectedItems.map { it.id },
                customIngredients = customIngredients
            )
            require(request.ingredientIds.isNotEmpty() || request.customIngredients.isNotEmpty()) {
                "Tambahkan bahan ke pantry sebelum mencari resep."
            }
            val response = recipeApiService.generateRecipes(request)
            if (response.isSuccessful && response.body() != null) {
                val recipesDto = response.body()!!.payload.recipes
                val recipes = recipesDto.mapIndexed { index, dto ->
                    dto.toDomain(fallbackId = "recipe_$index", pantryItems = selectedItems)
                }
                recipeCache.clear()
                recipes.forEach { recipe ->
                    recipeCache[recipe.id] = recipe
                }
                Result.success(recipes)
            } else {
                Result.failure(Exception(parseErrorMessage(response.errorBody()?.string())))
            }
        } catch (e: CancellationException) {
            throw e
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
                        ingredientId = it.ingredientId.takeIf { id ->
                            runCatching { UUID.fromString(id).toString().equals(id, ignoreCase = true) }
                                .getOrDefault(false)
                        },
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
                val errorBody = response.errorBody()?.use { it.string() }
                Result.failure(IllegalStateException(cookConfirmationError(json, response.code(), errorBody)))
            }
        } catch (e: CancellationException) {
            throw e
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

internal fun cookConfirmationError(json: Json, statusCode: Int, body: String?): String {
    val error = body?.let { runCatching { json.parseToJsonElement(it) as? JsonObject }.getOrNull() }
    val message = (error?.get("message") as? JsonPrimitive)?.contentOrNull
        ?: (error?.get("error") as? JsonPrimitive)?.contentOrNull
    val details = listOf("details", "detail", "errors", "field_errors")
        .flatMap { key -> error?.get(key).validationMessages() }
        .distinct()
    if (details.isNotEmpty()) return details.joinToString("\n")
    if (error?.get("error") is JsonObject) {
        val nested = error["error"].validationMessages()
        if (nested.isNotEmpty()) return nested.joinToString("\n")
    }
    val usefulMessage = message?.trim()?.takeIf {
        it.isNotEmpty() && it.trimEnd('.').replace('_', ' ').lowercase() !in
            setOf("validation failed", "validation error", "bad request")
    }
    return usefulMessage ?: when (statusCode) {
        400, 422 -> "Server menolak data bahan. Periksa nama, jumlah, dan satuan tiap bahan, lalu coba lagi."
        else -> "Gagal mengonfirmasi masakan (HTTP $statusCode)."
    }
}

private fun JsonElement?.validationMessages(): List<String> = when (this) {
    is JsonArray -> flatMap { it.validationMessages() }
    is JsonObject -> {
        val message = (get("message") as? JsonPrimitive)?.contentOrNull
            ?: (get("reason") as? JsonPrimitive)?.contentOrNull
        val field = (get("field") as? JsonPrimitive)?.contentOrNull
        if (!message.isNullOrBlank()) {
            listOf(if (field.isNullOrBlank()) message else "$field: $message")
        } else {
            entries.flatMap { (key, value) ->
                value.validationMessages().map { message -> "$key: $message" }
            }
        }
    }
    is JsonPrimitive -> listOfNotNull(contentOrNull?.takeIf { isString && it.isNotBlank() })
    else -> emptyList()
}
