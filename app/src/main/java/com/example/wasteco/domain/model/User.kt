package com.example.wasteco.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val phoneNumber: String,
    val areaName: String,
    val latitude: Double,
    val longitude: Double,
    val sharingRadiusKm: Int,
    val totalPortionsSaved: Int
)
