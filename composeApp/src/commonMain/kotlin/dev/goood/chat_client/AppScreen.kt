package dev.goood.chat_client

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.goood.chat_client.ui.AuthContainer
import dev.goood.chat_client.ui.MainContainer
import org.koin.compose.viewmodel.koinViewModel



@Composable
fun AppScreen(
    navController: NavHostController = rememberNavController()
) {

    val viewModel: AppViewModel = koinViewModel()
    val authState by viewModel.authState.collectAsState()

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = if (authState is AppViewModel.AuthState.Authorized) NavigationRoute.MainGraph else NavigationRoute.AuthGraph,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(700)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(700)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(700)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(700)
            )
        }
    ) {

        composable<NavigationRoute.AuthGraph> {
            AuthContainer(
                onGoToMainScreen = { route: Any, navBackStackEntry: NavBackStackEntry ->
                    // Check lifecycle to prevent multiple navigations if already navigating
                    if (navBackStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
                        navController.navigate(route = NavigationRoute.MainGraph) {
//                            popUpTo(navController.graph.startDestinationId) { // Pop up to the start destination of the graph
//                                inclusive = true // Also remove the start destination itself
//                            }
                            // Alternatively, if AuthGraph is the root of its own graph and you want to clear it:
                             popUpTo<NavigationRoute.AuthGraph> { inclusive = true }

                            launchSingleTop = true // Avoid multiple copies of the MainGraph if already present
                        }
                    }
                }
            )
        }

        composable<NavigationRoute.MainGraph> {
            MainContainer(
                onGoToAuth = { route: Any, navBackStackEntry: NavBackStackEntry ->
                    if (navBackStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
                        viewModel.logout()
                        navController.navigate(route = NavigationRoute.AuthGraph) {
//                            popUpTo(navController.graph.startDestinationId) {
//                                inclusive = true
//                            }
                            // Alternatively, if MainGraph is the root of its own graph and you want to clear it:
                            popUpTo<NavigationRoute.MainGraph> { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}