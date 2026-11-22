package com.example.wasteco.ui.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.wasteco.BuildConfig
import com.example.wasteco.R
import com.example.wasteco.domain.model.DONATION_RADIUS_KM
import com.example.wasteco.domain.model.User
import com.example.wasteco.ui.components.WasteCoBrandMark
import com.example.wasteco.ui.components.WasteCoPrimaryButton
import com.example.wasteco.ui.components.WasteCoTextField
import com.example.wasteco.ui.theme.Elevation
import com.example.wasteco.ui.theme.EntryColors
import com.example.wasteco.ui.theme.EntryDimens
import com.example.wasteco.ui.theme.EntryTypography
import com.example.wasteco.ui.theme.ErrorContainer
import com.example.wasteco.ui.theme.ErrorRed
import com.example.wasteco.ui.theme.Radius
import com.example.wasteco.ui.theme.Spacing
import com.example.wasteco.ui.theme.WasteCoTheme

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
    var editorOpen by rememberSaveable { mutableStateOf(false) }
    var showAbout by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state.saved) { if (state.saved) editorOpen = false }

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = EntryColors.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.compact)
                    ) {
                        WasteCoBrandMark(Modifier.size(EntryDimens.profileLogo), decorative = true)
                        Text(stringResource(R.string.profile_title), color = EntryColors.heading)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back), tint = EntryColors.heading)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EntryColors.surface)
            )
        }
    ) { insets ->
        Box(Modifier.fillMaxSize().padding(insets), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier.widthIn(max = EntryDimens.maxWidth).fillMaxWidth()
                    .verticalScroll(rememberScrollState()).padding(EntryDimens.gutter),
                verticalArrangement = Arrangement.spacedBy(Spacing.large)
            ) {
                if (state.isLoading) CircularProgressIndicator(color = EntryColors.action)
                val error = state.errorResource?.let { stringResource(it) } ?: state.errorMessage
                val user = state.user
                if (user == null) {
                    if (error != null) Text(error, color = MaterialTheme.colorScheme.error)
                    if (!state.isLoading) {
                        TextButton(onClick = onRetry) { Text(stringResource(R.string.retry)) }
                    }
                } else {
                    ProfileIdentity(user)
                    ProfileStatistics(user.totalPortionsSaved)

                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                        ProfileSectionTitle(stringResource(R.string.profile_account_settings))
                        ProfileAreaCard(
                            area = user.areaName,
                            onEdit = { editorOpen = true },
                            editEnabled = !editorOpen
                        )
                        if (editorOpen) {
                            ProfileEditCard(
                                user = user,
                                isLocating = state.isLocating,
                                isSaving = state.isSaving,
                                error = error,
                                onNameChange = onNameChange,
                                onPhoneChange = onPhoneChange,
                                onAreaChange = onAreaChange,
                                onLocation = onLocation,
                                onSave = onSave
                            )
                        }
                        if (state.saved) {
                            Text(stringResource(R.string.profile_saved),
                                color = EntryColors.action, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                        ProfileSectionTitle(stringResource(R.string.profile_about_section))
                        Surface(
                            onClick = { showAbout = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(Radius.large),
                            color = EntryColors.surface,
                            shadowElevation = Elevation.small
                        ) {
                            Row(
                                modifier = Modifier.padding(Spacing.medium),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.compact)
                            ) {
                                Box(
                                    modifier = Modifier.size(EntryDimens.featureIconBadge)
                                        .background(EntryColors.oliveTint, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.Info, contentDescription = null, tint = EntryColors.body)
                                }
                                Column(Modifier.weight(1f)) {
                                    Text(stringResource(R.string.profile_about), style = MaterialTheme.typography.titleMedium,
                                        color = EntryColors.heading)
                                    Text(stringResource(R.string.profile_version, BuildConfig.VERSION_NAME),
                                        style = MaterialTheme.typography.bodySmall, color = EntryColors.muted)
                                }
                                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = EntryColors.muted)
                            }
                        }
                    }
                }

                Button(
                    onClick = onLogout,
                    enabled = !state.isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Radius.medium),
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorContainer, contentColor = ErrorRed)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null,
                        modifier = Modifier.size(EntryDimens.buttonIcon))
                    Text(stringResource(R.string.profile_logout), Modifier.padding(start = Spacing.small))
                }
            }
        }
    }

    if (showAbout) {
        AlertDialog(
            onDismissRequest = { showAbout = false },
            title = { Text(stringResource(R.string.profile_about)) },
            text = { Text(stringResource(R.string.profile_about_description)) },
            confirmButton = {
                TextButton(onClick = { showAbout = false }) { Text(stringResource(R.string.auth_close)) }
            }
        )
    }
}

@Composable
private fun ProfileIdentity(user: User) {
    val initials = user.name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        .take(2).map { it.first().uppercaseChar() }.joinToString("")
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.large),
        color = EntryColors.surface,
        shadowElevation = Elevation.small
    ) {
        Box {
            Box(Modifier.align(Alignment.TopEnd).size(EntryDimens.profileAccent)
                .background(EntryColors.greenTint.copy(alpha = 0.55f), CircleShape))
            Box(Modifier.align(Alignment.BottomStart).size(EntryDimens.featureIconBadge)
                .background(EntryColors.orangeTint, CircleShape))
            Column(
                modifier = Modifier.fillMaxWidth().padding(Spacing.large),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                Box(
                    Modifier.size(EntryDimens.profileAvatar).background(EntryColors.greenTint, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(initials, style = EntryTypography.metric, color = EntryColors.action)
                }
                Text(user.name, style = EntryTypography.heading, color = EntryColors.heading,
                    textAlign = TextAlign.Center)
                Text(user.email, style = EntryTypography.body, color = EntryColors.muted,
                    textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun ProfileStatistics(portions: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.large),
        color = EntryColors.greenTint
    ) {
        Column(
            Modifier.padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.compact)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.profile_statistics), style = MaterialTheme.typography.titleMedium,
                    color = EntryColors.action)
                Surface(color = EntryColors.surface, shape = CircleShape) {
                    Text(stringResource(R.string.profile_statistics_total),
                        modifier = Modifier.padding(horizontal = Spacing.small, vertical = Spacing.xxs),
                        style = MaterialTheme.typography.labelMedium, color = EntryColors.action)
                }
            }
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Radius.medium),
                color = EntryColors.surface
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.compact),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.compact)
                ) {
                    Box(
                        Modifier.size(EntryDimens.featureIconBadge)
                            .background(EntryColors.greenTint, RoundedCornerShape(Radius.small)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Restaurant, contentDescription = null, tint = EntryColors.action)
                    }
                    Column {
                        Text(portions.toString(), style = EntryTypography.metric, color = EntryColors.heading)
                        Text(stringResource(R.string.profile_portions_label),
                            style = MaterialTheme.typography.bodySmall, color = EntryColors.body)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileSectionTitle(title: String) {
    Text(title, modifier = Modifier.padding(start = Spacing.xs), style = MaterialTheme.typography.labelLarge,
        color = EntryColors.body, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun ProfileAreaCard(area: String, onEdit: () -> Unit, editEnabled: Boolean) {
    val areaLabel = area.ifBlank { stringResource(R.string.profile_area_unspecified) }
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(Radius.large),
        color = EntryColors.surface, shadowElevation = Elevation.small) {
        Surface(
            modifier = Modifier.padding(Spacing.small),
            shape = RoundedCornerShape(Radius.medium),
            color = EntryColors.oliveTint.copy(alpha = 0.6f)
        ) {
            Row(
                modifier = Modifier.padding(Spacing.compact),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(EntryDimens.featureIconBadge).background(EntryColors.surface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = EntryColors.action)
                }
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.profile_location_label),
                        style = MaterialTheme.typography.bodySmall, color = EntryColors.muted)
                    Text(areaLabel, style = MaterialTheme.typography.titleMedium, color = EntryColors.heading)
                    Text(stringResource(R.string.profile_radius, DONATION_RADIUS_KM),
                        style = MaterialTheme.typography.bodySmall, color = EntryColors.body)
                }
                if (editEnabled) {
                    TextButton(onClick = onEdit) {
                        Text(stringResource(R.string.profile_edit_area), color = EntryColors.action)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileEditCard(
    user: User,
    isLocating: Boolean,
    isSaving: Boolean,
    error: String?,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onAreaChange: (String) -> Unit,
    onLocation: () -> Unit,
    onSave: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.large),
        color = EntryColors.greenTint
    ) {
        Column(Modifier.padding(Spacing.medium), verticalArrangement = Arrangement.spacedBy(Spacing.compact)) {
            Text(stringResource(R.string.profile_edit_heading), style = MaterialTheme.typography.titleMedium,
                color = EntryColors.heading)
            WasteCoTextField(user.name, onNameChange, stringResource(R.string.profile_name),
                filledStyle = true, enabled = !isSaving)
            WasteCoTextField(user.phoneNumber, onPhoneChange, stringResource(R.string.profile_phone),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), filledStyle = true, enabled = !isSaving)
            WasteCoTextField(user.areaName, onAreaChange, stringResource(R.string.profile_area),
                filledStyle = true, enabled = !isSaving)
            OutlinedButton(onClick = onLocation, enabled = !isSaving && !isLocating) {
                Text(stringResource(if (isLocating) R.string.profile_locating else R.string.profile_use_gps),
                    color = EntryColors.action)
            }
            if (error != null) Text(error, color = MaterialTheme.colorScheme.error)
            WasteCoPrimaryButton(
                text = stringResource(R.string.profile_save), onClick = onSave,
                enabled = !isLocating, isLoading = isSaving,
                containerColor = EntryColors.action, cornerRadius = Radius.medium
            )
        }
    }
}


@Preview(showBackground = true, widthDp = 390, heightDp = 850)
@Composable
private fun ProfileScreenPreview() {
    WasteCoTheme {
        ProfileScreen(
            state = ProfileUiState(
                user = User(
                    id = "preview", name = "Affan Maulana", email = "affan@example.com",
                    phoneNumber = "", areaName = "Dinoyo, Malang", latitude = 0.0, longitude = 0.0,
                    sharingRadiusKm = DONATION_RADIUS_KM, totalPortionsSaved = 14
                ),
                isLoading = false
            ),
            onBack = {}, onNameChange = {}, onPhoneChange = {}, onAreaChange = {},
            onLocation = {}, onSave = {}, onRetry = {}, onLogout = {}
        )
    }
}
