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

    fun loadDonation(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = donationRepository.getDonationDetail(id)
            result.onSuccess { donation ->
                val initialSelected = donation.items.take(2).map { it.name }.toSet()
                _uiState.update {
                    it.copy(
                        donation = donation,
                        selectedItemNames = initialSelected,
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun toggleItemSelection(name: String) {
        _uiState.update { state ->
            val set = state.selectedItemNames.toMutableSet()
            if (set.contains(name)) {
                set.remove(name)
            } else {
                set.add(name)
            }
            state.copy(selectedItemNames = set)
        }
    }

    fun submitHandover() {
        val donation = _uiState.value.donation ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            donationRepository.confirmHandover(donation.id)
            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    showHandoverSheet = true
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
