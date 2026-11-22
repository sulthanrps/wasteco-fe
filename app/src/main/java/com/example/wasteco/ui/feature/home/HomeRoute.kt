package com.example.wasteco.ui.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeRoute(
    onPantryClick: () -> Unit,
    onDonationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onDonationClick: (String) -> Unit,
    onDonationMapClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.loadHomeData() }

    HomeScreen(
        uiState = uiState,
        onPantryClick = onPantryClick,
        onDonationsClick = onDonationsClick,
        onProfileClick = onProfileClick,
        onDonationClick = onDonationClick,
        onDonationMapClick = onDonationMapClick,
        onRefresh = viewModel::loadHomeData,
        modifier = modifier
    )
}
