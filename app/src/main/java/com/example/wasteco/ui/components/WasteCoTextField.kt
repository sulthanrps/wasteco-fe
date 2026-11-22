package com.example.wasteco.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.wasteco.R
import com.example.wasteco.ui.theme.EntryColors
import com.example.wasteco.ui.theme.Radius
import com.example.wasteco.ui.theme.Spacing

@Composable
fun WasteCoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    readOnly: Boolean = false,
    filledStyle: Boolean = false,
    labelAction: @Composable (() -> Unit)? = null,
    enabled: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (filledStyle) EntryColors.heading else MaterialTheme.colorScheme.onSurface
            )
            labelAction?.invoke()
        }
        Spacer(modifier = Modifier.height(if (filledStyle) Spacing.xs else Spacing.small))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        style = if (filledStyle) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
                        color = if (filledStyle) EntryColors.muted else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = when {
                            isError -> MaterialTheme.colorScheme.error
                            filledStyle -> EntryColors.muted
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            } else null,
            trailingIcon = trailingIcon,
            isError = isError,
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            readOnly = readOnly,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            shape = RoundedCornerShape(Radius.medium),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = if (filledStyle) EntryColors.heading else MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = if (filledStyle) EntryColors.heading else MaterialTheme.colorScheme.onSurface,
                disabledTextColor = if (filledStyle) EntryColors.muted else MaterialTheme.colorScheme.onSurfaceVariant,
                errorTextColor = if (filledStyle) EntryColors.heading else MaterialTheme.colorScheme.onSurface,
                cursorColor = if (filledStyle) EntryColors.action else MaterialTheme.colorScheme.primary,
                focusedBorderColor = if (filledStyle) EntryColors.action else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (filledStyle) Color.Transparent else MaterialTheme.colorScheme.outline,
                disabledBorderColor = if (filledStyle) Color.Transparent else MaterialTheme.colorScheme.outline,
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedContainerColor = if (filledStyle) EntryColors.surface else MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = if (filledStyle) EntryColors.surface else MaterialTheme.colorScheme.surface,
                disabledContainerColor = if (filledStyle) EntryColors.surface else Color.Transparent
            )
        )
        if (isError && !errorMessage.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun WasteCoPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "Masukkan kata sandi",
    leadingIcon: ImageVector? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    filledStyle: Boolean = false,
    labelAction: @Composable (() -> Unit)? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    WasteCoTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        isError = isError,
        errorMessage = errorMessage,
        filledStyle = filledStyle,
        labelAction = labelAction,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        keyboardActions = keyboardActions,
        trailingIcon = {
            val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            val desc = stringResource(if (passwordVisible) R.string.auth_hide_password else R.string.auth_show_password)
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = icon,
                    contentDescription = desc,
                    tint = if (filledStyle) EntryColors.muted else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}
