package com.example.wasteco.data.repository

import com.example.wasteco.domain.model.Recipe
import com.example.wasteco.domain.model.UsedIngredient

interface RecipeRepository {
    suspend fun generateRecipes(
        ingredientIds: List<String>,
        customIngredients: List<String> = emptyList()
    ): Result<List<Recipe>>

    suspend fun confirmCooking(
        recipeName: String,
        portions: Int,
        estimatedSavings: Int,
        usedIngredients: List<UsedIngredient>
    ): Result<Unit>

    fun getCachedRecipe(id: String): Recipe?
}
