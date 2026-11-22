package com.example.wasteco.ui.feature.donation.create

import com.example.wasteco.domain.model.DonationItem

data class CreateDonationUiState(
    val inputIngredientName: String = "",
    val inputQuantity: String = "2",
    val inputUnit: String = "porsi",
    val addedItems: List<DonationItem> = listOf(
        DonationItem("Wortel", "500 gram"),
        DonationItem("Telur Ayam", "4 butir"),
        DonationItem("Nasi Putih", "2 porsi")
    ),
    val areaName: String = "Dinoyo, Malang",
    val meetingPoint: String = "Depan Minimarket Dinoyo",
    val latitude: Double = -7.9535,
    val longitude: Double = 112.6080,
    val pickupTime: String = "Hari ini, 16.00 – 19.00",
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val isGpsLoading: Boolean = false,
    val errorMessage: String? = null
)
