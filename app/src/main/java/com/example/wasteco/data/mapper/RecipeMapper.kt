package com.example.wasteco.data.mapper

import com.example.wasteco.data.remote.dto.RecipeDto
import com.example.wasteco.data.remote.dto.RecipeIngredientItemDto
import com.example.wasteco.domain.model.PantryItem
import com.example.wasteco.domain.model.Recipe
import com.example.wasteco.domain.model.UsedIngredient
import java.util.UUID

fun RecipeIngredientItemDto.toUsedIngredient(fallbackId: String = UUID.randomUUID().toString()): UsedIngredient {
    val parts = quantity.trim().split("\\s+".toRegex(), limit = 2)
    val parsedQty = parts.firstOrNull()?.replace(",", ".")?.toDoubleOrNull() ?: 1.0
    val parsedUnit = parts.getOrNull(1)?.ifBlank { null } ?: "porsi"

    return UsedIngredient(
        ingredientId = fallbackId,
        name = name,
        quantity = parsedQty,
        unit = parsedUnit,
        estimatedPrice = 5000
    )
}

fun RecipeDto.toDomain(
    fallbackId: String = UUID.randomUUID().toString(),
    pantryItems: List<PantryItem> = emptyList()
): Recipe {
    val recipeId = id ?: fallbackId
    val effectiveName = recipeName ?: name ?: "Resep Cerdas WasteCo"
    val effectiveServings = portions ?: servings ?: 2
    val effectiveInstructions = if (steps.isNotEmpty()) steps else instructions
    val effectiveTips = smartTips ?: zeroWasteTips

    val rawUsedIngredients = if (availableIngredients.isNotEmpty()) {
        availableIngredients
    } else {
        usedIngredients
    }

    val mappedUsedIngredients = rawUsedIngredients.mapIndexed { idx, ing ->
        val ingredient = ing.toUsedIngredient(fallbackId = "${recipeId}_ing_$idx")
        val matchingItems = pantryItems.filter { item ->
            val sameIngredient = if (ing.ingredientId != null) {
                item.id == ing.ingredientId
            } else {
                item.name.trim().equals(ingredient.name.trim(), ignoreCase = true)
            }
            sameIngredient && item.unit.trim().equals(ingredient.unit.trim(), ignoreCase = true)
        }
        val pantryItem = matchingItems.singleOrNull()
        if (pantryItem == null) ingredient.copy(estimatedPrice = 0) else {
            ingredient.copy(
                ingredientId = pantryItem.id,
                unit = pantryItem.unit,
                estimatedPrice = pantryItem.estimatedPrice
            )
        }
    }

    val mappedAdditionalIngredients = additionalIngredients.map { item ->
        if (item.quantity.isNotBlank()) {
            "${item.name}: ${item.quantity}"
        } else {
            item.name
        }
    }

    return Recipe(
        id = recipeId,
        name = effectiveName,
        description = description.orEmpty(),
        cookingTimeMinutes = cookingTimeMinutes ?: 20,
        difficulty = difficulty ?: "Mudah",
        servings = effectiveServings,
        estimatedSavings = estimatedSavings ?: 20000,
        usedIngredients = mappedUsedIngredients,
        additionalIngredients = mappedAdditionalIngredients,
        instructions = effectiveInstructions,
        zeroWasteTips = effectiveTips,
        imageUrl = imageUrl ?: "https://images.unsplash.com/photo-1603133872878-684f208fb84b?auto=format&fit=crop&w=800&q=80",
        matchPercentage = matchPercentage ?: 60
    )
}
