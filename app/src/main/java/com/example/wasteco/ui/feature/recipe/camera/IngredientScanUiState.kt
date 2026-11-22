package com.example.wasteco.ui.feature.recipe.camera

data class ReviewedIngredient(
    val name: String,
    val quantity: String,
    val unit: String
)

data class IngredientScanUiState(
    val photo: ByteArray? = null,
    val mediaType: String? = null,
    val items: List<ReviewedIngredient> = emptyList(),
    val isReadingPhoto: Boolean = false,
    val isScanning: Boolean = false,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val errorMessage: String? = null,
    val errorResource: Int? = null
)
