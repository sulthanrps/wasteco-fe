package com.example.wasteco.ui.feature.recipe.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wasteco.WasteCoApplication
import com.example.wasteco.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CookConfirmViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CookConfirmUiState())
    val uiState: StateFlow<CookConfirmUiState> = _uiState.asStateFlow()

    fun loadRecipe(recipeId: String) {
        val recipe = recipeRepository.getCachedRecipe(recipeId)
        val initialQuantities = recipe?.usedIngredients?.associate { it.ingredientId to it.quantity } ?: emptyMap()
        _uiState.update {
            it.copy(
                recipe = recipe,
                cookedPortions = recipe?.servings ?: 2,
                ingredientQuantities = initialQuantities
            )
        }
    }

    fun updatePortions(delta: Int) {
        val current = _uiState.value.cookedPortions
        val newPortions = (current + delta).coerceIn(1, 20)
        _uiState.update { it.copy(cookedPortions = newPortions) }
    }

    fun updateIngredientQuantity(ingredientId: String, delta: Double) {
        val current = _uiState.value.ingredientQuantities[ingredientId] ?: 1.0
        val newQty = (current + delta).coerceAtLeast(0.5)
        _uiState.update {
            it.copy(
                ingredientQuantities = it.ingredientQuantities + (ingredientId to newQty)
            )
        }
    }

    fun confirmCooking() {
        val recipe = _uiState.value.recipe ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isConfirming = true, errorMessage = null) }
            val updatedIngredients = recipe.usedIngredients.map { ing ->
                val qty = _uiState.value.ingredientQuantities[ing.ingredientId] ?: ing.quantity
                ing.copy(quantity = qty)
            }
            val result = recipeRepository.confirmCooking(
                recipeName = recipe.name,
                portions = _uiState.value.cookedPortions,
                estimatedSavings = _uiState.value.totalEstimatedSavings,
                usedIngredients = updatedIngredients
            )
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isConfirming = false,
                        isCookRecorded = true
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isConfirming = false,
                        errorMessage = error.message ?: "Gagal mencatat masakan"
                    )
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as WasteCoApplication)
                CookConfirmViewModel(
                    recipeRepository = app.container.recipeRepository
                )
            }
        }
    }
}
