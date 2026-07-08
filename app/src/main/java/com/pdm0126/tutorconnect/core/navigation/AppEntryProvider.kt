package com.pdm0126.tutorconnectproyect.core.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.pdm0126.tutorconnectproyect.presentation.booking.BookingScreen
import com.pdm0126.tutorconnectproyect.presentation.calendar.CalendarScreen
import com.pdm0126.tutorconnectproyect.presentation.chat.ChatScreen
import com.pdm0126.tutorconnectproyect.presentation.dashboard.DashboardScreen
import com.pdm0126.tutorconnectproyect.presentation.groupchat.GroupChatDetailScreen
import com.pdm0126.tutorconnectproyect.presentation.groupchat.GroupChatsListScreen
import com.pdm0126.tutorconnectproyect.presentation.login.LoginScreen
import com.pdm0126.tutorconnectproyect.presentation.messages.MessagesScreen
import com.pdm0126.tutorconnectproyect.presentation.post.CreatePostScreen
import com.pdm0126.tutorconnectproyect.presentation.postdetail.PostDetailScreen
import com.pdm0126.tutorconnectproyect.presentation.profile.ProfileScreen
import com.pdm0126.tutorconnectproyect.presentation.tutor_detail.TutorDetailScreen
import com.pdm0126.tutorconnectproyect.presentation.tutor_home.TutorDashboardScreen
import com.pdm0126.tutorconnectproyect.presentation.tutors.TutorsScreen

@Composable
fun appEntryProvider(navigator: AppNavigator): (NavKey) -> NavEntry<NavKey> =
    entryProvider {

        entry<AppDestinations.Login> {
            LoginScreen(
                onLoginSuccess = { isTutor ->
                    navigator.resetTo(
                        if (isTutor) AppDestinations.TutorDashboard
                        else AppDestinations.Dashboard
                    )
                },
                viewModel = hiltViewModel(),
            )
        }

        // Dashboard del TUTORADO
        entry<AppDestinations.Dashboard> {
            DashboardScreen(
                onNavigate = { navigator.switchTab(it) },
                onOpenMessages = { navigator.navigateTo(AppDestinations.Messages) },
                onOpenComments = { id, title, author ->
                    navigator.navigateTo(AppDestinations.PostDetail(id, title, author))
                },
                onSwitchToTutorView = { navigator.resetTo(AppDestinations.TutorDashboard) },
                onLogout = { navigator.resetTo(AppDestinations.Login) },
                viewModel = hiltViewModel(),
            )
        }

        // Dashboard del TUTOR
        entry<AppDestinations.TutorDashboard> {
            TutorDashboardScreen(
                onOpenCalendar = { navigator.switchTab(AppDestinations.Calendar(isTutor = true)) },
                onOpenCreatePost = { navigator.switchTab(AppDestinations.CreatePost) },
                onSwitchToStudentView = { navigator.resetTo(AppDestinations.Dashboard) },
                onNavigate = { navigator.switchTab(it) },
                onOpenMessages = { navigator.navigateTo(AppDestinations.Messages) },
                onLogout = { navigator.resetTo(AppDestinations.Login) },
                viewModel = hiltViewModel(),
            )
        }

        entry<AppDestinations.Tutors> {
            TutorsScreen(
                onNavigate = { navigator.switchTab(it) },
                onTutorClick = { id -> navigator.navigateTo(AppDestinations.TutorDetail(id)) },
                viewModel = hiltViewModel(),
            )
        }

        entry<AppDestinations.TutorDetail> { key ->
            TutorDetailScreen(
                tutorId = key.tutorId,
                onBack = navigator::pop,
                onOpenChat = { id, name -> navigator.navigateTo(AppDestinations.Chat(id, name)) },
                onBook = { id, name -> navigator.navigateTo(AppDestinations.Booking(id, name)) },
                viewModel = hiltViewModel(),
            )
        }

        entry<AppDestinations.Chat> { key ->
            ChatScreen(
                tutorId = key.tutorId,
                tutorName = key.tutorName,
                onBack = navigator::pop,
                viewModel = hiltViewModel(),
            )
        }

        entry<AppDestinations.Calendar> { key ->
            CalendarScreen(
                isTutor = key.isTutor,
                onNavigate = { navigator.switchTab(it) },
                viewModel = hiltViewModel()
            )
        }

        entry<AppDestinations.GroupChats> { key ->
            GroupChatsListScreen(
                isTutor = key.isTutor,
                onNavigate = { navigator.switchTab(it) },
                onOpenChat = { id, name -> navigator.navigateTo(AppDestinations.GroupChat(id, name)) },
                viewModel = hiltViewModel(),
            )
        }

        entry<AppDestinations.GroupChat> { key ->
            GroupChatDetailScreen(
                groupChatId = key.groupChatId,
                groupChatName = key.groupChatName,
                onBack = navigator::pop,
            )
        }

        entry<AppDestinations.Messages> {
            MessagesScreen(
                onBack = navigator::pop,
                onOpenChat = { userId, userName ->
                    navigator.navigateTo(AppDestinations.Chat(userId, userName))
                },
                viewModel = hiltViewModel(),
            )
        }

        entry<AppDestinations.PostDetail> { key ->
            PostDetailScreen(
                postId = key.postId,
                postTitle = key.postTitle,
                authorName = key.authorName,
                onBack = navigator::pop,
            )
        }

        entry<AppDestinations.Booking> { key ->
            BookingScreen(
                tutorId = key.tutorId,
                tutorName = key.tutorName,
                onBack = navigator::pop,
                onBooked = navigator::pop,
                viewModel = hiltViewModel(),
            )
        }

        entry<AppDestinations.CreatePost> {
            CreatePostScreen(
                onPublished = { navigator.switchTab(AppDestinations.TutorDashboard) },
                onNavigate = { navigator.switchTab(it) },
                viewModel = hiltViewModel(),
            )
        }

        entry<AppDestinations.Profile> {
            ProfileScreen(
                onLogout = { navigator.resetTo(AppDestinations.Login) },
                viewModel = hiltViewModel(),
            )
        }
    }
