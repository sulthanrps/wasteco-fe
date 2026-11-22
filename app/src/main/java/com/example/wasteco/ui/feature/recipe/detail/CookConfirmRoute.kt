package com.example.wasteco.ui.feature.recipe.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CookConfirmRoute(
    recipeId: String,
    onBackClick: () -> Unit,
    onFinishAndGoHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CookConfirmViewModel = viewModel(factory = CookConfirmViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    CookConfirmScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onPortionDelta = viewModel::updatePortions,
        onIngredientDelta = viewModel::updateIngredientQuantity,
        onConfirmCooking = viewModel::confirmCooking,
        onFinishAndGoHome = onFinishAndGoHome,
        modifier = modifier
    )
}
