package dev.goood.chat_client

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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

//    val backStackEntry by navController.currentBackStackEntryAsState()
//    val snackBarHostState = remember { SnackbarHostState() }
//
//    val bottomBarState = rememberSaveable { (mutableStateOf(true)) }

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
            AuthContainer { route: Any, navBackStackEntry: NavBackStackEntry ->
                // Navigate only when life cycle is resumed for current screen
                if (navBackStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
                    navController.navigate(route = route)
                }
            }
        }

        composable<NavigationRoute.MainGraph> {
            MainContainer { route: Any, navBackStackEntry: NavBackStackEntry ->
                if (navBackStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
                    navController.navigate(route = route)
                }
            }
        }
    }
}