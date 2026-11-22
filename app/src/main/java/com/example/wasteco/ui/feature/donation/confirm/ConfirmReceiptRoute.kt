package com.example.wasteco.ui.feature.donation.confirm

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ConfirmReceiptRoute(
    donationId: String,
    onBackClick: () -> Unit,
    onSuccessFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConfirmReceiptScreen(
        onBackClick = onBackClick,
        onConfirmReceived = onSuccessFinish,
        modifier = modifier
    )
}
