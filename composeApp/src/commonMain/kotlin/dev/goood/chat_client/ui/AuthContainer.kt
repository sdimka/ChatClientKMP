package dev.goood.chat_client.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import dev.goood.chat_client.NavigationRoute


@Composable
fun AuthContainer(
    onGoToMainScreen: (
        route: Any,
        navBackStackEntry: NavBackStackEntry,
    ) -> Unit,
) {

    val nestedNavController = rememberNavController()
    val navBackStackEntry: NavBackStackEntry? by nestedNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val snackBarHostState = remember { SnackbarHostState() }

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = nestedNavController,
        startDestination = NavigationRoute.AuthGraph
    ) {
        addAuthNavigationGraph(
            nestedNavController = nestedNavController,
            snackBarHostState = snackBarHostState,
            onGoToMainScreen = { route, navBackStackEntry ->
                onGoToMainScreen(route, navBackStackEntry)
            },
//            onBottomScreenClick = { route, navBackStackEntry ->
//                nestedNavController.navigate(route)
//            }
        )
    }
}

private fun NavGraphBuilder.addAuthNavigationGraph(
    nestedNavController: NavHostController,
    snackBarHostState: SnackbarHostState,
    onGoToMainScreen: (route: Any, navBackStackEntry: NavBackStackEntry) -> Unit,
) {

    navigation<NavigationRoute.AuthGraph>(
        startDestination = NavigationRoute.LoginRoute
    ) {
        composable<NavigationRoute.LoginRoute> { from: NavBackStackEntry ->
            LoginScreen(
                onLoginSuccess = {
                    onGoToMainScreen(NavigationRoute.MainGraph, from)
                },
                snackBarHostState = snackBarHostState
            )
        }
    }
}