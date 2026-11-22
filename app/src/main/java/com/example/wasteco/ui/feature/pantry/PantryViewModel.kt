package com.example.wasteco.ui.feature.pantry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wasteco.WasteCoApplication
import com.example.wasteco.data.repository.PantryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PantryViewModel(
    private val pantryRepository: PantryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PantryUiState())
    val uiState: StateFlow<PantryUiState> = _uiState.asStateFlow()

    init {
        loadPantryItems()
    }

    fun loadPantryItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = pantryRepository.getPantryIngredients()
            result.onSuccess { items ->
                _uiState.update { it.copy(items = items, isLoading = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun toggleItemSelection(id: String) {
        _uiState.update { state ->
            val updatedSelection = state.selectedItemIds.toMutableSet()
            if (updatedSelection.contains(id)) {
                updatedSelection.remove(id)
            } else {
                updatedSelection.add(id)
            }
            state.copy(selectedItemIds = updatedSelection)
        }
    }

    fun selectAll() {
        _uiState.update { state ->
            val allIds = state.items.map { it.id }.toSet()
            state.copy(selectedItemIds = allIds)
        }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedItemIds = emptySet()) }
    }

    fun showAddDialog(show: Boolean) {
        _uiState.update { it.copy(showAddDialog = show) }
    }

    fun addIngredient(
        name: String,
        quantity: Double,
        unit: String,
        category: String,
        storageLocation: String,
        expiryDate: String,
        estimatedPrice: Int
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAdding = true, errorMessage = null) }
            val result = pantryRepository.addPantryIngredient(
                name = name,
                quantity = quantity,
                unit = unit,
                category = category,
                storageLocation = storageLocation,
                expiryDate = expiryDate,
                estimatedPrice = estimatedPrice
            )
            result.onSuccess { newItem ->
                _uiState.update { state ->
                    state.copy(
                        isAdding = false,
                        showAddDialog = false,
                        items = listOf(newItem) + state.items,
                        successMessage = "${newItem.name} berhasil ditambahkan ke pantry"
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isAdding = false, errorMessage = error.message) }
            }
        }
    }

    fun deleteIngredient(id: String) {
        viewModelScope.launch {
            val result = pantryRepository.deletePantryIngredient(id)
            result.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        items = state.items.filter { it.id != id },
                        selectedItemIds = state.selectedItemIds - id
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as WasteCoApplication)
                PantryViewModel(
                    pantryRepository = app.container.pantryRepository
                )
            }
        }
    }
}
