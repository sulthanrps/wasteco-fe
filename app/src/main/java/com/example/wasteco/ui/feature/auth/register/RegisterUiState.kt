package com.example.wasteco.ui.feature.auth.register

data class RegisterUiState(
    val name: String = "",
    val nameError: String? = null,
    val email: String = "",
    val phoneNumber: String = "",
    val phoneNumberInvalid: Boolean = false,
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val areaName: String = "",
    val areaNameError: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val sharingRadiusKm: Int = 3,
    val isLocating: Boolean = false,
    val locationError: Boolean = false,
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
                phoneNumber.isNotBlank() &&
                areaName.isNotBlank() &&
                password.isNotBlank() &&
                passwordError == null &&
                confirmPassword.isNotBlank() &&
                confirmPasswordError == null &&
                isAgreementChecked
}
