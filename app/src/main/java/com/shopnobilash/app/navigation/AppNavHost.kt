package com.shopnobilash.app.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shopnobilash.app.presentation.auth.ui.LoginScreen
import com.shopnobilash.app.presentation.auth.ui.RegisterScreen
import com.shopnobilash.app.presentation.auth.ui.VerifyEmailScreen
import com.shopnobilash.app.presentation.chat.ui.ChatListScreen
import com.shopnobilash.app.presentation.chat.ui.ChatThreadScreen
import com.shopnobilash.app.presentation.checkout.ui.CheckoutScreen
import com.shopnobilash.app.presentation.detail.ui.DetailScreen
import com.shopnobilash.app.presentation.home.ui.HomeScreen
import com.shopnobilash.app.presentation.listing.ui.NewlyAddedScreen
import com.shopnobilash.app.presentation.notifications.ui.NotificationsScreen
import com.shopnobilash.app.presentation.onboarding.ui.SplashScreen
import com.shopnobilash.app.data.property.model.PropertyCategory
import com.shopnobilash.app.presentation.search.ui.SearchScreen
import com.shopnobilash.app.presentation.owner.ui.OwnerDashboardScreen
import com.shopnobilash.app.presentation.profile.ui.ProfileScreen
import com.shopnobilash.app.presentation.profile.viewmodel.ProfileViewModel
import com.shopnobilash.app.presentation.profile_setup.ui.ProfileSetupScreen
import com.shopnobilash.app.presentation.verification.ui.VerificationScreen
import com.shopnobilash.app.presentation.wishlist.ui.WishlistScreen
import org.koin.androidx.compose.koinViewModel

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
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToProfileSetup = {
                    navController.navigate(Screen.ProfileSetup.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
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
                    navController.navigate(Screen.ProfileSetup.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
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
            RegisterScreen(
                onNavigateToVerify = { userId, email ->
                    navController.navigate(Screen.VerifyEmail.createRoute(userId, email)) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToSignIn = {
                    navController.popBackStack()
                },
            )
        }
        composable(
            route = Screen.VerifyEmail.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType; nullable = true; defaultValue = null },
            ),
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId").orEmpty()
            val email = backStackEntry.arguments?.getString("email").orEmpty()
            VerifyEmailScreen(
                userId = userId,
                email = email,
                onNavigateToHome = {
                    navController.navigate(Screen.ProfileSetup.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
        composable(Screen.ProfileSetup.route) {
            ProfileSetupScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onSessionExpired = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToDetail = { id -> navController.navigate(Screen.Detail.createRoute(id)) },
                onNavigateToNewlyAdded = { navController.navigate(Screen.NewlyAdded.route) },
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavigateToSearch = { category -> navController.navigate(Screen.Search.createRoute(category)) },
                onNavigateToTab = { tab -> navController.navigate(tab) { popUpTo(Screen.Home.route) { inclusive = false } } },
            )
        }
        composable(
            route = Screen.Search.route,
            arguments = listOf(
                navArgument("category") { type = NavType.StringType; nullable = true; defaultValue = null },
            ),
        ) { backStackEntry ->
            val rawCategory = backStackEntry.arguments?.getString("category")
            SearchScreen(
                onBack = { navController.popBackStack() },
                onNavigateToDetail = { id -> navController.navigate(Screen.Detail.createRoute(id)) },
                initialCategory = PropertyCategory.fromRaw(rawCategory),
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
                onNavigateToVerification = { navController.navigate(Screen.VerifyIdentity.route) },
            )
        }
        composable(Screen.Profile.route) {
            val viewModel: ProfileViewModel = koinViewModel()
            val loggedOut by viewModel.loggedOut.collectAsStateWithLifecycle()

            LaunchedEffect(loggedOut) {
                if (loggedOut) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            ProfileScreen(
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavigateToVerification = { navController.navigate(Screen.VerifyIdentity.route) },
                onNavigateToOwnerDashboard = {
                    navController.navigate(Screen.OwnerDashboard.route) { launchSingleTop = true }
                },
                onLogout = { viewModel.logout() },
                onNavigateToTab = { tab -> navController.navigate(tab) { popUpTo(Screen.Home.route) { inclusive = false } } },
            )
        }
        composable(Screen.VerifyIdentity.route) {
            VerificationScreen(
                onBack = { navController.popBackStack() },
            )
        }
        composable(Screen.OwnerDashboard.route) {
            OwnerDashboardScreen(
                onClose = { navController.popBackStack() },
                onReviewProfile = {
                    navController.navigate(Screen.VerifyIdentity.route)
                },
                onSessionExpired = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onViewListing = { id ->
                    navController.navigate(Screen.Detail.createRoute(id)) {
                        popUpTo(Screen.OwnerDashboard.route) { inclusive = true }
                    }
                },
            )
        }
    }
}
