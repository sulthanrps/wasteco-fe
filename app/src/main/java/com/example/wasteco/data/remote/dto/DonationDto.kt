package com.example.wasteco.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DonationItemDto(
    val id: String,
    val name: String,
    val quantity: Double,
    val unit: String,
    val selected: Boolean = false
)

@Serializable
data class DonationUserDto(
    val id: String,
    val name: String,
    @SerialName("area_name") val areaName: String? = null,
    @SerialName("whatsapp_number") val whatsappNumber: String? = null,
    @SerialName("whatsapp_url") val whatsappUrl: String? = null
)

@Serializable
data class DonationDto(
    val id: String,
    val title: String,
    val note: String? = null,
    val items: List<DonationItemDto> = emptyList(),
    @SerialName("area_name") val areaName: String? = null,
    @SerialName("meeting_point") val meetingPoint: String,
    val latitude: Double,
    val longitude: Double,
    @SerialName("receive_start") val receiveStart: String,
    @SerialName("receive_end") val receiveEnd: String,
    @SerialName("formatted_distance") val formattedDistance: String? = null,
    val status: String,
    @SerialName("created_at") val createdAt: String? = null,
    val requester: DonationUserDto,
    val donor: DonationUserDto? = null
)

@Serializable
data class DonationListPayloadDto(val donations: List<DonationDto>)

@Serializable
data class DonationListResponse(val payload: DonationListPayloadDto)

@Serializable
data class DonationActivityPayloadDto(
    val active: List<DonationDto>,
    val history: List<DonationDto>
)

@Serializable
data class DonationActivityResponse(val payload: DonationActivityPayloadDto)

@Serializable
data class DonationDetailPayloadDto(val donation: DonationDto)

@Serializable
data class DonationDetailResponse(val payload: DonationDetailPayloadDto)

@Serializable
data class DonationRequestItemDto(val name: String, val quantity: Double, val unit: String)

@Serializable
data class CreateDonationRequest(
    val items: List<DonationRequestItemDto>,
    @SerialName("area_name") val areaName: String?,
    @SerialName("meeting_point") val meetingPoint: String,
    val latitude: Double,
    val longitude: Double,
    @SerialName("receive_start") val receiveStart: String,
    @SerialName("receive_end") val receiveEnd: String
)

@Serializable
data class FulfillDonationRequest(@SerialName("item_ids") val itemIds: List<String>)
