package com.pdm0126.tutorconnectproyect.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm0126.tutorconnectproyect.data.repository.PostRepository
import com.pdm0126.tutorconnectproyect.domain.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val authRepository: com.pdm0126.tutorconnectproyect.data.repository.AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _events = kotlinx.coroutines.channels.Channel<DashboardUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        observePosts()
        observeUser()
    }

    private fun observeUser() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                user?.let { u ->
                    _uiState.update { 
                        it.copy(
                            studentName = u.name,
                            isTutor = u.isTutor || u.role == "TUTOR"
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: DashboardUiAction) {
        when (action) {
            DashboardUiAction.Retry -> observePosts()
            is DashboardUiAction.ReplyToPost -> {}
            DashboardUiAction.OpenTutors -> viewModelScope.launch { _events.send(DashboardUiEvent.NavigateToTutors) }
            DashboardUiAction.SwitchToTutorView -> viewModelScope.launch { _events.send(DashboardUiEvent.NavigateToTutorView) }
            DashboardUiAction.Logout -> {
                viewModelScope.launch {
                    authRepository.logout()
                    _events.send(DashboardUiEvent.NavigateToLogin)
                }
            }
        }
    }

    fun observePosts() {
        viewModelScope.launch {
            postRepository.getAllPosts().collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true, error = null) }
                    is Resource.Success -> {
                        val mapeado = result.data.map {
                            com.pdm0126.tutorconnectproyect.data.model.FeaturedPost(
                                id = it.id, authorName = it.authorName, question = it.title
                            )
                        }
                        _uiState.update { it.copy(isLoading = false, featuredPosts = mapeado) }
                    }

                    is Resource.Error -> _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}
