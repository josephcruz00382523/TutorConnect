package com.pdm0126.tutorconnectproyect.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pdm0126.tutorconnectproyect.core.components.Avatar
import com.pdm0126.tutorconnectproyect.core.components.ErrorState
import com.pdm0126.tutorconnectproyect.core.components.LoadingState
import com.pdm0126.tutorconnectproyect.data.model.UserProfile
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                ProfileUiEvent.LoggedOut -> onLogout()
                is ProfileUiEvent.ShowMessage -> snackbar.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = { TopAppBar(title = { Text("Perfil") }) },
    ) { padding ->
        when {
            state.isLoading -> LoadingState(Modifier.padding(padding))
            state.error != null -> ErrorState(state.error!!, Modifier.padding(padding)) {
                viewModel.onAction(ProfileUiAction.Retry)
            }
            else -> state.user?.let { user ->
                ProfileContent(
                    user = user,
                    isLoggingOut = state.isLoggingOut,
                    onEdit = { viewModel.onAction(ProfileUiAction.EditProfile) },
                    onLogout = { viewModel.onAction(ProfileUiAction.Logout) },
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    user: UserProfile,
    isLoggingOut: Boolean,
    onEdit: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Avatar(photoUrl = user.photoUrl, size = 96)
        Text(user.fullName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(1.dp),
        ) {
            ListItem(
                leadingContent = { Icon(Icons.Filled.Email, contentDescription = null) },
                headlineContent = { Text(user.institutionalEmail) },
                overlineContent = { Text("Correo institucional") },
            )
            ListItem(
                leadingContent = { Icon(Icons.Filled.School, contentDescription = null) },
                headlineContent = { Text(user.career) },
                overlineContent = { Text("Carrera") },
            )
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(onClick = onEdit, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.Edit, contentDescription = null)
            Text("  Editar perfil")
        }

        OutlinedButton(
            onClick = onLogout,
            enabled = !isLoggingOut,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isLoggingOut) {
                CircularProgressIndicator(Modifier.height(20.dp), strokeWidth = 2.dp)
            } else {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Text("  Cerrar sesión")
            }
        }
    }
}
