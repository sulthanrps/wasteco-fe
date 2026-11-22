package com.example.wasteco.ui.feature.recipe.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RecipeDetailRoute(
    recipeId: String,
    onBackClick: () -> Unit,
    onCookClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecipeDetailViewModel = viewModel(factory = RecipeDetailViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    RecipeDetailScreen(
        recipe = uiState.recipe,
        onBackClick = onBackClick,
        onCookClick = {
            onCookClick(recipeId)
        },
        modifier = modifier
    )
}
