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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wasteco.ui.components.WasteCoPasswordField
import com.example.wasteco.ui.components.WasteCoPrimaryButton
import com.example.wasteco.ui.components.WasteCoTextField
import com.example.wasteco.ui.theme.Elevation
import com.example.wasteco.ui.theme.PrimaryLight
import com.example.wasteco.ui.theme.PrimaryMain
import com.example.wasteco.ui.theme.Radius
import com.example.wasteco.ui.theme.Spacing
import com.example.wasteco.ui.theme.WasteCoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onAreaNameChange: (String) -> Unit,
    onAgreementChange: (Boolean) -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.large, vertical = Spacing.small)
        ) {
            // Header Section
            Text(
                text = "Buat Akun WasteCo",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(Spacing.small))

            Text(
                text = "Satu akun untuk mengolah bahan sisa dan meminta atau mendonasikan bahan pangan.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.large))

            // Field: Nama Lengkap
            WasteCoTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                label = "Nama Lengkap",
                placeholder = "Affan Maulana",
                leadingIcon = Icons.Default.Person,
                isError = uiState.nameError != null,
                errorMessage = uiState.nameError
            )

            Spacer(modifier = Modifier.height(Spacing.medium))

            // Field: Alamat Email
            WasteCoTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = "Alamat Email",
                placeholder = "nama@email.com",
                leadingIcon = Icons.Default.Email,
                isError = uiState.emailError != null,
                errorMessage = uiState.emailError
            )

            Spacer(modifier = Modifier.height(Spacing.medium))

            // Field: Kata Sandi
            WasteCoPasswordField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = "Kata Sandi",
                placeholder = "Minimal 8 karakter",
                leadingIcon = Icons.Default.Lock,
                isError = uiState.passwordError != null,
                errorMessage = uiState.passwordError
            )

            // Password strength validation indicator
            Spacer(modifier = Modifier.height(Spacing.xs))
            val isPassValid = uiState.isPasswordLengthValid && uiState.isPasswordCombinationValid
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = Spacing.xs)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (isPassValid) PrimaryMain else MaterialTheme.colorScheme.outline),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPassValid) Icons.Default.Check else Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Spacer(modifier = Modifier.width(Spacing.small))
                Text(
                    text = "Gunakan minimal 8 karakter dengan kombinasi huruf & angka",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isPassValid) PrimaryMain else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(Spacing.medium))

            // Field: Konfirmasi Kata Sandi
            WasteCoPasswordField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = "Konfirmasi Kata Sandi",
                placeholder = "Ulangi kata sandi",
                leadingIcon = Icons.Default.Lock,
                isError = uiState.confirmPasswordError != null,
                errorMessage = uiState.confirmPasswordError
            )

            Spacer(modifier = Modifier.height(Spacing.medium))

            // Field: Area Tinggal Umum
            WasteCoTextField(
                value = uiState.areaName,
                onValueChange = onAreaNameChange,
                label = "Lokasi Kamu (Area Umum)",
                placeholder = "Dinoyo, Lowokwaru, Malang",
                leadingIcon = Icons.Default.LocationOn,
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { /* Auto-fill GPS */ }
                            .padding(end = Spacing.small)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Via GPS",
                            tint = PrimaryMain,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(Spacing.xxs))
                        Text(
                            text = "Via GPS",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryMain,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(Spacing.large))

            // Community Sharing Agreement Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAgreementChange(!uiState.isAgreementChecked) },
                shape = RoundedCornerShape(Radius.medium),
                colors = CardDefaults.cardColors(containerColor = PrimaryLight),
                elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.medium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = uiState.isAgreementChecked,
                        onCheckedChange = onAgreementChange,
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryMain,
                            uncheckedColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    Spacer(modifier = Modifier.width(Spacing.small))
                    Text(
                        text = "Saya setuju dengan prinsip berbagi makanan yang aman, bersih, dan higienis di WasteCo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (!uiState.errorMessage.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(Spacing.medium))
                Text(
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xlarge))

            // Button CTA Utama
            WasteCoPrimaryButton(
                text = "Buat Akun",
                onClick = onRegisterClick,
                isLoading = uiState.isLoading,
                trailingIcon = Icons.AutoMirrored.Filled.ArrowForward
            )

            Spacer(modifier = Modifier.height(Spacing.medium))

            // Footer Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.large),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val annotatedText = buildAnnotatedString {
                    append("Sudah punya akun? ")
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("Masuk")
                    }
                }
                Text(
                    text = annotatedText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    WasteCoTheme {
        RegisterScreen(
            uiState = RegisterUiState(),
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onAreaNameChange = {},
            onAgreementChange = {},
            onRegisterClick = {},
            onLoginClick = {},
            onBackClick = {}
        )
    }
}
