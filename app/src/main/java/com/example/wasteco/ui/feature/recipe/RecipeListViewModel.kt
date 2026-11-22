package com.example.wasteco.ui.feature.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wasteco.WasteCoApplication
import com.example.wasteco.data.repository.PantryRepository
import com.example.wasteco.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipeListViewModel(
    private val recipeRepository: RecipeRepository,
    private val pantryRepository: PantryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeListUiState())
    val uiState: StateFlow<RecipeListUiState> = _uiState.asStateFlow()

    fun generateRecipes(ingredientIds: List<String>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // If empty, fetch all pantry items first
            val finalIngredientIds = if (ingredientIds.isEmpty()) {
                val pantryRes = pantryRepository.getPantryIngredients()
                pantryRes.getOrNull()?.map { it.id } ?: emptyList()
            } else {
                ingredientIds
            }

            if (finalIngredientIds.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Tidak ada bahan di pantry. Tambahkan bahan makanan terlebih dahulu!"
                    )
                }
                return@launch
            }

            val result = recipeRepository.generateRecipes(
                ingredientIds = finalIngredientIds
            )
            result.onSuccess { recipes ->
                _uiState.update { it.copy(recipes = recipes, isLoading = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as WasteCoApplication)
                RecipeListViewModel(
                    recipeRepository = app.container.recipeRepository,
                    pantryRepository = app.container.pantryRepository
                )
            }
        }
    }
}
