package com.example.wasteco.ui.feature.donation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wasteco.WasteCoApplication
import com.example.wasteco.data.repository.DonationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DonationDetailViewModel(
    private val donationRepository: DonationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DonationDetailUiState())
    val uiState: StateFlow<DonationDetailUiState> = _uiState.asStateFlow()

    fun loadDonation(id: String, latitude: Double?, longitude: Double?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = donationRepository.getDonationDetail(id, latitude, longitude)
            result.onSuccess { donation ->
                val initialSelected = donation.items.filter { it.selected }.map { it.id }.toSet()
                _uiState.update {
                    it.copy(
                        donation = donation,
                        selectedItemIds = initialSelected,
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun toggleItemSelection(id: String) {
        _uiState.update { state ->
            val set = state.selectedItemIds.toMutableSet()
            if (set.contains(id)) {
                set.remove(id)
            } else {
                set.add(id)
            }
            state.copy(selectedItemIds = set)
        }
    }

    fun submitHandover() {
        val state = _uiState.value
        val donation = state.donation ?: return
        if (state.isSubmitting || donation.status != "open" || state.selectedItemIds.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = donationRepository.fulfillDonation(donation.id, state.selectedItemIds.toList())
            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    donation = result.getOrDefault(donation),
                    errorMessage = result.exceptionOrNull()?.message,
                    showHandoverSheet = result.isSuccess
                )
            }
        }
    }

    fun dismissHandoverSheet() {
        _uiState.update { it.copy(showHandoverSheet = false) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as WasteCoApplication)
                DonationDetailViewModel(
                    donationRepository = app.container.donationRepository
                )
            }
        }
    }
}
