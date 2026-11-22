package com.example.wasteco.ui.feature.home

import com.example.wasteco.domain.model.Donation
import com.example.wasteco.domain.model.PantryItem
import com.example.wasteco.domain.model.User

data class HomeUiState(
    val currentUser: User? = null,
    val nearbyDonations: List<Donation> = emptyList(),
    val nearbyError: String? = null,
    val pantryItems: List<PantryItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val totalIngredientsCount: Int
        get() = pantryItems.size
}
