package com.dorent.app.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dorent.app.ui.feature.chat.ChatListScreen
import com.dorent.app.ui.feature.chat.ChatThreadScreen
import com.dorent.app.ui.feature.checkout.CheckoutScreen
import com.dorent.app.ui.feature.detail.DetailScreen
import com.dorent.app.ui.feature.home.HomeScreen
import com.dorent.app.ui.feature.listing.NewlyAddedScreen
import com.dorent.app.ui.feature.notifications.NotificationsScreen
import com.dorent.app.ui.feature.auth.LoginScreen
import com.dorent.app.ui.feature.auth.SignupScreen
import com.dorent.app.ui.feature.onboarding.SplashScreen
import com.dorent.app.ui.feature.profile.ProfileScreen
import com.dorent.app.ui.feature.wishlist.WishlistScreen

private val EaseInSine  = CubicBezierEasing(0.12f, 0f, 0.39f, 0f)
private val EaseInQuint = CubicBezierEasing(0.64f, 0f, 0.78f, 0f)

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
            )
        }
        composable(
            route = Screen.Login.route,
            popEnterTransition = {
                slideInHorizontally(
                    animationSpec = tween(420, easing = EaseInQuint),
                    initialOffsetX = { -it },
                )
            },
        ) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
            )
        }
        composable(
            route = Screen.Register.route,
            enterTransition = {
                slideInHorizontally(
                    animationSpec = tween(420, easing = EaseInSine),
                    initialOffsetX = { it },
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(420, easing = EaseInQuint),
                    targetOffsetX = { it },
                )
            },
        ) {
            SignupScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToSignIn = {
                    navController.popBackStack()
                },
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToDetail = { id -> navController.navigate(Screen.Detail.createRoute(id)) },
                onNavigateToNewlyAdded = { navController.navigate(Screen.NewlyAdded.route) },
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavigateToTab = { tab -> navController.navigate(tab) { popUpTo(Screen.Home.route) { inclusive = false } } },
            )
        }
        composable(Screen.NewlyAdded.route) {
            NewlyAddedScreen(
                onBack = { navController.popBackStack() },
                onNavigateToDetail = { id -> navController.navigate(Screen.Detail.createRoute(id)) },
            )
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("propertyId") ?: return@composable
            DetailScreen(
                propertyId = id,
                onBack = { navController.popBackStack() },
                onNavigateToCheckout = { navController.navigate(Screen.Checkout.createRoute(id)) },
                onNavigateToChatThread = { navController.navigate(Screen.ChatThread.createRoute(id)) },
            )
        }
        composable(
            route = Screen.Checkout.route,
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("propertyId") ?: return@composable
            CheckoutScreen(
                propertyId = id,
                onBack = { navController.popBackStack() },
                onBookingConfirmed = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onMessageOwner = { navController.navigate(Screen.ChatThread.createRoute(id)) },
            )
        }
        composable(Screen.Wishlist.route) {
            WishlistScreen(
                onNavigateToDetail = { id -> navController.navigate(Screen.Detail.createRoute(id)) },
                onNavigateToTab = { tab -> navController.navigate(tab) { popUpTo(Screen.Home.route) { inclusive = false } } },
            )
        }
        composable(Screen.Chat.route) {
            ChatListScreen(
                onNavigateToChatThread = { id -> navController.navigate(Screen.ChatThread.createRoute(id)) },
                onNavigateToTab = { tab -> navController.navigate(tab) { popUpTo(Screen.Home.route) { inclusive = false } } },
            )
        }
        composable(
            route = Screen.ChatThread.route,
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("propertyId") ?: return@composable
            ChatThreadScreen(
                propertyId = id,
                onBack = { navController.popBackStack() },
                onNavigateToDetail = { navController.navigate(Screen.Detail.createRoute(id)) },
            )
        }
        composable(Screen.Notifications.route) {
            NotificationsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToDetail = { id -> navController.navigate(Screen.Detail.createRoute(id)) },
                onNavigateToChatThread = { id -> navController.navigate(Screen.ChatThread.createRoute(id)) },
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                onLogout = {
                    navController.navigate(Screen.Splash.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToTab = { tab -> navController.navigate(tab) { popUpTo(Screen.Home.route) { inclusive = false } } },
            )
        }
    }
}
