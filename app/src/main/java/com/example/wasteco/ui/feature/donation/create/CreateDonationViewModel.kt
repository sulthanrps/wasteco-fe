package com.example.wasteco.ui.feature.donation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wasteco.WasteCoApplication
import com.example.wasteco.data.repository.DonationRepository
import com.example.wasteco.domain.model.DonationItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateDonationViewModel(
    private val donationRepository: DonationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateDonationUiState())
    val uiState: StateFlow<CreateDonationUiState> = _uiState.asStateFlow()

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
                longitude = lng,
                areaName = areaName ?: it.areaName,
                isGpsLoading = false
            )
        }
    }

    fun setGpsLoading(loading: Boolean) {
        _uiState.update { it.copy(isGpsLoading = loading) }
    }

    fun onPickupTimeChange(time: String) {
        _uiState.update { it.copy(pickupTime = time) }
    }

    fun addIngredientToList() {
        val name = _uiState.value.inputIngredientName.trim()
        val qty = _uiState.value.inputQuantity.trim()
        val unit = _uiState.value.inputUnit.trim()

        if (name.isBlank()) return

        val newItem = DonationItem(name = name, quantity = "$qty $unit")
        _uiState.update {
            it.copy(
                addedItems = it.addedItems + newItem,
                inputIngredientName = ""
            )
        }
    }

    fun removeIngredient(item: DonationItem) {
        _uiState.update {
            it.copy(addedItems = it.addedItems - item)
        }
    }

    fun submitRequest() {
        val state = _uiState.value
        if (state.addedItems.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Tambahkan minimal satu bahan terlebih dahulu") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val firstItem = state.addedItems.first()
            val title = if (state.addedItems.size > 1) {
                "${firstItem.name} & Lainnya"
            } else {
                firstItem.name
            }

            val result = donationRepository.createDonation(
                title = title,
                category = "Sayuran & Bumbu",
                quantity = state.addedItems.size.toDouble(),
                unit = "item",
                conditionNote = "Bahan diminta oleh warga sekitar.",
                storageMethod = "Segar",
                pickupDeadline = state.pickupTime,
                areaName = state.areaName,
                meetingPoint = state.meetingPoint,
                latitude = state.latitude,
                longitude = state.longitude,
                whatsappNumber = "+6281358902144",
                estimatedPrice = 15000,
                estimatedPortions = state.addedItems.size
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
                    donationRepository = app.container.donationRepository
                )
            }
        }
    }
}
