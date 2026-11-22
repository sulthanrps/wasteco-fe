package com.example.wasteco.ui.feature.auth.register

data class RegisterUiState(
    val name: String = "",
    val nameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val areaName: String = "Dinoyo, Lowokwaru, Malang",
    val areaNameError: String? = null,
    val latitude: Double = -7.9528,
    val longitude: Double = 112.6074,
    val sharingRadiusKm: Int = 3,
    val isAgreementChecked: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
) {
    val isPasswordLengthValid: Boolean
        get() = password.length >= 8

    val isPasswordCombinationValid: Boolean
        get() = password.any { it.isLetter() } && password.any { it.isDigit() }

    val isFormValid: Boolean
        get() = name.isNotBlank() &&
                email.isNotBlank() &&
                emailError == null &&
                password.isNotBlank() &&
                passwordError == null &&
                confirmPassword.isNotBlank() &&
                confirmPasswordError == null &&
                isAgreementChecked
}
