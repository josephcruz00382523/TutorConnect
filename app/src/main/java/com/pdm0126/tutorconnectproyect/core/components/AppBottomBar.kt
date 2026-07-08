package com.pdm0126.tutorconnectproyect.core.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.pdm0126.tutorconnectproyect.core.navigation.AppDestinations

@Composable
fun AppBottomBar(
    isTutor: Boolean,
    current: AppDestinations,
    onNavigate: (AppDestinations) -> Unit,
) {
    NavigationBar {
        if (isTutor) {
            NavigationBarItem(
                selected = current is AppDestinations.TutorDashboard,
                onClick = { onNavigate(AppDestinations.TutorDashboard) },
                icon = { Icon(Icons.Filled.Home, "Inicio") },
                label = { Text("Inicio") },
            )
            NavigationBarItem(
                selected = current is AppDestinations.GroupChats,
                onClick = { onNavigate(AppDestinations.GroupChats(isTutor = true)) },
                icon = { Icon(Icons.Filled.Groups, "Chat General") },
                label = { Text("Chat General") },
            )
            NavigationBarItem(
                selected = current is AppDestinations.CreatePost,
                onClick = { onNavigate(AppDestinations.CreatePost) },
                icon = { Icon(Icons.Filled.AddCircle, "Agregar") },
                label = { Text("Agregar") },
            )
            NavigationBarItem(
                selected = current is AppDestinations.Calendar,
                onClick = { onNavigate(AppDestinations.Calendar(isTutor = true)) },
                icon = { Icon(Icons.Filled.CalendarMonth, "Calendario") },
                label = { Text("Calendario") },
            )
        } else {
            NavigationBarItem(
                selected = current is AppDestinations.Dashboard,
                onClick = { onNavigate(AppDestinations.Dashboard) },
                icon = { Icon(Icons.Filled.Home, "Inicio") },
                label = { Text("Inicio") },
            )
            NavigationBarItem(
                selected = current is AppDestinations.Tutors,
                onClick = { onNavigate(AppDestinations.Tutors) },
                icon = { Icon(Icons.Filled.School, "Tutores") },
                label = { Text("Tutores") },
            )
            NavigationBarItem(
                selected = current is AppDestinations.Calendar,
                onClick = { onNavigate(AppDestinations.Calendar(isTutor = false)) },
                icon = { Icon(Icons.Filled.CalendarMonth, "Calendario") },
                label = { Text("Calendario") },
            )
            NavigationBarItem(
                selected = current is AppDestinations.GroupChats,
                onClick = { onNavigate(AppDestinations.GroupChats(isTutor = false)) },
                icon = { Icon(Icons.Filled.Forum, "Chats") },
                label = { Text("Chats") },
            )
        }
    }
}
