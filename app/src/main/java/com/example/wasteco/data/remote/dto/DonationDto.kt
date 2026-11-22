package com.example.wasteco.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DonationItemDetailDto(
    @SerialName("name") val name: String,
    @SerialName("quantity") val quantity: String = ""
)

@Serializable
data class DonorUserDto(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String = "Warga WasteCo",
    @SerialName("area_name") val areaName: String? = "Dinoyo, Malang",
    @SerialName("photo_url") val photoUrl: String? = null,
    @SerialName("is_verified") val isVerified: Boolean = true
)

@Serializable
data class DonationDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("category") val category: String? = null,
    @SerialName("quantity") val quantity: Double? = null,
    @SerialName("unit") val unit: String? = null,
    @SerialName("items_summary") val itemsSummary: String? = null,
    @SerialName("items") val items: List<DonationItemDetailDto> = emptyList(),
    @SerialName("condition_note") val conditionNote: String? = null,
    @SerialName("storage_method") val storageMethod: String? = null,
    @SerialName("pickup_deadline") val pickupDeadline: String? = null,
    @SerialName("area_name") val areaName: String? = "Dinoyo, Malang",
    @SerialName("meeting_point") val meetingPoint: String? = null,
    @SerialName("latitude") val latitude: Double? = -7.9535,
    @SerialName("longitude") val longitude: Double? = 112.608,
    @SerialName("distance_meters") val distanceMeters: Int? = 800,
    @SerialName("distance_desc") val distanceDesc: String? = null,
    @SerialName("whatsapp_number") val whatsappNumber: String? = null,
    @SerialName("estimated_price") val estimatedPrice: Int? = 0,
    @SerialName("estimated_portions") val estimatedPortions: Int? = 2,
    @SerialName("photo_url") val photoUrl: String? = null,
    @SerialName("status") val status: String = "active",
    @SerialName("step") val step: Int = 1,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("created_time_ago") val createdTimeAgo: String? = "1 jam lalu",
    @SerialName("user") val user: DonorUserDto? = null,
    @SerialName("donor") val donor: DonorUserDto? = null
)

@Serializable
data class DonationListPayloadDto(
    @SerialName("total") val total: Int = 0,
    @SerialName("donations") val donations: List<DonationDto> = emptyList()
)

@Serializable
data class DonationListResponse(
    @SerialName("payload") val payload: DonationListPayloadDto
)

@Serializable
data class DonationDetailPayloadDto(
    @SerialName("donation") val donation: DonationDto
)

@Serializable
data class DonationDetailResponse(
    @SerialName("payload") val payload: DonationDetailPayloadDto
)

@Serializable
data class DonationActionResponse(
    @SerialName("message") val message: String? = null
)
