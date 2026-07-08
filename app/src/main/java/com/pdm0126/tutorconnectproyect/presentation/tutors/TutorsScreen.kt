package com.pdm0126.tutorconnectproyect.presentation.tutors

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pdm0126.tutorconnectproyect.core.components.AppBottomBar
import com.pdm0126.tutorconnectproyect.core.components.Avatar
import com.pdm0126.tutorconnectproyect.core.components.EmptyState
import com.pdm0126.tutorconnectproyect.core.components.ErrorState
import com.pdm0126.tutorconnectproyect.core.components.LoadingState
import com.pdm0126.tutorconnectproyect.core.components.StatusChip
import com.pdm0126.tutorconnectproyect.core.navigation.AppDestinations
import com.pdm0126.tutorconnectproyect.data.model.Tutor
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorsScreen(
    onNavigate: (AppDestinations) -> Unit,
    onTutorClick: (String) -> Unit,
    viewModel: TutorsViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var searchActive by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is TutorsUiEvent.OpenTutorDetail -> onTutorClick(event.tutorId)
            }
        }
    }

    Scaffold(
        bottomBar = {
            AppBottomBar(
                isTutor = false,
                current = AppDestinations.Tutors,
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SearchBar(
                query = state.query,
                onQueryChange = { viewModel.onAction(TutorsUiAction.QueryChanged(it)) },
                onSearch = { searchActive = false },
                active = searchActive,
                onActiveChange = { searchActive = it },
                placeholder = { Text("Buscar tutor o especialidad") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                colors = SearchBarDefaults.colors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            ) {
                // Live suggestions while the search bar is expanded
                LazyColumn {
                    items(state.filtered, key = { "suggestion_${it.id}" }) { tutor ->
                        Text(
                            "${tutor.name} — ${tutor.specialty}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    searchActive = false
                                    viewModel.onAction(TutorsUiAction.TutorClicked(tutor.id))
                                }
                                .padding(16.dp),
                        )
                    }
                }
            }

            // Faculty filters
            FilterRow(
                options = state.faculties,
                selected = state.selectedFaculty,
                onSelect = { viewModel.onAction(TutorsUiAction.FacultySelected(it)) },
            )
            // Subject filters
            FilterRow(
                options = state.subjects,
                selected = state.selectedSubject,
                onSelect = { viewModel.onAction(TutorsUiAction.SubjectSelected(it)) },
            )

            when {
                state.isLoading -> LoadingState()
                state.error != null -> ErrorState(state.error!!) { viewModel.onAction(TutorsUiAction.Retry) }
                state.filtered.isEmpty() -> EmptyState("No hay tutores que coincidan con tu búsqueda.")
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(state.filtered, key = { "list_${it.id}" }) { tutor ->
                        TutorCard(tutor) { viewModel.onAction(TutorsUiAction.TutorClicked(tutor.id)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterRow(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(
        Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelect(option) },
                label = { Text(option) },
            )
        }
    }
}

@Composable
private fun TutorCard(tutor: Tutor, onClick: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Row(
            Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(tutor.photoUrl, size = 52)
            Column(Modifier.weight(1f)) {
                Text(tutor.name, style = MaterialTheme.typography.titleMedium)
                Text(tutor.specialty, style = MaterialTheme.typography.bodyMedium)
                Text(tutor.faculty, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
            }
            StatusChip(tutor.status)
        }
    }
}
