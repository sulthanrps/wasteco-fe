package com.example.wasteco.ui.feature.donation.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DonationMapRoute(
    onListViewClick: () -> Unit,
    onDonationDetailClick: (String, Double?, Double?) -> Unit,
    onHomeTabClick: () -> Unit,
    onRecipeTabClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DonationMapViewModel = viewModel(factory = DonationMapViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DonationMapScreen(
        uiState = uiState,
        onRetry = { viewModel.loadNearbyDonations() },
        onDonationSelected = viewModel::selectDonation,
        onLocationChanged = viewModel::updateUserLocation,
        onOpenLocationSheet = { viewModel.setLocationSheetVisible(true) },
        onDismissLocationSheet = { viewModel.setLocationSheetVisible(false) },
        onGpsLoadingChanged = viewModel::setGpsLoading,
        onListViewClick = onListViewClick,
        onDonationDetailClick = { id ->
            onDonationDetailClick(
                id,
                uiState.userLatitude.takeIf { uiState.hasLocation },
                uiState.userLongitude.takeIf { uiState.hasLocation }
            )
        },
        onHomeTabClick = onHomeTabClick,
        onRecipeTabClick = onRecipeTabClick,
        onProfileClick = onProfileClick,
        modifier = modifier
    )
}
