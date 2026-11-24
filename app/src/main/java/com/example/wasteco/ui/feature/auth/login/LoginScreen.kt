package com.example.wasteco.ui.feature.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.wasteco.R
import com.example.wasteco.ui.components.WasteCoEntryBackButton
import com.example.wasteco.ui.components.WasteCoLogo
import com.example.wasteco.ui.components.WasteCoPasswordField
import com.example.wasteco.ui.components.WasteCoPrimaryButton
import com.example.wasteco.ui.components.WasteCoTextField
import com.example.wasteco.ui.theme.EntryColors
import com.example.wasteco.ui.theme.EntryDimens
import com.example.wasteco.ui.theme.EntryTypography
import com.example.wasteco.ui.theme.Radius
import com.example.wasteco.ui.theme.Spacing
import com.example.wasteco.ui.theme.WasteCoTheme

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetInfo by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier.fillMaxSize().background(EntryColors.background).statusBarsPadding().imePadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = EntryDimens.maxWidth)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = EntryDimens.gutter, vertical = Spacing.compact)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WasteCoEntryBackButton(onClick = onBackClick)
                WasteCoLogo(
                    Modifier.background(EntryColors.greenTint, CircleShape)
                        .padding(horizontal = Spacing.compact, vertical = Spacing.xs)
                )
            }
            Spacer(Modifier.height(Spacing.large))
            Text(stringResource(R.string.auth_login_heading), style = EntryTypography.heading, color = EntryColors.heading)
            Spacer(Modifier.height(Spacing.xs))
            Text(stringResource(R.string.auth_login_description), style = EntryTypography.body, color = EntryColors.body)
            Spacer(Modifier.height(Spacing.large))
            WasteCoTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = stringResource(R.string.auth_email),
                placeholder = stringResource(R.string.auth_email_placeholder),
                leadingIcon = Icons.Outlined.Email,
                isError = uiState.emailError != null,
                errorMessage = uiState.emailError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                filledStyle = true
            )
            Spacer(Modifier.height(Spacing.medium))
            WasteCoPasswordField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = stringResource(R.string.auth_password),
                placeholder = stringResource(R.string.auth_login_password_placeholder),
                leadingIcon = Icons.Outlined.Lock,
                isError = uiState.passwordError != null,
                errorMessage = uiState.passwordError,
                filledStyle = true,
                labelAction = {
                    Text(
                        stringResource(R.string.auth_forgot_password),
                        modifier = Modifier.clickable(role = Role.Button) {
                            onForgotPasswordClick()
                            showResetInfo = true
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = EntryColors.action
                    )
                }
            )
            uiState.errorMessage?.let { message ->
                Spacer(Modifier.height(Spacing.small))
                Text(message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(Spacing.large))
            WasteCoPrimaryButton(
                text = stringResource(R.string.auth_sign_in),
                onClick = onLoginClick,
                isLoading = uiState.isLoading,
                trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                containerColor = EntryColors.actionSolid,
                cornerRadius = Radius.medium,
                iconSize = EntryDimens.buttonIcon
            )
            Spacer(Modifier.height(Spacing.large))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.auth_new_account), style = EntryTypography.body, color = EntryColors.body)
                TextButton(onClick = onRegisterClick) {
                    Text(stringResource(R.string.auth_register_link), color = EntryColors.action)
                }
            }
        }
    }
    if (showResetInfo) {
        AlertDialog(
            onDismissRequest = { showResetInfo = false },
            title = { Text(stringResource(R.string.auth_forgot_password)) },
            text = { Text(stringResource(R.string.auth_reset_unavailable)) },
            confirmButton = {
                TextButton(onClick = { showResetInfo = false }) { Text(stringResource(R.string.auth_close)) }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun LoginScreenPreview() {
    WasteCoTheme {
        LoginScreen(LoginUiState(email = "nama@email.com"), {}, {}, {}, {}, {}, {})
    }
}
