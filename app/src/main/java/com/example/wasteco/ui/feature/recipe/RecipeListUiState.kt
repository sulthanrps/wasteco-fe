package com.example.wasteco.ui.feature.recipe

import com.example.wasteco.domain.model.Recipe

data class RecipeListUiState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
