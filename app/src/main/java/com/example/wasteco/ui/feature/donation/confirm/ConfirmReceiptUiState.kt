package com.example.wasteco.ui.feature.donation.confirm

import com.example.wasteco.domain.model.Donation

data class ConfirmReceiptUiState(
    val donation: Donation? = null,
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val isConfirmed: Boolean = false,
    val errorMessage: String? = null
)
