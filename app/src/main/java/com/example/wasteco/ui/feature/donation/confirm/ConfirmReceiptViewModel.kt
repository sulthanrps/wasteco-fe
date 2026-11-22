package com.example.wasteco.ui.feature.donation.confirm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wasteco.WasteCoApplication
import com.example.wasteco.data.repository.DonationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConfirmReceiptViewModel(private val repository: DonationRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ConfirmReceiptUiState())
    val uiState = _uiState.asStateFlow()

    fun load(id: String) {
        viewModelScope.launch {
            _uiState.value = ConfirmReceiptUiState()
            val result = repository.getDonationDetail(id)
            _uiState.value = ConfirmReceiptUiState(
                donation = result.getOrNull(),
                isLoading = false,
                errorMessage = result.exceptionOrNull()?.message
            )
        }
    }

    fun confirm() {
        val state = _uiState.value
        val donation = state.donation ?: return
        if (state.isSubmitting || state.isConfirmed || donation.status != "handed_over") return
        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            val result = repository.confirmReceipt(donation.id)
            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    isConfirmed = result.isSuccess,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as WasteCoApplication
                ConfirmReceiptViewModel(app.container.donationRepository)
            }
        }
    }
}
