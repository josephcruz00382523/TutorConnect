package com.pdm0126.tutorconnectproyect.presentation.groupchat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.pdm0126.tutorconnectproyect.core.components.AppBottomBar
import com.pdm0126.tutorconnectproyect.core.navigation.AppDestinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupChatsScreen(isTutor: Boolean, onNavigate: (AppDestinations) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Chats por Materia") })
        },
        bottomBar = {
            AppBottomBar(
                isTutor = isTutor,
                current = AppDestinations.GroupChats(isTutor),
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Próximamente: chats grupales por materia.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
