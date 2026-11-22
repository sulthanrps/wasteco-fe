package com.example.wasteco.ui.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wasteco.R
import com.example.wasteco.WasteCoApplication
import com.example.wasteco.data.repository.AuthRepository
import com.example.wasteco.data.repository.normalizedWhatsAppNumber
import com.example.wasteco.domain.model.DONATION_RADIUS_KM
import com.example.wasteco.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(ProfileUiState())
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = ProfileUiState()
        viewModelScope.launch {
            val result = repository.getMe()
            _state.value = ProfileUiState(
                user = result.getOrNull(),
                isLoading = false,
                errorMessage = result.exceptionOrNull()?.message
            )
        }
    }

    fun changeName(name: String) = edit { copy(name = name) }
    fun changePhone(phone: String) = edit { copy(phoneNumber = phone) }
    fun changeArea(area: String) = edit { copy(areaName = area) }

    fun locating() {
        _state.update { it.copy(isLocating = true, errorResource = null) }
    }

    fun setLocation(latitude: Double, longitude: Double, areaName: String?) {
        _state.update { state ->
            state.copy(
                user = state.user?.copy(
                    latitude = latitude,
                    longitude = longitude,
                    areaName = areaName ?: state.user.areaName
                ),
                isLocating = false,
                saved = false,
                errorMessage = null,
                errorResource = if (areaName == null) R.string.profile_area_unavailable else null
            )
        }
    }

    fun locationUnavailable() {
        _state.update { it.copy(isLocating = false, errorResource = R.string.profile_location_unavailable) }
    }

    fun save() {
        val state = _state.value
        val user = state.user ?: return
        if (state.isSaving || state.isLocating) return
        val phone = user.phoneNumber.normalizedWhatsAppNumber()
        if (phone == null || user.name.isBlank() || user.areaName.isBlank()) {
            _state.update { it.copy(errorResource = R.string.profile_invalid_fields, saved = false) }
            return
        }
        val updated = user.copy(
            name = user.name.trim(),
            phoneNumber = phone,
            areaName = user.areaName.trim(),
            sharingRadiusKm = DONATION_RADIUS_KM
        )
        _state.update { it.copy(isSaving = true, errorResource = null, errorMessage = null, saved = false) }
        viewModelScope.launch {
            val result = repository.updateProfile(updated)
            _state.update {
                it.copy(
                    user = if (result.isSuccess) updated else it.user,
                    isSaving = false,
                    saved = result.isSuccess,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch { repository.logout() }
    }

    private fun edit(transform: User.() -> User) {
        _state.update { it.copy(user = it.user?.transform(), saved = false, errorResource = null, errorMessage = null) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as WasteCoApplication
                ProfileViewModel(app.container.authRepository)
            }
        }
    }
}
