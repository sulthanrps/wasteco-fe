package com.example.wasteco.data.mapper

import com.example.wasteco.data.remote.dto.UserDto
import com.example.wasteco.domain.model.User

fun UserDto.toDomain(): User {
    return User(
        id = id,
        email = email,
        name = name,
        phoneNumber = phoneNumber.orEmpty(),
        areaName = areaName.orEmpty(),
        latitude = latitude ?: 0.0,
        longitude = longitude ?: 0.0,
        sharingRadiusKm = sharingRadiusKm ?: 3,
        totalPortionsSaved = totalPortionsSaved ?: 0
    )
}
