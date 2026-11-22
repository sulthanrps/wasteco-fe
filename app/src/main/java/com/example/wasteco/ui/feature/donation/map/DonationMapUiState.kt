package com.example.wasteco.ui.feature.donation.map

import com.example.wasteco.domain.model.Donation

data class DonationMapUiState(
    val donations: List<Donation> = emptyList(),
    val selectedDonationId: String? = null,
    val userLatitude: Double = -7.9535,
    val userLongitude: Double = 112.608,
    val userAddress: String = "Dinoyo, Malang",
    val isLoading: Boolean = false,
    val isGpsLoading: Boolean = false,
    val isLocationSheetVisible: Boolean = false,
    val errorMessage: String? = null
) {
    val selectedDonation: Donation?
        get() = donations.firstOrNull { it.id == selectedDonationId } ?: donations.firstOrNull()
}
