package com.example.wasteco.ui.feature.auth.register

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.wasteco.R
import com.example.wasteco.ui.components.WasteCoEntryBackButton
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
fun RegisterScreen(
    uiState: RegisterUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onAreaNameChange: (String) -> Unit,
    onLocate: () -> Unit,
    onAgreementChange: (Boolean) -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            WasteCoEntryBackButton(onClick = onBackClick)
            Spacer(Modifier.height(Spacing.large))
            Text(stringResource(R.string.auth_register_heading), style = EntryTypography.heading, color = EntryColors.heading)
            Spacer(Modifier.height(Spacing.xs))
            Text(stringResource(R.string.auth_register_description), style = EntryTypography.body, color = EntryColors.body)
            Spacer(Modifier.height(Spacing.medium))

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                WasteCoTextField(
                    value = uiState.name,
                    onValueChange = onNameChange,
                    label = stringResource(R.string.auth_full_name),
                    placeholder = stringResource(R.string.auth_full_name_placeholder),
                    leadingIcon = Icons.Outlined.PersonOutline,
                    isError = uiState.nameError != null,
                    errorMessage = uiState.nameError,
                    filledStyle = true
                )
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
                WasteCoTextField(
                    value = uiState.phoneNumber,
                    onValueChange = onPhoneNumberChange,
                    label = stringResource(R.string.profile_phone),
                    placeholder = stringResource(R.string.auth_phone_placeholder),
                    leadingIcon = Icons.Outlined.Phone,
                    isError = uiState.phoneNumberInvalid,
                    errorMessage = if (uiState.phoneNumberInvalid) stringResource(R.string.register_invalid_phone) else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    filledStyle = true
                )
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    WasteCoPasswordField(
                        value = uiState.password,
                        onValueChange = onPasswordChange,
                        label = stringResource(R.string.auth_password),
                        placeholder = stringResource(R.string.auth_password_minimum),
                        leadingIcon = Icons.Outlined.Lock,
                        isError = uiState.passwordError != null,
                        errorMessage = uiState.passwordError,
                        filledStyle = true
                    )
                    val validPassword = uiState.isPasswordLengthValid && uiState.isPasswordCombinationValid
                    val hintColor = if (validPassword) EntryColors.action else EntryColors.warningInk
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            if (validPassword) Icons.Outlined.CheckCircle else Icons.Outlined.Info,
                            contentDescription = null, tint = hintColor,
                            modifier = Modifier.size(EntryDimens.compactLogo)
                        )
                        Text(stringResource(R.string.auth_password_hint),
                            color = hintColor, style = MaterialTheme.typography.bodySmall)
                    }
                }
                WasteCoPasswordField(
                    value = uiState.confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = stringResource(R.string.auth_confirm_password),
                    placeholder = stringResource(R.string.auth_confirm_password_placeholder),
                    leadingIcon = Icons.Outlined.Lock,
                    isError = uiState.confirmPasswordError != null,
                    errorMessage = uiState.confirmPasswordError,
                    filledStyle = true
                )
                WasteCoTextField(
                    value = uiState.areaName,
                    onValueChange = onAreaNameChange,
                    label = stringResource(R.string.auth_area),
                    placeholder = stringResource(R.string.auth_area_placeholder),
                    leadingIcon = Icons.Outlined.Explore,
                    isError = uiState.areaNameError != null,
                    errorMessage = uiState.areaNameError,
                    filledStyle = true,
                    labelAction = {
                        Text(
                            stringResource(if (uiState.isLocating) R.string.profile_locating else R.string.auth_gps),
                            modifier = Modifier.clickable(
                                enabled = !uiState.isLocating && !uiState.isLoading,
                                role = Role.Button,
                                onClick = onLocate
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = EntryColors.muted
                        )
                    }
                )
                if (uiState.locationError) {
                    Text(stringResource(R.string.register_location_unavailable),
                        color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Surface(
                    modifier = Modifier.fillMaxWidth().toggleable(
                        value = uiState.isAgreementChecked, role = Role.Checkbox,
                        onValueChange = onAgreementChange
                    ),
                    shape = RoundedCornerShape(Radius.medium),
                    color = EntryColors.surface
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.compact),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.compact),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = uiState.isAgreementChecked,
                            onCheckedChange = null,
                            colors = CheckboxDefaults.colors(checkedColor = EntryColors.actionSolid)
                        )
                        val agreementStart = stringResource(R.string.auth_agreement_start)
                        val agreementEmphasis = stringResource(R.string.auth_agreement_emphasis)
                        val agreementEnd = stringResource(R.string.auth_agreement_end)
                        Text(
                            text = buildAnnotatedString {
                                append(agreementStart)
                                append(' ')
                                withStyle(SpanStyle(color = EntryColors.action, fontWeight = FontWeight.SemiBold)) {
                                    append(agreementEmphasis)
                                }
                                append(' ')
                                append(agreementEnd)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = EntryColors.heading
                        )
                    }
                }
                uiState.errorMessage?.let { message ->
                    Text(message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(Modifier.height(Spacing.large))
            WasteCoPrimaryButton(
                text = stringResource(R.string.auth_create_account),
                onClick = onRegisterClick,
                enabled = uiState.isFormValid && !uiState.isLocating,
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
                Text(stringResource(R.string.auth_existing_account), style = EntryTypography.body, color = EntryColors.body)
                TextButton(onClick = onLoginClick) {
                    Text(stringResource(R.string.auth_sign_in), color = EntryColors.action)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 920)
@Composable
private fun RegisterScreenPreview() {
    WasteCoTheme {
        RegisterScreen(
            uiState = RegisterUiState(name = "Affan Maulana", areaName = "Dinoyo, Lowokwaru, Malang"),
            onNameChange = {}, onEmailChange = {}, onPhoneNumberChange = {},
            onPasswordChange = {}, onConfirmPasswordChange = {}, onAreaNameChange = {},
            onLocate = {}, onAgreementChange = {}, onRegisterClick = {}, onLoginClick = {}, onBackClick = {}
        )
    }
}
