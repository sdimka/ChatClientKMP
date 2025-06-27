package dev.goood.chat_client.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.HeadsetSolid
import compose.icons.lineawesomeicons.HourglassEndSolid
import compose.icons.lineawesomeicons.ListSolid
import dev.goood.chat_client.NavigationRoute
import dev.goood.chat_client.ui.chatScreen.ChatScreen
import dev.goood.chat_client.ui.systemMessages.SystemMessageDetailScreen
import dev.goood.chat_client.ui.systemMessages.SystemMessagesScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(
    onGoToAuth: (
        route: Any,
        navBackStackEntry: NavBackStackEntry,
            ) -> Unit,
) {
    val items = listOf(
        BottomRouteElement(
            "Chats",
            LineAwesomeIcons.ListSolid,
            NavigationRoute.MainGraph
        ),
        BottomRouteElement(
            "SysMessages",
            LineAwesomeIcons.HourglassEndSolid,
            NavigationRoute.SystemMessagesGraph
        ),
        BottomRouteElement(
            "Translate",
            LineAwesomeIcons.HeadsetSolid,
            NavigationRoute.SettingsRoute
        )
    )
    val nestedNavController = rememberNavController()
    val navBackStackEntry: NavBackStackEntry? by nestedNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("TopAppbar")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(56.dp),
                tonalElevation = 4.dp
            ) {
                items.forEachIndexed { index, item ->

                    val selected =
                        currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true

                    NavigationBarItem(
                        selected = selected,
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            nestedNavController.navigate(route = item.route) {
                                launchSingleTop = true

                                // If restoreState = true and saveState = true are commented
                                // routes other than Home1 are not saved
                                restoreState = true

                                // Pop up backstack to the first destination and save state.

                                // Then restore any previous back stack state associated with
                                // the item.route destination.
                                // Finally navigate to the item.route destination.
                                popUpTo(nestedNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues: PaddingValues ->

        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = nestedNavController,
            startDestination = NavigationRoute.MainGraph
        ) {
            addMainNavigationGraph(
                nestedNavController = nestedNavController,
                snackBarHostState = snackBarHostState,
                onGoToAuthScreen = { route, navBackStackEntry ->
                    onGoToAuth(route, navBackStackEntry)
                },
                onBottomScreenClick = { route, navBackStackEntry ->
                    nestedNavController.navigate(route)
                }
            )
        }
    }
}


private fun NavGraphBuilder.addMainNavigationGraph(
    nestedNavController: NavHostController,
    snackBarHostState: SnackbarHostState,
    onGoToAuthScreen: (route: Any, navBackStackEntry: NavBackStackEntry) -> Unit,
    onBottomScreenClick: (route: Any, navBackStackEntry: NavBackStackEntry) -> Unit,
) {

    navigation<NavigationRoute.MainGraph>(
        startDestination = NavigationRoute.ChatListRoute
    ) {
        composable<NavigationRoute.ChatListRoute> { from: NavBackStackEntry ->
            ChatListScreen(
                toChat = { chat ->
//                    viewModel.setCurrentChat(chat)
                    nestedNavController.navigate(NavigationRoute.ChatDetailRoute(chat.id)) {
//                        popUpTo(nestedNavController.graph.findStartDestination())// navController.graph.findStartDestination())
                    }
                },
                snackBarHostState = snackBarHostState
            )
        }

        composable<NavigationRoute.ChatDetailRoute> { from: NavBackStackEntry ->
            val chatId = from.toRoute<NavigationRoute.ChatDetailRoute>().chatID
            ChatScreen(
                chatId,
                snackBarHostState = snackBarHostState
            )
        }

    }

    navigation<NavigationRoute.SystemMessagesGraph>(
        startDestination = NavigationRoute.SystemMessagesRoute
    ) {
        composable<NavigationRoute.SystemMessagesRoute> { from: NavBackStackEntry ->
            SystemMessagesScreen(
                toDetail = { messID: Int ->
                    nestedNavController.navigate(NavigationRoute.SystemMessageDetailRoute(messID))
                },
                toNew = {
//                        navController.navigate(Screen.SysMessagesDetail(-1))
                },
                snackBarHostState = snackBarHostState
            )
        }

        composable<NavigationRoute.SystemMessageDetailRoute> { from: NavBackStackEntry ->
            val messID = from.toRoute<NavigationRoute.SystemMessageDetailRoute>().sysMessageID
            SystemMessageDetailScreen(
                messID = messID,
                snackBarHostState = snackBarHostState
            )
        }

    }

    composable<NavigationRoute.SettingsRoute> { from: NavBackStackEntry ->
        SettingsScreen()
    }

}

data class BottomRouteElement(
    val title: String,
    val icon: ImageVector,
    val route: NavigationRoute,
)