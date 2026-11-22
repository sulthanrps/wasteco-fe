package com.example.wasteco.ui.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wasteco.WasteCoApplication
import com.example.wasteco.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        val error = when {
            email.isBlank() -> "Email tidak boleh kosong"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Format email tidak valid"
            else -> null
        }
        _uiState.update { it.copy(email = email, emailError = error, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        val error = if (password.isBlank()) "Kata sandi tidak boleh kosong" else null
        _uiState.update { it.copy(password = password, passwordError = error, errorMessage = null) }
    }

    fun onLoginSubmit() {
        val currentState = _uiState.value
        if (!currentState.isFormValid) {
            _uiState.update {
                it.copy(
                    emailError = if (it.email.isBlank()) "Email tidak boleh kosong" else it.emailError,
                    passwordError = if (it.password.isBlank()) "Kata sandi tidak boleh kosong" else it.passwordError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.login(
                email = currentState.email.trim(),
                password = currentState.password
            )
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Login gagal. Coba lagi."
                    )
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as WasteCoApplication)
                LoginViewModel(
                    authRepository = application.container.authRepository
                )
            }
        }
    }
}
