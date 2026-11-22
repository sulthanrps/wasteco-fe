package com.example.wasteco.data.mapper

import com.example.wasteco.data.remote.dto.DonationDto
import com.example.wasteco.data.remote.dto.DonationItemDetailDto
import com.example.wasteco.data.remote.dto.DonorUserDto
import com.example.wasteco.domain.model.Donation
import com.example.wasteco.domain.model.DonationItem
import com.example.wasteco.domain.model.DonationUser

fun DonationItemDetailDto.toDomain(): DonationItem {
    return DonationItem(
        name = name,
        quantity = quantity
    )
}

fun DonorUserDto.toDomain(): DonationUser {
    return DonationUser(
        id = id ?: "user_default",
        name = name,
        areaName = areaName ?: "Dinoyo, Malang",
        photoUrl = photoUrl,
        isVerified = isVerified
    )
}

fun DonationDto.toDomain(): Donation {
    val distance = distanceDesc ?: if (distanceMeters != null && distanceMeters > 0) {
        if (distanceMeters >= 1000) "${distanceMeters / 1000.0} km" else "$distanceMeters m"
    } else "800 m"

    val itemsList = if (items.isNotEmpty()) {
        items.map { it.toDomain() }
    } else {
        listOf(DonationItem(name = title, quantity = "${quantity ?: 1} ${unit ?: "item"}"))
    }

    val summary = itemsSummary ?: itemsList.joinToString(" • ") { it.name }

    val defaultPhoto = when {
        title.contains("wortel", ignoreCase = true) -> "https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?auto=format&fit=crop&w=600&q=80"
        title.contains("kentang", ignoreCase = true) -> "https://images.unsplash.com/photo-1518977676601-b53f82aba655?auto=format&fit=crop&w=600&q=80"
        title.contains("tomat", ignoreCase = true) -> "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?auto=format&fit=crop&w=600&q=80"
        title.contains("telur", ignoreCase = true) -> "https://images.unsplash.com/photo-1582722872445-44dc5f7e3c8f?auto=format&fit=crop&w=600&q=80"
        else -> "https://images.unsplash.com/photo-1610832958506-aa56368176cf?auto=format&fit=crop&w=600&q=80"
    }

    return Donation(
        id = id,
        title = title,
        category = category ?: "Bahan Masak",
        itemsSummary = summary,
        items = itemsList,
        conditionNote = conditionNote.orEmpty(),
        storageMethod = storageMethod ?: "Suhu ruang",
        pickupDeadline = pickupDeadline ?: "Hari ini, 16.00 – 19.00",
        areaName = areaName ?: "Dinoyo, Malang",
        meetingPoint = meetingPoint ?: "Depan Minimarket Dinoyo",
        latitude = latitude ?: -7.9535,
        longitude = longitude ?: 112.608,
        distanceDesc = distance,
        whatsappNumber = whatsappNumber ?: "+6281234567890",
        estimatedPrice = estimatedPrice ?: 10000,
        estimatedPortions = estimatedPortions ?: 2,
        photoUrl = photoUrl ?: defaultPhoto,
        status = status,
        step = step,
        createdTimeAgo = createdTimeAgo ?: "1 jam lalu",
        user = user?.toDomain() ?: DonationUser("1", "Budi Santoso", "Warga Dinoyo", null, true),
        donor = donor?.toDomain()
    )
}
