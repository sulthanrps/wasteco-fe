package com.example.wasteco.ui.feature.donation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wasteco.WasteCoApplication
import com.example.wasteco.data.repository.DonationRepository
import com.example.wasteco.data.repository.AuthRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DonationMapViewModel(
    private val donationRepository: DonationRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DonationMapUiState())
    val uiState: StateFlow<DonationMapUiState> = _uiState.asStateFlow()

    private var requestsJob: Job? = null

    init {
        requestsJob = viewModelScope.launch {
            val user = authRepository.getMe().getOrNull()
            if (user != null && (user.latitude != 0.0 || user.longitude != 0.0)) {
                updateUserLocation(user.latitude, user.longitude, user.areaName)
            } else {
                loadNearbyDonations()
            }
        }
    }

    fun loadNearbyDonations(
        lat: Double? = _uiState.value.userLatitude.takeIf { _uiState.value.hasLocation },
        lng: Double? = _uiState.value.userLongitude.takeIf { _uiState.value.hasLocation }
    ) {
        requestsJob?.cancel()
        requestsJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, donations = emptyList()) }
            val result = donationRepository.getNearbyDonations(latitude = lat, longitude = lng)
            result.onSuccess { list ->
                _uiState.update {
                    it.copy(
                        donations = list,
                        selectedDonationId = it.selectedDonationId?.takeIf { id -> list.any { item -> item.id == id } }
                            ?: list.firstOrNull()?.id,
                        isLoading = false
                    )
                }
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
            }
        }
    }

    fun setLocationSheetVisible(visible: Boolean) {
        _uiState.update { it.copy(isLocationSheetVisible = visible) }
    }

    fun setGpsLoading(loading: Boolean) {
        _uiState.update { it.copy(isGpsLoading = loading) }
    }

    fun updateUserLocation(lat: Double, lng: Double, address: String? = null) {
        val newAddress = address ?: _uiState.value.userAddress
        _uiState.update {
            it.copy(
                userLatitude = lat,
                hasLocation = true,
                userLongitude = lng,
                userAddress = newAddress,
                isGpsLoading = false,
                isLocationSheetVisible = false
            )
        }
        loadNearbyDonations(lat, lng)
    }

    fun selectDonation(id: String) {
        _uiState.update { it.copy(selectedDonationId = id) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as WasteCoApplication)
                DonationMapViewModel(
                    donationRepository = app.container.donationRepository,
                    authRepository = app.container.authRepository
                )
            }
        }
    }
}
