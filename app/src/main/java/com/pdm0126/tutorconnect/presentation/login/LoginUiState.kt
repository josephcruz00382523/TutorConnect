package com.pdm0126.tutorconnectproyect.presentation.login

import com.pdm0126.tutorconnectproyect.data.model.UserRole


data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null,
    val isLoading: Boolean = false,
    // Registro
    val isRegisterMode: Boolean = false,
    val fullName: String = "",
    val selectedRole: UserRole = UserRole.TUTORADO,
)

sealed interface LoginUiAction {
    data class EmailChanged(val value: String) : LoginUiAction
    data class PasswordChanged(val value: String) : LoginUiAction
    data class FullNameChanged(val value: String) : LoginUiAction
    data class RoleSelected(val role: UserRole) : LoginUiAction
    data object Submit : LoginUiAction
    data object MicrosoftLogin : LoginUiAction
    data object ForgotPassword : LoginUiAction
    data object ToggleMode : LoginUiAction
}

sealed interface LoginUiEvent {
    data class NavigateToHome(val isTutor: Boolean) : LoginUiEvent
    data class ShowMessage(val message: String) : LoginUiEvent
}
