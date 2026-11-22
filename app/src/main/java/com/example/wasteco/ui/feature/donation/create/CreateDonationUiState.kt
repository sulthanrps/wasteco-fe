package com.example.wasteco.ui.feature.donation.create

import com.example.wasteco.domain.model.DonationRequestItem
import java.time.LocalDateTime

data class CreateDonationUiState(
    val inputIngredientName: String = "",
    val inputQuantity: String = "",
    val inputUnit: String = "porsi",
    val addedItems: List<DonationRequestItem> = emptyList(),
    val areaName: String = "",
    val meetingPoint: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val hasLocation: Boolean = false,
    val receiveStart: LocalDateTime? = null,
    val receiveEnd: LocalDateTime? = null,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val isGpsLoading: Boolean = false,
    val errorMessage: String? = null,
    val errorResource: Int? = null
)
