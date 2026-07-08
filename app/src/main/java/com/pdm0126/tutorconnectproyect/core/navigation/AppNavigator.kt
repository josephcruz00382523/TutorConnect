package com.pdm0126.tutorconnectproyect.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey


class AppNavigator(val backStack: NavBackStack<NavKey>) {


    fun navigateTo(destination: AppDestinations) {
        backStack.add(destination)
    }

    fun switchTab(destination: AppDestinations) {
        if (backStack.lastOrNull() == destination) return
        backStack.clear()
        backStack.add(destination)
    }


    fun resetTo(destination: AppDestinations) {
        backStack.clear()
        backStack.add(destination)
    }


    fun pop() {
        if (backStack.size > 1) backStack.removeLastOrNull()
    }
}
