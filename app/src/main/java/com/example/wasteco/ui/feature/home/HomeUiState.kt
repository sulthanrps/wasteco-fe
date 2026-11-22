package com.example.wasteco.ui.feature.home

import com.example.wasteco.domain.model.PantryItem
import com.example.wasteco.domain.model.User

data class HomeUiState(
    val currentUser: User? = null,
    val pantryItems: List<PantryItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val totalIngredientsCount: Int
        get() = pantryItems.size
}
