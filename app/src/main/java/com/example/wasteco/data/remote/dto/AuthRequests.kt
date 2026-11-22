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
    @SerialName("phone_number") val phoneNumber: String = "+6281234567890",
    @SerialName("area_name") val areaName: String,
    @SerialName("latitude") val latitude: Double = -7.9528,
    @SerialName("longitude") val longitude: Double = 112.6074,
    @SerialName("sharing_radius_km") val sharingRadiusKm: Int = 3
)
