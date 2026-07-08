package com.pdm0126.tutorconnectproyect.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Type-safe navigation keys for the entire app.
 */
sealed interface AppDestinations : NavKey {

    @Serializable
    data object Login : AppDestinations

    @Serializable
    data object Dashboard : AppDestinations

    @Serializable
    data object TutorDashboard : AppDestinations

    @Serializable
    data object Tutors : AppDestinations

    @Serializable
    data class TutorDetail(val tutorId: String) : AppDestinations

    @Serializable
    data class Chat(val tutorId: String, val tutorName: String) : AppDestinations

    @Serializable
    data class PostDetail(val postId: String, val postTitle: String, val authorName: String) : AppDestinations

    @Serializable
    data class Booking(val tutorId: String, val tutorName: String) : AppDestinations

    @Serializable
    data class Calendar(val isTutor: Boolean = false) : AppDestinations

    @Serializable
    data class GroupChats(val isTutor: Boolean = false) : AppDestinations

    @Serializable
    data class GroupChat(val groupChatId: String, val groupChatName: String) : AppDestinations

    @Serializable
    data object Messages : AppDestinations

    @Serializable
    data object CreatePost : AppDestinations

    @Serializable
    data object Profile : AppDestinations
}
