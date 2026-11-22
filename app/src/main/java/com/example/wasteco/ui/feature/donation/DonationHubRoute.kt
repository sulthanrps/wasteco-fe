package com.example.wasteco.ui.feature.donation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DonationHubRoute(
    onMapViewClick: () -> Unit,
    onCreateRequestClick: () -> Unit,
    onDonationClick: (String) -> Unit,
    onConfirmReceiptClick: (String) -> Unit,
    onHomeTabClick: () -> Unit,
    onRecipeTabClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DonationHubViewModel = viewModel(factory = DonationHubViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DonationHubScreen(
        uiState = uiState,
        onTabSelected = viewModel::selectTab,
        onActivityFilterSelected = viewModel::selectActivityFilter,
        onSearchChange = viewModel::onSearchQueryChange,
        onMapViewClick = onMapViewClick,
        onCreateRequestClick = onCreateRequestClick,
        onDonationClick = onDonationClick,
        onConfirmReceiptClick = onConfirmReceiptClick,
        onConfirmHandoverAsDonor = viewModel::confirmHandoverAsDonor,
        onCancelMyDonationClick = viewModel::cancelMyDonation,
        onHomeTabClick = onHomeTabClick,
        onRecipeTabClick = onRecipeTabClick,
        onProfileClick = onProfileClick,
        modifier = modifier
    )
}
