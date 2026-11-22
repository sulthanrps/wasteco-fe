package com.example.wasteco.ui.feature.donation.detail

import com.example.wasteco.domain.model.Donation

data class DonationDetailUiState(
    val donation: Donation? = null,
    val selectedItemIds: Set<String> = emptySet(),
    val showHandoverSheet: Boolean = false,
    val isSubmitting: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
