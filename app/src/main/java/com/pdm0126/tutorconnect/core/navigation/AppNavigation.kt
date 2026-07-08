package com.pdm0126.tutorconnectproyect.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay

@Composable
fun AppNavigation() {
    // Inicializamos el stack de navegación en el Login usando AppDestinations
    val backStack: NavBackStack<NavKey> = rememberNavBackStack(AppDestinations.Login)
    val navigator = AppNavigator(backStack)

    NavDisplay(
        backStack = backStack,
        entryProvider = appEntryProvider(navigator)
    )
}
