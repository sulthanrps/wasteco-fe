package com.example.wasteco.ui.feature.auth.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wasteco.ui.feature.profile.rememberDeviceLocationRequest

@Composable
fun RegisterRoute(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = viewModel(factory = RegisterViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onRegisterSuccess()
        }
    }

    val locate = rememberDeviceLocationRequest(
        onStart = viewModel::locate,
        onLocation = viewModel::setLocation,
        onUnavailable = viewModel::locationUnavailable
    )
    RegisterScreen(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPhoneNumberChange = viewModel::onPhoneNumberChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onAreaNameChange = viewModel::onAreaNameChange,
        onLocate = locate,
        onAgreementChange = viewModel::onAgreementChange,
        onRegisterClick = viewModel::onRegisterSubmit,
        onLoginClick = onLoginClick,
        onBackClick = onBackClick,
        modifier = modifier
    )
}
