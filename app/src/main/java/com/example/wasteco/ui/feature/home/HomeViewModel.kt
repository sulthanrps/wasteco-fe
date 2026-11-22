package com.example.wasteco.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wasteco.WasteCoApplication
import com.example.wasteco.data.repository.AuthRepository
import com.example.wasteco.data.repository.DonationRepository
import com.example.wasteco.data.repository.PantryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val pantryRepository: PantryRepository,
    private val donationRepository: DonationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadHomeData() {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 1. Load User Profile
            val userResult = authRepository.getMe()
            userResult.onSuccess { user ->
                _uiState.update { it.copy(currentUser = user) }
            }

            // 2. Load Pantry Items
            val pantryResult = pantryRepository.getPantryIngredients()
            pantryResult.onSuccess { items ->
                _uiState.update { it.copy(pantryItems = items) }
            }.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message) }
            }
            val nearby = donationRepository.getNearbyDonations()
            _uiState.update {
                it.copy(
                    nearbyDonations = nearby.getOrNull().orEmpty(),
                    nearbyError = nearby.exceptionOrNull()?.message,
                    isLoading = false
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as WasteCoApplication)
                HomeViewModel(
                    authRepository = app.container.authRepository,
                    pantryRepository = app.container.pantryRepository,
                    donationRepository = app.container.donationRepository
                )
            }
        }
    }
}
