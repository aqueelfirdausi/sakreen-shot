package com.sakreenshot.app.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Detail : Screen("detail/{id}") {
        fun createRoute(id: Long) = "detail/$id"
    }
    object Cleanup : Screen("cleanup")
    object Settings : Screen("settings")
}
