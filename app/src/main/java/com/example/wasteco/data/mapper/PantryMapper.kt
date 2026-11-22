package com.example.wasteco.data.mapper

import com.example.wasteco.data.remote.dto.PantryItemDto
import com.example.wasteco.domain.model.PantryItem

fun PantryItemDto.toDomain(): PantryItem {
    return PantryItem(
        id = id,
        name = name,
        quantity = quantity,
        unit = unit,
        category = category ?: "Lainnya",
        storageLocation = storageLocation ?: "Kulkas",
        expiryDate = expiryDate.orEmpty(),
        estimatedPrice = estimatedPrice ?: 0
    )
}
