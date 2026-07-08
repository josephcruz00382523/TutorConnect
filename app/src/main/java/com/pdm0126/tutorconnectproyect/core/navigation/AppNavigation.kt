package com.pdm0126.tutorconnectproyect.core.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
        onBack = { backStack.removeLastOrNull() },
        entryProvider = appEntryProvider(navigator),
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(500)
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(500)
            )
        },
        popTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(500)
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(500)
            )
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(250)
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(250)
            )
        }
    )
}
