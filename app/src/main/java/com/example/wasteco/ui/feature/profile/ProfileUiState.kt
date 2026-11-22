package com.example.wasteco.ui.feature.profile

import com.example.wasteco.domain.model.User

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isLocating: Boolean = false,
    val saved: Boolean = false,
    val errorMessage: String? = null,
    val errorResource: Int? = null
)
