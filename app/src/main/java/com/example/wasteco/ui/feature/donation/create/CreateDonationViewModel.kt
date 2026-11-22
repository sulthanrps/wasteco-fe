package com.example.wasteco.ui.feature.donation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wasteco.R
import com.example.wasteco.WasteCoApplication
import com.example.wasteco.data.repository.DonationRepository
import com.example.wasteco.domain.model.DonationRequest
import com.example.wasteco.domain.model.DonationRequestItem
import com.example.wasteco.data.repository.AuthRepository
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateDonationViewModel(
    private val donationRepository: DonationRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateDonationUiState())
    val uiState: StateFlow<CreateDonationUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.getMe().onSuccess { user ->
                _uiState.update { state ->
                    if (state.hasLocation || user.latitude == 0.0 && user.longitude == 0.0) state else {
                        state.copy(latitude = user.latitude, longitude = user.longitude,
                            areaName = user.areaName, hasLocation = true)
                    }
                }
            }
        }
    }

    fun onIngredientNameChange(name: String) {
        _uiState.update { it.copy(inputIngredientName = name) }
    }

    fun onQuantityChange(qty: String) {
        _uiState.update { it.copy(inputQuantity = qty) }
    }

    fun onUnitChange(unit: String) {
        _uiState.update { it.copy(inputUnit = unit) }
    }

    fun onMeetingPointChange(point: String) {
        _uiState.update { it.copy(meetingPoint = point) }
    }

    fun onLocationChange(lat: Double, lng: Double, areaName: String?) {
        _uiState.update {
            it.copy(
                latitude = lat,
                hasLocation = true,
                longitude = lng,
                areaName = areaName ?: it.areaName,
                isGpsLoading = false
            )
        }
    }

    fun setGpsLoading(loading: Boolean) {
        _uiState.update { it.copy(isGpsLoading = loading) }
    }

    fun onReceiveStartChange(time: LocalDateTime) {
        _uiState.update { it.copy(receiveStart = time, errorResource = it.errorResource.takeUnless { it == R.string.donation_invalid_time }) }
    }

    fun onReceiveEndChange(time: LocalDateTime) {
        _uiState.update { it.copy(receiveEnd = time, errorResource = it.errorResource.takeUnless { it == R.string.donation_invalid_time }) }
    }

    fun addIngredientToList() {
        val name = _uiState.value.inputIngredientName.trim()
        val qty = _uiState.value.inputQuantity.trim().replace(',', '.').toDoubleOrNull()
        val unit = _uiState.value.inputUnit.trim()

        if (name.isBlank() || qty == null || !qty.isFinite() || qty <= 0 || unit.isBlank()) {
            _uiState.update { it.copy(errorMessage = null, errorResource = R.string.donation_invalid_item) }
            return
        }

        val newItem = DonationRequestItem(name, qty, unit)
        _uiState.update {
            it.copy(
                addedItems = it.addedItems + newItem,
                inputIngredientName = "",
                inputQuantity = "",
                errorMessage = null,
                errorResource = null
            )
        }
    }

    fun removeIngredient(item: DonationRequestItem) {
        _uiState.update {
            it.copy(addedItems = it.addedItems - item)
        }
    }

    fun submitRequest() {
        val state = _uiState.value
        if (state.isSubmitting || state.isSuccess) return
        if (state.addedItems.isEmpty()) {
            _uiState.update { it.copy(errorMessage = null, errorResource = R.string.donation_empty_items) }
            return
        }

        val zone = ZoneId.systemDefault()
        val start = state.receiveStart?.atZone(zone)
        val end = state.receiveEnd?.atZone(zone)
        if (!state.hasLocation || state.meetingPoint.isBlank()) {
            _uiState.update { it.copy(errorMessage = null, errorResource = R.string.donation_invalid_location) }
            return
        }
        if (start == null || end == null || !end.isAfter(start) || !end.toInstant().isAfter(Instant.now())) {
            _uiState.update { it.copy(errorMessage = null, errorResource = R.string.donation_invalid_time) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null, errorResource = null) }
            val result = donationRepository.createDonation(
                DonationRequest(
                    items = state.addedItems,
                    areaName = state.areaName.trim().ifBlank { null },
                    meetingPoint = state.meetingPoint.trim(),
                    latitude = state.latitude,
                    longitude = state.longitude,
                    receiveStart = start.toOffsetDateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                    receiveEnd = end.toOffsetDateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                )
            )

            result.onSuccess {
                _uiState.update { it.copy(isSubmitting = false, isSuccess = true) }
            }.onFailure { error ->
                _uiState.update { it.copy(isSubmitting = false, errorMessage = error.message) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as WasteCoApplication)
                CreateDonationViewModel(
                    donationRepository = app.container.donationRepository,
                    authRepository = app.container.authRepository
                )
            }
        }
    }
}
