package com.pdm0126.tutorconnectproyect.presentation.tutors

import com.pdm0126.tutorconnectproyect.data.model.Tutor

data class TutorsUiState(
    val query: String = "",
    val faculties: List<String> = emptyList(),
    val subjects: List<String> = emptyList(),
    val selectedFaculty: String = "Todas",
    val selectedSubject: String = "Todas",
    val tutors: List<Tutor> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
) {
    val filtered: List<Tutor>
        get() = tutors.filter { tutor ->
            (query.isBlank() || tutor.name.contains(query, true) || tutor.specialty.contains(query, true)) &&
                (selectedFaculty == "Todas" || tutor.faculty == selectedFaculty) &&
                (selectedSubject == "Todas" || tutor.subjects.any { it == selectedSubject })
        }.distinctBy { it.id }
}

sealed interface TutorsUiAction {
    data class QueryChanged(val value: String) : TutorsUiAction
    data class FacultySelected(val faculty: String) : TutorsUiAction
    data class SubjectSelected(val subject: String) : TutorsUiAction
    data class TutorClicked(val tutorId: String) : TutorsUiAction
    data object Retry : TutorsUiAction
}

sealed interface TutorsUiEvent {
    data class OpenTutorDetail(val tutorId: String) : TutorsUiEvent
}
