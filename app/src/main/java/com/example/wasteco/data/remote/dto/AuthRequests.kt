package com.example.wasteco.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)

@Serializable
data class RegisterRequest(
    @SerialName("name") val name: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("area_name") val areaName: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("sharing_radius_km") val sharingRadiusKm: Int
)

@Serializable
data class UpdateProfileRequest(
    val name: String,
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("area_name") val areaName: String,
    val latitude: Double,
    val longitude: Double,
    @SerialName("sharing_radius_km") val sharingRadiusKm: Int
)
