package com.example.wasteco.ui.feature.donation.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DonationDetailRoute(
    donationId: String,
    onBackClick: () -> Unit,
    latitude: Double? = null,
    longitude: Double? = null,
    onReturnToDonationHub: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DonationDetailViewModel = viewModel(factory = DonationDetailViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(donationId, latitude, longitude) {
        viewModel.loadDonation(donationId, latitude, longitude)
    }

    DonationDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onToggleItem = viewModel::toggleItemSelection,
        onSubmitHandover = viewModel::submitHandover,
        onDismissHandoverSheet = viewModel::dismissHandoverSheet,
        onReturnToDonationHub = onReturnToDonationHub,
        modifier = modifier
    )
}
