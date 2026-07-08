package com.pdm0126.tutorconnectproyect.presentation.tutor_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pdm0126.tutorconnectproyect.core.components.Avatar
import com.pdm0126.tutorconnectproyect.core.components.ErrorState
import com.pdm0126.tutorconnectproyect.core.components.LoadingState
import com.pdm0126.tutorconnectproyect.core.components.StatusChip
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorDetailScreen(
    tutorId: String,
    onBack: () -> Unit,
    onOpenChat: (String, String) -> Unit,
    onBook: (String, String) -> Unit,
    viewModel: TutorDetailViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(tutorId) { viewModel.onAction(TutorDetailUiAction.Load(tutorId)) }
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is TutorDetailUiEvent.OpenChat -> onOpenChat(event.tutorId, event.tutorName)
                is TutorDetailUiEvent.Book -> onBook(event.tutorId, event.tutorName)
                TutorDetailUiEvent.Back -> onBack()
                is TutorDetailUiEvent.ShowMessage -> snackbar.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Tutor") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onAction(TutorDetailUiAction.Back) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
            )
        },
    ) { padding ->
        when {
            state.isLoading -> LoadingState(Modifier.padding(padding))
            state.error != null -> ErrorState(state.error!!, Modifier.padding(padding))
            else -> state.tutor?.let { tutor ->
                Column(
                    Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Avatar(tutor.photoUrl, size = 72)
                        Column {
                            Text(tutor.name, style = MaterialTheme.typography.headlineSmall)
                            Text(tutor.specialty, style = MaterialTheme.typography.bodyLarge)
                            StatusChip(tutor.status, Modifier.padding(top = 4.dp))
                        }
                    }

                    Text("⭐ ${tutor.rating}  ·  ${tutor.faculty}", style = MaterialTheme.typography.bodyMedium)
                    Text(tutor.bio, style = MaterialTheme.typography.bodyMedium)

                    InfoCard("Materias", tutor.subjects)
                    InfoCard("Horarios", tutor.schedule)

                    Button(
                        onClick = { viewModel.onAction(TutorDetailUiAction.SendNudge) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Filled.WavingHand, null); Text("  Enviar Nudge")
                    }
                    OutlinedButton(
                        onClick = { viewModel.onAction(TutorDetailUiAction.OpenChat) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, null); Text("  Abrir Chat")
                    }
                    OutlinedButton(
                        onClick = { viewModel.onAction(TutorDetailUiAction.Book) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Filled.CalendarMonth, null); Text("  Reservar Tutoría")
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, items: List<String>) {
    Card(elevation = CardDefaults.cardElevation(1.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (items.isEmpty()) Text("Sin información", style = MaterialTheme.typography.bodyMedium)
            items.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
        }
    }
}
