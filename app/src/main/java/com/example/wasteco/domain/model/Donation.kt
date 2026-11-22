package com.example.wasteco.domain.model

data class DonationItem(
    val name: String,
    val quantity: String
)

data class DonationUser(
    val id: String,
    val name: String,
    val areaName: String,
    val photoUrl: String?,
    val isVerified: Boolean
)

enum class DonationRole {
    REQUESTER,
    DONOR
}

data class Donation(
    val id: String,
    val title: String,
    val category: String,
    val itemsSummary: String,
    val items: List<DonationItem>,
    val conditionNote: String,
    val storageMethod: String,
    val pickupDeadline: String,
    val areaName: String,
    val meetingPoint: String,
    val latitude: Double,
    val longitude: Double,
    val distanceDesc: String,
    val whatsappNumber: String,
    val estimatedPrice: Int,
    val estimatedPortions: Int,
    val photoUrl: String,
    val status: String,
    val step: Int,
    val createdTimeAgo: String,
    val user: DonationUser,
    val donor: DonationUser?
)
