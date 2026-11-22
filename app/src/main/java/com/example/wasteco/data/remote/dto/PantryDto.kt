package com.example.wasteco.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddPantryRequest(
    @SerialName("name") val name: String,
    @SerialName("quantity") val quantity: Double,
    @SerialName("unit") val unit: String,
    @SerialName("category") val category: String = "Lainnya",
    @SerialName("storage_location") val storageLocation: String = "Kulkas",
    @SerialName("expiry_date") val expiryDate: String,
    @SerialName("estimated_price") val estimatedPrice: Int = 0
)

@Serializable
data class PantryItemDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("quantity") val quantity: Double,
    @SerialName("unit") val unit: String,
    @SerialName("category") val category: String? = null,
    @SerialName("storage_location") val storageLocation: String? = null,
    @SerialName("expiry_date") val expiryDate: String? = null,
    @SerialName("estimated_price") val estimatedPrice: Int? = null
)

@Serializable
data class PantryListPayloadDto(
    @SerialName("ingredients") val ingredients: List<PantryItemDto> = emptyList(),
    @SerialName("total") val total: Int? = 0
)

@Serializable
data class PantryListResponse(
    @SerialName("payload") val payload: PantryListPayloadDto
)

@Serializable
data class PantryItemPayloadDto(
    @SerialName("ingredient") val ingredient: PantryItemDto
)

@Serializable
data class PantryItemResponse(
    @SerialName("payload") val payload: PantryItemPayloadDto
)

@Serializable
data class ScannedIngredientDto(val name: String, val quantity: Double, val unit: String)

@Serializable
data class PantryScanPayloadDto(val ingredients: List<ScannedIngredientDto>)

@Serializable
data class PantryScanResponse(val payload: PantryScanPayloadDto)

@Serializable
data class BulkPantryRequest(val items: List<ScannedIngredientDto>)
