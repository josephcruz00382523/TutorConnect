package com.pdm0126.tutorconnectproyect.presentation.booking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pdm0126.tutorconnectproyect.core.components.AppTextField
import com.pdm0126.tutorconnectproyect.core.components.PrimaryButton
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    tutorId: String,
    tutorName: String,
    onBack: () -> Unit,
    onBooked: () -> Unit,
    viewModel: BookingViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(tutorId, tutorName) {
        viewModel.onAction(BookingUiAction.Init(tutorId, tutorName))
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                BookingUiEvent.Booked -> onBooked()
                BookingUiEvent.Back -> onBack()
                is BookingUiEvent.ShowMessage -> snackbar.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("Reservar Tutoría") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onAction(BookingUiAction.Back) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Tutor", style = MaterialTheme.typography.labelMedium)
            Text(
                state.tutorName.ifBlank { "Tutor seleccionado" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            AppTextField(
                value = state.subject,
                onValueChange = { viewModel.onAction(BookingUiAction.SubjectChanged(it)) },
                label = "Materia",
                leadingIcon = Icons.Filled.School,
                isError = state.subjectError,
                supportingText = if (state.subjectError) "Indica la materia" else null,
            )

            AppTextField(
                value = state.date,
                onValueChange = { viewModel.onAction(BookingUiAction.DateChanged(it)) },
                label = "Fecha (ej. 2026-06-15)",
                leadingIcon = Icons.Filled.CalendarMonth,
                isError = state.dateError,
                supportingText = if (state.dateError) "Indica la fecha" else null,
            )

            AppTextField(
                value = state.time,
                onValueChange = { viewModel.onAction(BookingUiAction.TimeChanged(it)) },
                label = "Hora (ej. 10:30)",
                leadingIcon = Icons.Filled.Schedule,
                isError = state.timeError,
                supportingText = if (state.timeError) "Indica la hora" else null,
            )

            OutlinedTextField(
                value = state.comments,
                onValueChange = { viewModel.onAction(BookingUiAction.CommentsChanged(it)) },
                label = { Text("Comentarios (opcional)") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            )

            PrimaryButton(
                text = "Reservar",
                onClick = { viewModel.onAction(BookingUiAction.Submit) },
                loading = state.isSubmitting,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
