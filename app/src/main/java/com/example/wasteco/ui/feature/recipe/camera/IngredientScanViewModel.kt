package com.example.wasteco.ui.feature.recipe.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wasteco.R
import com.example.wasteco.WasteCoApplication
import com.example.wasteco.data.repository.PantryRepository
import com.example.wasteco.data.repository.ScannedIngredient
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class IngredientScanViewModel(private val pantryRepository: PantryRepository) : ViewModel() {
    private val _state = MutableStateFlow(IngredientScanUiState())
    val state = _state.asStateFlow()
    private var scanJob: Job? = null

    fun readingPhoto() {
        _state.update { it.copy(isReadingPhoto = true, errorResource = null) }
    }

    fun cameraReady() {
        _state.update { it.copy(isReadingPhoto = false) }
    }

    fun photoUnavailable(message: Int) {
        _state.update { it.copy(isReadingPhoto = false, errorResource = message) }
    }

    fun setPhoto(bytes: ByteArray, mediaType: String) {
        _state.value = IngredientScanUiState(photo = bytes, mediaType = mediaType)
        scan()
    }

    fun scan() {
        val state = _state.value
        val image = state.photo ?: return
        val mediaType = state.mediaType ?: return
        if (state.isScanning || state.isSaving) return
        _state.update { it.copy(isScanning = true, items = emptyList(), errorMessage = null, errorResource = null) }
        scanJob = viewModelScope.launch {
            val result = pantryRepository.scanIngredients(image, mediaType)
            _state.update {
                it.copy(
                    items = result.getOrNull().orEmpty().map { item ->
                        ReviewedIngredient(item.name, item.quantity.toBigDecimal().stripTrailingZeros().toPlainString(), item.unit)
                    },
                    isScanning = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun retake() {
        if (_state.value.isSaving) return
        scanJob?.cancel()
        _state.value = IngredientScanUiState()
    }

    fun changeItem(index: Int, item: ReviewedIngredient) {
        _state.update { state ->
            state.copy(
                items = state.items.mapIndexed { position, current -> if (position == index) item else current },
                errorResource = null,
                errorMessage = null
            )
        }
    }

    fun removeItem(index: Int) {
        _state.update { state ->
            state.copy(items = state.items.filterIndexed { position, _ -> position != index }, errorResource = null)
        }
    }

    fun save() {
        val state = _state.value
        if (state.isScanning || state.isSaving || state.saved || state.items.isEmpty()) return
        val items = state.items.mapNotNull { item ->
            val quantity = item.quantity.trim().replace(',', '.').toDoubleOrNull()
            if (quantity == null || !quantity.isFinite() || quantity <= 0.0 || item.name.isBlank() || item.unit.isBlank()) {
                null
            } else {
                ScannedIngredient(item.name.trim(), quantity, item.unit.trim())
            }
        }
        if (items.size != state.items.size || items.size > 50) {
            _state.update { it.copy(errorResource = R.string.scan_invalid_items) }
            return
        }

        _state.update { it.copy(isSaving = true, errorMessage = null, errorResource = null) }
        viewModelScope.launch {
            val result = pantryRepository.addScannedIngredients(items)
            _state.update {
                it.copy(isSaving = false, saved = result.isSuccess, errorMessage = result.exceptionOrNull()?.message)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as WasteCoApplication
                IngredientScanViewModel(app.container.pantryRepository)
            }
        }
    }
}
