package com.example.wasteco.ui.feature.recipe

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RecipeListRoute(
    ingredientIds: List<String>,
    onBackClick: () -> Unit,
    onRecipeClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecipeListViewModel = viewModel(factory = RecipeListViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel, ingredientIds) {
        viewModel.loadRecipes(ingredientIds)
    }

    RecipeListScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onRecipeClick = onRecipeClick,
        onRetryClick = { viewModel.generateRecipes(ingredientIds) },
        modifier = modifier
    )
}
