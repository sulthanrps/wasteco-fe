package com.example.wasteco.ui.feature.donation.confirm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ConfirmReceiptRoute(
    donationId: String,
    onBackClick: () -> Unit,
    onSuccessFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConfirmReceiptViewModel = viewModel(factory = ConfirmReceiptViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(donationId) { viewModel.load(donationId) }
    LaunchedEffect(state.isConfirmed) {
        if (state.isConfirmed) onSuccessFinish()
    }
    ConfirmReceiptScreen(
        state = state,
        onBackClick = onBackClick,
        onConfirmReceived = viewModel::confirm,
        onRetry = { viewModel.load(donationId) },
        modifier = modifier
    )
}
