package com.example.wasteco.ui.feature.donation.create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CreateDonationRoute(
    onBackClick: () -> Unit,
    onSuccessFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateDonationViewModel = viewModel(factory = CreateDonationViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateDonationScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onIngredientNameChange = viewModel::onIngredientNameChange,
        onQuantityChange = viewModel::onQuantityChange,
        onUnitChange = viewModel::onUnitChange,
        onMeetingPointChange = viewModel::onMeetingPointChange,
        onLocationChange = viewModel::onLocationChange,
        onGpsLoadingChanged = viewModel::setGpsLoading,
        onPickupTimeChange = viewModel::onPickupTimeChange,
        onAddIngredient = viewModel::addIngredientToList,
        onRemoveIngredient = viewModel::removeIngredient,
        onSubmit = viewModel::submitRequest,
        onSuccessFinish = onSuccessFinish,
        modifier = modifier
    )
}
