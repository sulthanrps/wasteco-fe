package com.example.wasteco.domain.model

data class PantryItem(
    val id: String,
    val name: String,
    val quantity: Double,
    val unit: String,
    val category: String,
    val storageLocation: String,
    val expiryDate: String,
    val estimatedPrice: Int
) {
    val displayQuantity: String
        get() = if (quantity % 1.0 == 0.0) "${quantity.toInt()} $unit" else "$quantity $unit"
}
