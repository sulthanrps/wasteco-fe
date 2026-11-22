package com.example.wasteco.ui.feature.donation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wasteco.WasteCoApplication
import com.example.wasteco.data.repository.DonationRepository
import com.example.wasteco.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DonationHubViewModel(
    private val donationRepository: DonationRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DonationHubUiState())
    val uiState: StateFlow<DonationHubUiState> = _uiState.asStateFlow()

    fun loadData() {
        if (_uiState.value.isLoading || _uiState.value.isUpdating) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val user = authRepository.getMe().getOrNull()
            val nearbyRes = donationRepository.getNearbyDonations()
            val myRes = donationRepository.getMyDonations()
            val givenRes = donationRepository.getGivenDonations()

            _uiState.update {
                it.copy(
                    nearbyDonations = nearbyRes.getOrDefault(emptyList()),
                    myDonations = myRes.getOrDefault(emptyList()),
                    givenDonations = givenRes.getOrDefault(emptyList()),
                    currentLocation = user?.areaName.orEmpty(),
                    errorMessage = (nearbyRes.exceptionOrNull() ?: myRes.exceptionOrNull() ?: givenRes.exceptionOrNull())?.message,
                    isLoading = false
                )
            }
        }
    }

    fun selectTab(tab: DonationTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun selectActivityFilter(filter: ActivityFilter) {
        _uiState.update { it.copy(selectedActivityFilter = filter) }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun cancelMyDonation(id: String) {
        if (_uiState.value.isUpdating) return
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null) }
            val result = donationRepository.cancelDonation(id)
            _uiState.update { it.copy(isUpdating = false, errorMessage = result.exceptionOrNull()?.message) }
            if (result.isSuccess) loadData()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as WasteCoApplication)
                DonationHubViewModel(
                    donationRepository = app.container.donationRepository,
                    authRepository = app.container.authRepository
                )
            }
        }
    }
}
