package com.example.wasteco.ui.feature.recipe.detail

import com.example.wasteco.domain.model.Recipe

data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val cookedPortions: Int = 2,
    val showConfirmDialog: Boolean = false,
    val isConfirming: Boolean = false,
    val isCookRecorded: Boolean = false,
    val errorMessage: String? = null
) {
    val totalEstimatedSavings: Int
        get() = if (recipe != null) {
            (recipe.estimatedSavings.toDouble() / recipe.servings * cookedPortions).toInt()
        } else 0
}
