package com.pdm0126.tutorconnectproyect.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * Thin, type-safe wrapper over the Navigation 3 back stack.
 * Replaces the old NavController: navigation is just list manipulation.
 */
class AppNavigator(val backStack: NavBackStack<NavKey>) {

    /** Push a destination on top of the stack. */
    fun navigateTo(destination: AppDestinations) {
        backStack.add(destination)
    }

    /** Switch to a top-level tab: make it the single root so tabs behave as roots. */
    fun switchTab(destination: AppDestinations) {
        if (backStack.lastOrNull() == destination) return
        backStack.clear()
        backStack.add(destination)
    }

    /** Clear everything and set a new single root (used after login/logout). */
    fun resetTo(destination: AppDestinations) {
        backStack.clear()
        backStack.add(destination)
    }

    /** Pop the top, guarded so we never pop the last entry. */
    fun pop() {
        if (backStack.size > 1) backStack.removeLastOrNull()
    }
}
