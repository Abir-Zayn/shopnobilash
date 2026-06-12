package com.dorent.app.navigation

sealed class Screen(val route: String) {
    object Splash       : Screen("splash")
    object Login        : Screen("login")
    object Home         : Screen("home")
    object NewlyAdded   : Screen("newly_added")
    object Wishlist     : Screen("wishlist")
    object Chat         : Screen("chat")
    object Profile      : Screen("profile")
    object Notifications: Screen("notifications")

    object Detail : Screen("detail/{propertyId}") {
        fun createRoute(id: String) = "detail/$id"
    }
    object Checkout : Screen("checkout/{propertyId}") {
        fun createRoute(id: String) = "checkout/$id"
    }
    object ChatThread : Screen("chat_thread/{propertyId}") {
        fun createRoute(id: String) = "chat_thread/$id"
    }
}
