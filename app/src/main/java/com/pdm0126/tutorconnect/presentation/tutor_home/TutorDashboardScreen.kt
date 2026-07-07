package com.pdm0126.tutorconnectproyect.presentation.tutor_home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pdm0126.tutorconnectproyect.core.components.AppBottomBar
import com.pdm0126.tutorconnectproyect.core.components.PrimaryButton
import com.pdm0126.tutorconnectproyect.core.navigation.AppDestinations
import com.pdm0126.tutorconnectproyect.core.theme.UcaAccent
import com.pdm0126.tutorconnectproyect.core.theme.UcaNavy
import com.pdm0126.tutorconnectproyect.core.theme.UcaNavyDark
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Pantalla principal del TUTOR.
 * Muestra: sus materias, próximas sesiones, y formulario de nueva publicación (foto/PDF).
 * Los datos reales vendrán del backend MongoDB.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorDashboardScreen(
    onOpenCalendar: () -> Unit,
    onOpenCreatePost: () -> Unit,
    onSwitchToStudentView: () -> Unit,
    onNavigate: (AppDestinations) -> Unit,
    onOpenMessages: () -> Unit,
    onLogout: () -> Unit,
    viewModel: TutorDashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                TutorDashboardUiEvent.SwitchToStudentView -> onSwitchToStudentView()
                TutorDashboardUiEvent.NavigateToLogin -> onLogout()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "TutorConnect UCA",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "Panel del Tutor",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onSwitchToStudent() }) {
                        Icon(Icons.Filled.SwapHoriz, "Cambiar a Estudiante", tint = Color.White)
                    }
                    IconButton(onClick = onOpenMessages) {
                        Icon(Icons.AutoMirrored.Filled.Chat, "Mensajes", tint = Color.White)
                    }
                    IconButton(onClick = { viewModel.onLogout() }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, "Cerrar sesión", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = UcaNavy,
                    titleContentColor = Color.White,
                ),
            )
        },
        bottomBar = {
            AppBottomBar(
                isTutor = true,
                current = AppDestinations.TutorDashboard,
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { Spacer(Modifier.height(8.dp)) }

            // Header de bienvenida
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.horizontalGradient(listOf(UcaNavy, Color(0xFF1E5BA8)))),
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(56.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.Person, null, tint = Color.White, modifier = Modifier.size(32.dp))
                            }
                        }
                        Column {
                            Text("¡Hola, Tutor!", color = Color.White, fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium)
                            Text("Tienes sesiones pendientes", color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // Mis materias de tutoría
            item {
                TutorSectionCard(title = "Mis Materias de Tutoría") {
                    // Vacío — vendrá del backend
                    TutorSubjectRow("Cálculo Avanzado")
                    TutorSubjectRow("Principios de Programación")
                }
            }

            // Próximas sesiones
            item {
                TutorSectionCard(title = "Próximas Sesiones") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "Ver en Calendario",
                            style = MaterialTheme.typography.bodySmall,
                            color = UcaAccent,
                        )
                        TextButton(onClick = onOpenCalendar) {
                            Icon(Icons.Filled.CalendarMonth, null,
                                modifier = Modifier.size(16.dp), tint = UcaAccent)
                            Text(" Abrir", color = UcaAccent, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    // Las sesiones reales vendrán del backend
                    Text(
                        "Las sesiones aparecerán aquí cuando el backend esté conectado.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }

            // Acceso rápido a crear publicación
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = UcaAccent.copy(alpha = 0.08f)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        width = 1.dp,
                    ),
                    modifier = Modifier.fillMaxWidth().animateContentSize(),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Publicar Recurso",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = UcaNavy,
                        )
                        Text(
                            "Sube fotos o PDFs para que tus tutorados los vean.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                        )
                        PrimaryButton(
                            text = "Crear Publicación",
                            onClick = onOpenCreatePost,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun TutorSectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = UcaNavy,
            )
            content()
        }
    }
}

@Composable
private fun TutorSubjectRow(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(name, style = MaterialTheme.typography.bodyMedium)
        AssistChip(
            onClick = {},
            label = { Text("Activa", style = MaterialTheme.typography.labelSmall) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = UcaAccent.copy(alpha = 0.15f),
                labelColor = UcaAccent,
            ),
            border = null,
        )
    }
}
