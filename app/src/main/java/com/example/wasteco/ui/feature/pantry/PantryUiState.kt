package com.example.wasteco.ui.feature.pantry

import com.example.wasteco.domain.model.PantryItem

data class PantryUiState(
    val items: List<PantryItem> = emptyList(),
    val selectedItemIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val isAdding: Boolean = false,
    val showAddDialog: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val selectedItemsCount: Int
        get() = selectedItemIds.size
}
