package com.example.wasteco.data.mapper

import com.example.wasteco.data.remote.dto.DonationDto
import com.example.wasteco.data.remote.dto.DonationUserDto
import com.example.wasteco.domain.model.Donation
import com.example.wasteco.domain.model.DonationItem
import com.example.wasteco.domain.model.DonationUser
import java.net.URI
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

private fun DonationUserDto.toDomain() = DonationUser(
    id = id,
    name = name,
    areaName = areaName.orEmpty(),
    photoUrl = null,
    isVerified = false,
    whatsappUrl = whatsappUrl?.takeIf { url ->
        val uri = runCatching { URI(url) }.getOrNull()
        uri?.scheme == "https" && uri.host in setOf("wa.me", "api.whatsapp.com")
    }
)

fun DonationDto.toDomain(): Donation {
    val timeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    val start = OffsetDateTime.parse(receiveStart).format(timeFormat)
    val end = OffsetDateTime.parse(receiveEnd).format(timeFormat)
    return Donation(
        id = id,
        title = title,
        category = "",
        itemsSummary = items.joinToString(" • ") { it.name },
        items = items.map {
            val quantity = it.quantity.toBigDecimal().stripTrailingZeros().toPlainString()
            DonationItem(it.name, "$quantity ${it.unit}", it.id, it.selected)
        },
        conditionNote = note.orEmpty(),
        storageMethod = "",
        pickupDeadline = "$start – $end",
        areaName = areaName.orEmpty(),
        meetingPoint = meetingPoint,
        latitude = latitude,
        longitude = longitude,
        distanceDesc = formattedDistance.orEmpty(),
        whatsappNumber = requester.whatsappNumber.orEmpty(),
        estimatedPrice = 0,
        estimatedPortions = items.count { it.selected },
        photoUrl = "",
        status = status,
        step = when (status) {
            "handed_over" -> 2
            "received" -> 3
            else -> 1
        },
        createdTimeAgo = createdAt?.let { OffsetDateTime.parse(it).format(timeFormat) }.orEmpty(),
        user = requester.toDomain(),
        donor = donor?.toDomain()
    )
}
