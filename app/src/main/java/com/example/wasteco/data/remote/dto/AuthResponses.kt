package com.example.wasteco.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("id") val id: String,
    @SerialName("email") val email: String,
    @SerialName("name") val name: String,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("area_name") val areaName: String? = null,
    @SerialName("latitude") val latitude: Double? = 0.0,
    @SerialName("longitude") val longitude: Double? = 0.0,
    @SerialName("sharing_radius_km") val sharingRadiusKm: Int? = 3,
    @SerialName("total_portions_saved") val totalPortionsSaved: Int? = 0
)

@Serializable
data class AuthPayloadDto(
    @SerialName("token") val token: String,
    @SerialName("user") val user: UserDto
)

@Serializable
data class AuthResponse(
    @SerialName("payload") val payload: AuthPayloadDto
)

@Serializable
data class MePayloadDto(
    @SerialName("user") val user: UserDto
)

@Serializable
data class MeResponse(
    @SerialName("payload") val payload: MePayloadDto
)

@Serializable
data class ErrorResponse(
    @SerialName("error_code") val errorCode: String? = null,
    @SerialName("error") val error: String? = null,
    @SerialName("message") val message: String? = null
)
