package com.example.wasteco.data.repository

import com.example.wasteco.domain.model.PantryItem

interface PantryRepository {
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
