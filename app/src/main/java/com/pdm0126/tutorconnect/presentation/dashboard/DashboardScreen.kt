package com.pdm0126.tutorconnectproyect.presentation.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pdm0126.tutorconnectproyect.core.components.AppBottomBar
import com.pdm0126.tutorconnectproyect.core.components.Avatar
import com.pdm0126.tutorconnectproyect.core.components.ErrorState
import com.pdm0126.tutorconnectproyect.core.components.LoadingState
import com.pdm0126.tutorconnectproyect.core.navigation.AppDestinations
import com.pdm0126.tutorconnectproyect.core.theme.UcaAccent
import com.pdm0126.tutorconnectproyect.core.theme.UcaNavy
import com.pdm0126.tutorconnectproyect.core.theme.UcaNavyDark
import com.pdm0126.tutorconnectproyect.data.model.FeaturedPost
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigate: (AppDestinations) -> Unit,
    onOpenMessages: () -> Unit,
    onOpenComments: (postId: String, title: String, author: String) -> Unit,
    onSwitchToTutorView: () -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var visible by remember { mutableStateOf(false) }
    var searchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredPosts = remember(state.featuredPosts, searchQuery) {
        if (searchQuery.isEmpty()) state.featuredPosts
        else state.featuredPosts.filter { it.authorName.contains(searchQuery, ignoreCase = true) }
    }

    LaunchedEffect(Unit) {
        visible = true
        viewModel.events.collectLatest { event ->
            when (event) {
                DashboardUiEvent.NavigateToTutors -> {} // Removed notification icon usage
                DashboardUiEvent.NavigateToTutorView -> onSwitchToTutorView()
                is DashboardUiEvent.ShowMessage -> snackbar.showSnackbar(event.message)
                DashboardUiEvent.NavigateToLogin -> onLogout()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    if (searchActive) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Buscar tutor...") },
                            modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                cursorColor = Color.White,
                            ),
                        )
                    } else {
                        Text("TutorConnect UCA", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { searchActive = !searchActive }) {
                        Icon(Icons.Filled.Search, "Buscar")
                    }
                },
                actions = {
                    if (state.isTutor) {
                        IconButton(onClick = { viewModel.onAction(DashboardUiAction.SwitchToTutorView) }) {
                            Icon(Icons.Filled.SwapHoriz, "Cambiar a Tutor", tint = Color.White)
                        }
                    }
                    IconButton(onClick = onOpenMessages) {
                        Icon(Icons.AutoMirrored.Filled.Chat, "Mensajes", tint = Color.White)
                    }
                    IconButton(onClick = { viewModel.onAction(DashboardUiAction.Logout) }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, "Cerrar sesión", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = UcaNavy,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                ),
            )
        },
        bottomBar = {
            AppBottomBar(
                isTutor = false,
                current = AppDestinations.Dashboard,
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        when {
            state.isLoading -> LoadingState(Modifier.padding(padding))
            state.error != null -> ErrorState(state.error!!, Modifier.padding(padding)) {
                viewModel.onAction(DashboardUiAction.Retry)
            }
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item { Spacer(Modifier.height(4.dp)) }

                // Saludo con gradiente
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { -it / 2 },
                    ) {
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
                                    modifier = Modifier.size(52.dp),
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            state.studentName.take(1).ifEmpty { "?" },
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        "¡Hola, ${state.studentName.substringBefore(' ').ifEmpty { "Estudiante" }}!",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                    Text(
                                        "Bienvenido a TutorConnect",
                                        color = Color.White.copy(alpha = 0.75f),
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                            }
                        }
                    }
                }

                // Mis Materias
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 2 },
                    ) {
                        SectionCard("Mis Materias de Tutoría") {
                            if (state.tutorSubjects.isEmpty()) {
                                EmptySection("Las materias aparecerán cuando el backend esté conectado.")
                            } else {
                                state.tutorSubjects.forEach { SubjectRow(it) }
                            }
                        }
                    }
                }

                // Carga adicional
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 2 },
                    ) {
                        SectionCard("Carga Académica Adicional") {
                            if (state.additionalLoad.isEmpty()) {
                                EmptySection("Sin carga adicional registrada.")
                            } else {
                                state.additionalLoad.forEach { SubjectRow(it) }
                            }
                        }
                    }
                }

                // Posts destacados
                item {
                    AnimatedVisibility(visible = visible, enter = fadeIn(tween(700))) {
                        Text(
                            "Post de Preguntas Destacadas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = UcaNavyDark,
                        )
                    }
                }

                if (filteredPosts.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(1.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                "No se encontraron resultados para la búsqueda o el backend no está conectado.",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                            )
                        }
                    }
                } else {
                    items(filteredPosts.distinctBy { it.id }, key = { it.id }) { post ->
                        PostCard(
                            post = post,
                            onReply = { onOpenComments(post.id, post.question, post.authorName) }
                        )
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
private fun SubjectRow(subject: Subject) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(subject.name, style = MaterialTheme.typography.bodyMedium)
        if (subject.completed) {
            Icon(
                Icons.Filled.CheckCircle,
                "Activa",
                tint = UcaAccent,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun EmptySection(message: String) {
    Text(message, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
}

@Composable
private fun PostCard(post: FeaturedPost, onReply: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onReply),
    ) {
        Row(
            Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Avatar(post.photoUrl, size = 44)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(post.authorName, style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold, color = UcaNavy)
                    Text(" ${post.handle}", style = MaterialTheme.typography.labelMedium,
                        color = UcaAccent)
                }
                Text(post.question, style = MaterialTheme.typography.bodyMedium)
                TextButton(
                    onClick = onReply,
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text("Responder", color = UcaAccent, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
