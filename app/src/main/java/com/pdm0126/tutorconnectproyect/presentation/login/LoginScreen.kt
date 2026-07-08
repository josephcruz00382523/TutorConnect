package com.pdm0126.tutorconnectproyect.presentation.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pdm0126.tutorconnectproyect.core.components.AppTextField
import com.pdm0126.tutorconnectproyect.core.components.PrimaryButton
import com.pdm0126.tutorconnectproyect.core.theme.UcaAccent
import com.pdm0126.tutorconnectproyect.core.theme.UcaNavy
import com.pdm0126.tutorconnectproyect.core.theme.UcaNavyDark
import com.pdm0126.tutorconnectproyect.data.model.UserRole
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    onLoginSuccess: (isTutor: Boolean) -> Unit,
    viewModel: LoginViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is LoginUiEvent.NavigateToHome -> onLoginSuccess(event.isTutor)
                is LoginUiEvent.ShowMessage -> snackbar.showSnackbar(event.message)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(UcaNavyDark, UcaNavy, Color(0xFF1E4D8C))
                )
            ),
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbar) },
            containerColor = Color.Transparent,
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Spacer(Modifier.height(56.dp))

                // Logo animado
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.size(96.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.Hub,
                            contentDescription = "Logo",
                            tint = Color.White,
                            modifier = Modifier.size(52.dp),
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    "TutorConnect UCA",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Text(
                    "Tu plataforma de tutorías",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                )

                Spacer(Modifier.height(32.dp))

                // Card principal con animación de contenido
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    AnimatedContent(
                        targetState = state.isRegisterMode,
                        transitionSpec = {
                            (slideInVertically { it } + fadeIn()) togetherWith
                                    (slideOutVertically { -it } + fadeOut())
                        },
                        label = "login_register_switch",
                    ) { isRegister ->
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(
                                if (isRegister) "Crear cuenta" else "Iniciar sesión",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = UcaNavy,
                            )

                            // Nombre (solo en registro)
                            AnimatedVisibility(visible = isRegister) {
                                AppTextField(
                                    value = state.fullName,
                                    onValueChange = { viewModel.onAction(LoginUiAction.FullNameChanged(it)) },
                                    label = "Nombre completo",
                                    leadingIcon = Icons.Filled.Person,
                                )
                            }

                            AppTextField(
                                value = state.email,
                                onValueChange = { viewModel.onAction(LoginUiAction.EmailChanged(it)) },
                                label = "Correo institucional",
                                leadingIcon = Icons.Filled.Email,
                                isError = state.emailError != null,
                                supportingText = state.emailError,
                            )

                            AppTextField(
                                value = state.password,
                                onValueChange = { viewModel.onAction(LoginUiAction.PasswordChanged(it)) },
                                label = "Contraseña",
                                leadingIcon = Icons.Filled.Lock,
                                isPassword = true,
                                isError = state.passwordError != null,
                                supportingText = state.passwordError,
                            )

                            // Selección de rol (solo en registro)
                            AnimatedVisibility(visible = isRegister) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        "¿Cuál es tu rol?",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = UcaNavy,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        RoleChip(
                                            label = "Tutorado",
                                            icon = Icons.Filled.School,
                                            selected = state.selectedRole == UserRole.TUTORADO,
                                            onClick = { viewModel.onAction(LoginUiAction.RoleSelected(UserRole.TUTORADO)) },
                                            modifier = Modifier.weight(1f),
                                        )
                                        RoleChip(
                                            label = "Tutor",
                                            icon = Icons.Filled.Person,
                                            selected = state.selectedRole == UserRole.TUTOR,
                                            onClick = { viewModel.onAction(LoginUiAction.RoleSelected(UserRole.TUTOR)) },
                                            modifier = Modifier.weight(1f),
                                        )
                                    }
                                }
                            }

                            if (!isRegister) {
                                TextButton(
                                    onClick = { viewModel.onAction(LoginUiAction.ForgotPassword) },
                                    modifier = Modifier.align(Alignment.End),
                                ) { Text("Se me olvidó la contraseña", color = UcaAccent) }
                            }

                            state.generalError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth())
                            }

                            PrimaryButton(
                                text = if (isRegister) "Registrarme" else "Iniciar Sesión",
                                onClick = { viewModel.onAction(LoginUiAction.Submit) },
                                loading = state.isLoading,
                                modifier = Modifier.fillMaxWidth(),
                            )

                            if (!isRegister) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(1.dp)
                                            .background(Color.LightGray)
                                    )
                                    Text(
                                        "  ó  ",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray,
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(1.dp)
                                            .background(Color.LightGray)
                                    )
                                }

                                OutlinedButton(
                                    onClick = { viewModel.onAction(LoginUiAction.MicrosoftLogin) },
                                    enabled = !state.isLoading,
                                    modifier = Modifier.fillMaxWidth().height(52.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, UcaNavy.copy(alpha = 0.4f)),
                                ) {
                                    Icon(Icons.Filled.Email, contentDescription = null, tint = UcaNavy)
                                    Spacer(Modifier.size(8.dp))
                                    Text("Conectar con Microsoft", color = UcaNavy)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    if (isRegister) "¿Ya tienes cuenta? " else "¿No tienes cuenta? ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                )
                                TextButton(onClick = { viewModel.onAction(LoginUiAction.ToggleMode) }) {
                                    Text(
                                        if (isRegister) "Inicia sesión" else "Regístrate",
                                        color = UcaAccent,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun RoleChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor = if (selected) UcaNavy else Color.Transparent
    val contentColor = if (selected) Color.White else UcaNavy
    val borderColor = if (selected) UcaNavy else UcaNavy.copy(alpha = 0.3f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(18.dp))
            Text(label, color = contentColor, fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelLarge)
        }
    }
}
