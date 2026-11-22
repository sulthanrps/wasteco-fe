package com.example.wasteco.domain.model

data class UsedIngredient(
    val ingredientId: String,
    val name: String,
    val quantity: Double,
    val unit: String,
    val estimatedPrice: Int
)

data class Recipe(
    val id: String,
    val name: String,
    val description: String,
    val cookingTimeMinutes: Int,
    val difficulty: String,
    val servings: Int,
    val estimatedSavings: Int,
    val usedIngredients: List<UsedIngredient>,
    val additionalIngredients: List<String>,
    val instructions: List<String>,
    val zeroWasteTips: String? = null,
    val imageUrl: String? = null,
    val matchPercentage: Int = 60
)
