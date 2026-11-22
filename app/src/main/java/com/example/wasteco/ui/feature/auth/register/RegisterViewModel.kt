package com.example.wasteco.ui.feature.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wasteco.WasteCoApplication
import com.example.wasteco.data.repository.AuthRepository
import com.example.wasteco.data.repository.normalizedWhatsAppNumber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        val error = if (name.isBlank()) "Nama tidak boleh kosong" else null
        _uiState.update { it.copy(name = name, nameError = error, errorMessage = null) }
    }

    fun onPhoneNumberChange(phoneNumber: String) {
        _uiState.update { it.copy(phoneNumber = phoneNumber, phoneNumberInvalid = false, errorMessage = null) }
    }

    fun onEmailChange(email: String) {
        val error = when {
            email.isBlank() -> "Email tidak boleh kosong"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Format email tidak valid"
            else -> null
        }
        _uiState.update { it.copy(email = email, emailError = error, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        val error = when {
            password.length < 8 -> "Minimal 8 karakter"
            !(password.any { it.isLetter() } && password.any { it.isDigit() }) -> "Gunakan kombinasi huruf & angka"
            else -> null
        }
        _uiState.update {
            val confirmError = if (it.confirmPassword.isNotEmpty() && it.confirmPassword != password) {
                "Konfirmasi kata sandi tidak cocok"
            } else null
            it.copy(password = password, passwordError = error, confirmPasswordError = confirmError, errorMessage = null)
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        val error = if (confirmPassword != _uiState.value.password) {
            "Konfirmasi kata sandi tidak cocok"
        } else null
        _uiState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = error, errorMessage = null) }
    }

    fun onAreaNameChange(areaName: String) {
        _uiState.update { it.copy(areaName = areaName, areaNameError = null) }
    }

    fun locate() {
        _uiState.update { it.copy(isLocating = true, locationError = false) }
    }

    fun setLocation(latitude: Double, longitude: Double, areaName: String?) {
        _uiState.update { state ->
            state.copy(
                latitude = latitude,
                longitude = longitude,
                areaName = areaName ?: state.areaName,
                areaNameError = null,
                isLocating = false,
                locationError = areaName == null
            )
        }
    }

    fun locationUnavailable() {
        _uiState.update { it.copy(isLocating = false, locationError = true) }
    }

    fun onAgreementChange(isChecked: Boolean) {
        _uiState.update { it.copy(isAgreementChecked = isChecked, errorMessage = null) }
    }

    fun onRegisterSubmit() {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isLocating) return
        if (!currentState.isFormValid) {
            _uiState.update {
                it.copy(
                    nameError = if (it.name.isBlank()) "Nama tidak boleh kosong" else it.nameError,
                    emailError = if (it.email.isBlank()) "Email tidak boleh kosong" else it.emailError,
                    passwordError = if (it.password.isBlank()) "Kata sandi tidak boleh kosong" else it.passwordError,
                    confirmPasswordError = if (it.confirmPassword.isBlank()) "Konfirmasi sandi tidak boleh kosong" else it.confirmPasswordError,
                    errorMessage = if (!it.isAgreementChecked) "Harap setujui prinsip berbagi makanan terlebih dahulu" else null
                )
            }
            return
        }

        val phoneNumber = currentState.phoneNumber.normalizedWhatsAppNumber()
        if (phoneNumber == null) {
            _uiState.update { it.copy(phoneNumberInvalid = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.register(
                name = currentState.name.trim(),
                email = currentState.email.trim(),
                password = currentState.password,
                phoneNumber = phoneNumber,
                areaName = currentState.areaName.ifBlank { "Dinoyo, Lowokwaru, Malang" },
                latitude = currentState.latitude,
                longitude = currentState.longitude,
                sharingRadiusKm = currentState.sharingRadiusKm
            )

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Registrasi gagal. Coba lagi."
                    )
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as WasteCoApplication)
                RegisterViewModel(
                    authRepository = application.container.authRepository
                )
            }
        }
    }
}
