package com.example.wasteco.ui.feature.donation

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

class DonationHubViewModel(
    private val donationRepository: DonationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DonationHubUiState())
    val uiState: StateFlow<DonationHubUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val nearbyRes = donationRepository.getNearbyDonations()
            val myRes = donationRepository.getMyDonations()
            val givenRes = donationRepository.getGivenDonations()

            _uiState.update {
                it.copy(
                    nearbyDonations = nearbyRes.getOrDefault(emptyList()),
                    myDonations = myRes.getOrDefault(emptyList()),
                    givenDonations = givenRes.getOrDefault(emptyList()),
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

    fun confirmHandoverAsDonor(id: String) {
        viewModelScope.launch {
            donationRepository.confirmHandover(id)
            loadData()
        }
    }

    fun cancelMyDonation(id: String) {
        viewModelScope.launch {
            donationRepository.cancelDonation(id)
            loadData()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as WasteCoApplication)
                DonationHubViewModel(
                    donationRepository = app.container.donationRepository
                )
            }
        }
    }
}
