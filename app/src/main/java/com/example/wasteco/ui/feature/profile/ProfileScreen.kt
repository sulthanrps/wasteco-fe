package com.example.wasteco.ui.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.wasteco.R
import com.example.wasteco.domain.model.DONATION_RADIUS_KM
import com.example.wasteco.ui.components.WasteCoPrimaryButton
import com.example.wasteco.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onAreaChange: (String) -> Unit,
    onLocation: () -> Unit,
    onSave: () -> Unit,
    onRetry: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_title)) },
                navigationIcon = { TextButton(onClick = onBack) { Text(stringResource(R.string.back)) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(Spacing.large),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            if (state.isLoading) CircularProgressIndicator()
            val error = state.errorResource?.let { stringResource(it) } ?: state.errorMessage
            if (error != null) Text(error, color = MaterialTheme.colorScheme.error)
            if (!state.isLoading && state.user == null) {
                TextButton(onClick = onRetry) { Text(stringResource(R.string.retry)) }
            }
            state.user?.let { user ->
                Text(user.email, style = MaterialTheme.typography.bodyLarge)
                OutlinedTextField(user.name, onNameChange, Modifier.fillMaxWidth(), enabled = !state.isSaving,
                    label = { Text(stringResource(R.string.profile_name)) }, singleLine = true)
                OutlinedTextField(user.phoneNumber, onPhoneChange, Modifier.fillMaxWidth(), enabled = !state.isSaving,
                    label = { Text(stringResource(R.string.profile_phone)) }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                OutlinedTextField(user.areaName, onAreaChange, Modifier.fillMaxWidth(), enabled = !state.isSaving,
                    label = { Text(stringResource(R.string.profile_area)) }, singleLine = true)
                OutlinedButton(onClick = onLocation, enabled = !state.isSaving && !state.isLocating) {
                    Text(stringResource(if (state.isLocating) R.string.profile_locating else R.string.profile_use_gps))
                }
                Text(stringResource(R.string.profile_radius, DONATION_RADIUS_KM))
                Text(stringResource(R.string.profile_portions, user.totalPortionsSaved))
                WasteCoPrimaryButton(
                    text = stringResource(R.string.profile_save),
                    onClick = onSave,
                    enabled = !state.isLocating,
                    isLoading = state.isSaving
                )
                if (state.saved) Text(stringResource(R.string.profile_saved))
            }
            TextButton(onClick = onLogout, enabled = !state.isSaving) {
                Text(stringResource(R.string.profile_logout))
            }
        }
    }
}
