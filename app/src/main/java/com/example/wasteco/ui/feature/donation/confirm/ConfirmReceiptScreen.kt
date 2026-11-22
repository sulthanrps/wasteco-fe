package com.example.wasteco.ui.feature.donation.confirm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.wasteco.R
import com.example.wasteco.ui.components.WasteCoPrimaryButton
import com.example.wasteco.ui.feature.donation.donationStatusLabel
import com.example.wasteco.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmReceiptScreen(
    state: ConfirmReceiptUiState,
    onBackClick: () -> Unit,
    onConfirmReceived: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text(stringResource(R.string.donation_confirm_title)) }) }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(Spacing.large),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            if (state.isLoading) CircularProgressIndicator()
            state.errorMessage?.let { message ->
                Text(message, color = MaterialTheme.colorScheme.error)
                if (state.donation == null) {
                    TextButton(onClick = onRetry) { Text(stringResource(R.string.retry)) }
                }
            }
            state.donation?.let { donation ->
                Text(donation.title, style = MaterialTheme.typography.headlineSmall)
                donation.donor?.let { Text(stringResource(R.string.donation_donor_name, it.name)) }
                donation.items.filter { it.selected }.forEach { item ->
                    Text(stringResource(R.string.donation_item_summary, item.name, item.quantity))
                }
                Text(stringResource(donationStatusLabel(donation.status)))
                if (donation.status == "handed_over") {
                    WasteCoPrimaryButton(
                        text = stringResource(R.string.donation_confirm_action),
                        onClick = onConfirmReceived,
                        enabled = !state.isSubmitting,
                        isLoading = state.isSubmitting,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            TextButton(onClick = onBackClick) { Text(stringResource(R.string.back)) }
        }
    }
}
