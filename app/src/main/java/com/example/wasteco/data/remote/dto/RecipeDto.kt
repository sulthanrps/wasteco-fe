package com.example.wasteco.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenerateRecipeRequest(
    @SerialName("ingredient_ids") val ingredientIds: List<String>,
    @SerialName("custom_ingredients") val customIngredients: List<String> = emptyList()
)

@Serializable
data class RecipeIngredientItemDto(
    @SerialName("name") val name: String,
    @SerialName("quantity") val quantity: String = ""
)

@Serializable
data class RecipeDto(
    @SerialName("id") val id: String? = null,
    @SerialName("recipe_name") val recipeName: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("cooking_time_minutes") val cookingTimeMinutes: Int? = 20,
    @SerialName("difficulty") val difficulty: String? = "Mudah",
    @SerialName("portions") val portions: Int? = null,
    @SerialName("servings") val servings: Int? = null,
    @SerialName("match_percentage") val matchPercentage: Int? = 60,
    @SerialName("estimated_savings") val estimatedSavings: Int? = 20000,
    @SerialName("available_ingredients") val availableIngredients: List<RecipeIngredientItemDto> = emptyList(),
    @SerialName("used_ingredients") val usedIngredients: List<RecipeIngredientItemDto> = emptyList(),
    @SerialName("additional_ingredients") val additionalIngredients: List<RecipeIngredientItemDto> = emptyList(),
    @SerialName("steps") val steps: List<String> = emptyList(),
    @SerialName("instructions") val instructions: List<String> = emptyList(),
    @SerialName("smart_tips") val smartTips: String? = null,
    @SerialName("zero_waste_tips") val zeroWasteTips: String? = null,
    @SerialName("image_url") val imageUrl: String? = null
)

@Serializable
data class RecipeListPayloadDto(
    @SerialName("total") val total: Int? = null,
    @SerialName("recipes") val recipes: List<RecipeDto> = emptyList()
)

@Serializable
data class RecipeListResponse(
    @SerialName("payload") val payload: RecipeListPayloadDto
)

@Serializable
data class UsedIngredientDto(
    @SerialName("ingredient_id") val ingredientId: String? = null,
    @SerialName("name") val name: String,
    @SerialName("quantity") val quantity: Double = 1.0,
    @SerialName("unit") val unit: String = "porsi",
    @SerialName("estimated_price") val estimatedPrice: Int? = 0
)

@Serializable
data class ConfirmCookRequest(
    @SerialName("recipe_name") val recipeName: String,
    @SerialName("portions") val portions: Int,
    @SerialName("estimated_savings") val estimatedSavings: Int,
    @SerialName("used_ingredients") val usedIngredients: List<UsedIngredientDto>
)

@Serializable
data class ConfirmCookPayloadDto(
    @SerialName("message") val message: String? = null,
    @SerialName("portions_saved") val portionsSaved: Int? = null,
    @SerialName("money_saved") val moneySaved: Int? = null
)

@Serializable
data class ConfirmCookResponse(
    @SerialName("payload") val payload: ConfirmCookPayloadDto? = null
)
