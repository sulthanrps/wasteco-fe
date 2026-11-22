package com.example.wasteco.ui.feature.recipe.camera

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import coil.compose.AsyncImage
import com.example.wasteco.R
import com.example.wasteco.ui.components.WasteCoPrimaryButton
import com.example.wasteco.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewPhotoScreen(
    state: IngredientScanUiState,
    onRetake: () -> Unit,
    onRescan: () -> Unit,
    onChangeItem: (Int, ReviewedIngredient) -> Unit,
    onRemoveItem: (Int) -> Unit,
    onSave: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.scan_review_title)) }) }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(Spacing.large),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            AsyncImage(
                model = state.photo,
                contentDescription = stringResource(R.string.scan_photo_description),
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth().aspectRatio(1.5f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                TextButton(onClick = onRetake, enabled = !state.isSaving) {
                    Text(stringResource(R.string.scan_retake))
                }
                if (!state.isScanning && state.items.isEmpty() && state.errorMessage != null) {
                    TextButton(onClick = onRescan) { Text(stringResource(R.string.retry)) }
                }
            }
            if (state.isScanning) {
                CircularProgressIndicator()
                Text(stringResource(R.string.scan_loading))
            }
            val error = state.errorResource?.let { stringResource(it) } ?: state.errorMessage
            if (error != null) Text(error, color = MaterialTheme.colorScheme.error)
            if (!state.isScanning && state.items.isEmpty() && error == null) {
                Text(stringResource(R.string.scan_empty))
            }
            state.items.forEachIndexed { index, item ->
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    OutlinedTextField(
                        value = item.name,
                        onValueChange = { onChangeItem(index, item.copy(name = it)) },
                        label = { Text(stringResource(R.string.scan_name)) },
                        enabled = !state.isSaving,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                        OutlinedTextField(
                            value = item.quantity,
                            onValueChange = { onChangeItem(index, item.copy(quantity = it)) },
                            label = { Text(stringResource(R.string.scan_quantity)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            enabled = !state.isSaving,
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = item.unit,
                            onValueChange = { onChangeItem(index, item.copy(unit = it)) },
                            label = { Text(stringResource(R.string.scan_unit)) },
                            enabled = !state.isSaving,
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    TextButton(onClick = { onRemoveItem(index) }, enabled = !state.isSaving) {
                        Text(stringResource(R.string.scan_remove))
                    }
                }
            }
            if (state.items.isNotEmpty()) {
                WasteCoPrimaryButton(
                    text = stringResource(R.string.scan_save, state.items.size),
                    onClick = onSave,
                    isLoading = state.isSaving
                )
            }
        }
    }
}
