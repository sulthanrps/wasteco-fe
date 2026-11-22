package com.example.wasteco.ui.feature.recipe.camera

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.wasteco.R
import com.example.wasteco.ui.components.WasteCoPrimaryButton
import com.example.wasteco.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraCaptureScreen(
    state: IngredientScanUiState,
    onCloseClick: () -> Unit,
    onCaptureClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.scan_title)) },
                navigationIcon = {
                    TextButton(onClick = onCloseClick, enabled = !state.isReadingPhoto) {
                        Text(stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(Spacing.large),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            Text(stringResource(R.string.scan_instruction))
            WasteCoPrimaryButton(
                text = stringResource(R.string.scan_camera),
                onClick = onCaptureClick,
                enabled = !state.isReadingPhoto
            )
            OutlinedButton(
                onClick = onGalleryClick,
                enabled = !state.isReadingPhoto,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.scan_gallery))
            }
            if (state.isReadingPhoto) CircularProgressIndicator()
            state.errorResource?.let { Text(stringResource(it), color = MaterialTheme.colorScheme.error) }
        }
    }
}
