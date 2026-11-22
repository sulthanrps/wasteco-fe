package com.example.wasteco.ui.feature.pantry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PantryRoute(
    onHomeTabClick: () -> Unit,
    onDonationsTabClick: () -> Unit,
    onProfileClick: () -> Unit,
    onCameraClick: () -> Unit,
    onGenerateRecipesClick: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PantryViewModel = viewModel(factory = PantryViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PantryScreen(
        uiState = uiState,
        onHomeTabClick = onHomeTabClick,
        onDonationsTabClick = onDonationsTabClick,
        onProfileClick = onProfileClick,
        onCameraClick = onCameraClick,
        onAddConfirm = viewModel::addIngredient,
        onDeleteIngredient = viewModel::deleteIngredient,
        onGenerateRecipesClick = onGenerateRecipesClick,
        modifier = modifier
    )
}
