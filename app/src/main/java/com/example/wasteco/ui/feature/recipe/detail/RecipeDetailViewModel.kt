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

class RecipeDetailViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    fun loadRecipe(recipeId: String) {
        val recipe = recipeRepository.getCachedRecipe(recipeId)
        _uiState.update {
            it.copy(
                recipe = recipe,
                cookedPortions = recipe?.servings ?: 2
            )
        }
    }

    fun updatePortions(portions: Int) {
        if (portions in 1..10) {
            _uiState.update { it.copy(cookedPortions = portions) }
        }
    }

    fun showConfirmDialog(show: Boolean) {
        _uiState.update { it.copy(showConfirmDialog = show) }
    }

    fun confirmCooking() {
        val recipe = _uiState.value.recipe ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isConfirming = true, errorMessage = null) }
            val result = recipeRepository.confirmCooking(
                recipeName = recipe.name,
                portions = _uiState.value.cookedPortions,
                estimatedSavings = _uiState.value.totalEstimatedSavings,
                usedIngredients = recipe.usedIngredients
            )
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isConfirming = false,
                        showConfirmDialog = false,
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
                RecipeDetailViewModel(
                    recipeRepository = app.container.recipeRepository
                )
            }
        }
    }
}
