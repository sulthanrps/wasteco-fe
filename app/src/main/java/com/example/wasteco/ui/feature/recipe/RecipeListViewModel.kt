package com.example.wasteco.ui.feature.recipe

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

class RecipeListViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeListUiState())
    val uiState: StateFlow<RecipeListUiState> = _uiState.asStateFlow()

    private var requestedIngredientIds: List<String>? = null

    fun loadRecipes(ingredientIds: List<String>) {
        if (requestedIngredientIds != ingredientIds) generateRecipes(ingredientIds)
    }

    fun generateRecipes(ingredientIds: List<String>) {
        if (_uiState.value.isLoading) return
        requestedIngredientIds = ingredientIds.toList()
        _uiState.update { it.copy(isLoading = true, errorMessage = null, recipes = emptyList()) }
        viewModelScope.launch {

            val result = recipeRepository.generateRecipes(ingredientIds)
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
                    recipeRepository = app.container.recipeRepository
                )
            }
        }
    }
}
