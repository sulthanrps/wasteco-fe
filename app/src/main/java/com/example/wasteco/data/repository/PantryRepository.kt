package com.example.wasteco.data.repository

import com.example.wasteco.domain.model.PantryItem

data class ScannedIngredient(val name: String, val quantity: Double, val unit: String)

interface PantryRepository {
    suspend fun scanIngredients(image: ByteArray, mediaType: String): Result<List<ScannedIngredient>>
    suspend fun addScannedIngredients(items: List<ScannedIngredient>): Result<List<PantryItem>>
    suspend fun getPantryIngredients(): Result<List<PantryItem>>
    suspend fun addPantryIngredient(
        name: String,
        quantity: Double,
        unit: String,
        category: String,
        storageLocation: String,
        expiryDate: String,
        estimatedPrice: Int
    ): Result<PantryItem>
    suspend fun deletePantryIngredient(id: String): Result<Unit>
}
