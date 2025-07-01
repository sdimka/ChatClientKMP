package dev.goood.chat_client.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import compose.icons.lineawesomeicons.ArrowLeftSolid
import compose.icons.lineawesomeicons.CogSolid
import compose.icons.lineawesomeicons.HeadsetSolid
import compose.icons.lineawesomeicons.HourglassEndSolid
import compose.icons.lineawesomeicons.ListSolid
import dev.goood.chat_client.NavigationRoute
import dev.goood.chat_client.ui.chatScreen.ChatScreen
import dev.goood.chat_client.ui.systemMessages.SystemMessageDetailScreen
import dev.goood.chat_client.ui.systemMessages.SystemMessagesScreen
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import dev.goood.chat_client.ui.platformComposable.PlatformWindowSize


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
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
            NavigationRoute.TranslateRoute
        )
    )

    val nestedNavController = rememberNavController()
    val navBackStackEntry: NavBackStackEntry? by nestedNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var topBarTitle by rememberSaveable { mutableStateOf("PolyChat") }

    val onSetTitle: (String) -> Unit = { newTitle ->
        topBarTitle = newTitle
    }

    LaunchedEffect(currentDestination) {
        val newTitle = when {
            currentDestination?.hierarchy?.any { it.hasRoute(NavigationRoute.MainGraph::class) } == true -> "Chats"
            currentDestination?.hierarchy?.any { it.hasRoute(NavigationRoute.SystemMessagesGraph::class) } == true -> "System Messages"
            currentDestination?.hasRoute(NavigationRoute.TranslateRoute::class) == true -> "Translate"
            currentDestination?.hasRoute(NavigationRoute.SettingsRoute::class) == true -> "Settings"
            else -> "PolyChat" // Fallback if no specific screen sets it
        }
        // This ensures a base title is set when switching bottom tabs,
        // detail screens can then override it.
        if (topBarTitle != newTitle &&
            !(currentDestination?.hasRoute(NavigationRoute.ChatDetailRoute::class) == true ||
                    currentDestination?.hasRoute(NavigationRoute.SystemMessageDetailRoute::class) == true)) {
            // Avoid resetting if a detail screen is active and might have set its own title
            topBarTitle = newTitle
        }
    }

    val snackBarHostState = remember { SnackbarHostState() }

    val navButtonEnabled = nestedNavController.previousBackStackEntry != null

    val windowSizeClass = PlatformWindowSize()
    val useNavRail = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium

//    val navigator = rememberSupportingPaneScaffoldNavigator<Nothing>()
//    SupportingPaneScaffold(
//        directive = navigator.scaffoldDirective,
//        value = navigator.scaffoldValue,
//        mainPane = {  },
//        supportingPane = {  },
//        extraPane = {  },
//    )


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(topBarTitle)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                navigationIcon = {
                    if (navButtonEnabled) {
                        IconButton(onClick = { nestedNavController.popBackStack() }) {
                            Icon(
                                imageVector = LineAwesomeIcons.ArrowLeftSolid,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            nestedNavController.navigate(route = NavigationRoute.SettingsRoute) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(nestedNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                            }
                        }
                    ){
                        Icon(
                            imageVector = LineAwesomeIcons.CogSolid,
                            contentDescription = "Close",
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (!useNavRail) {
                NavigationBar(
                    modifier = Modifier.height(56.dp),
                    tonalElevation = 4.dp
                ) {
                    items.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                nestedNavController.navigate(route = item.route) {
                                    popUpTo(nestedNavController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) }
                        )
                    }
                }
            }
        },
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = {},
//                modifier = Modifier.padding(16.dp),
////            shape = TODO(),
////            containerColor = TODO(),
////            contentColor = TODO(),
////            elevation = TODO(),
////            interactionSource = TODO(),
////            content = TODO(),
//            ){}
//        }
    ) { paddingValues: PaddingValues ->

        Row(Modifier.fillMaxSize()) { // Use Row for NavRail + Content
            if (useNavRail) { // Show NavRail if useNavRail is true AND it's allowed
                NavigationRail(
                    modifier = Modifier.fillMaxHeight()
                        .padding(top = paddingValues.calculateTopPadding()), // Apply top padding from Scaffold
                    header = {
                        // Optional: Add a header to the NavRail, like an app icon or a FAB-like action
                        // Example:
                        // IconButton(onClick = { /* Action */ }) {
                        //     Icon(Icons.Filled.Add, contentDescription = "Add")
                        // }
                    }
                ) {
                    items.forEach { item ->
                        val selected =
                            currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
                        NavigationRailItem(
                            selected = selected,
                            onClick = {
                                nestedNavController.navigate(route = item.route) {
                                    popUpTo(nestedNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            alwaysShowLabel = false // Common for NavRail to only show labels for selected items or on hover
                        )
                    }
                }
            }


            NavHost(
                modifier = Modifier
                    .weight(1f) // Ensures NavHost takes remaining width
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = if (useNavRail) paddingValues.calculateBottomPadding() else 0.dp // Only apply bottom scaffold padding if no bottom bar is there
                    ),
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
                    },
                    onSetTitle = onSetTitle
                )
            }
        }
    }
}


private fun NavGraphBuilder.addMainNavigationGraph(
    nestedNavController: NavHostController,
    snackBarHostState: SnackbarHostState,
    onGoToAuthScreen: (route: Any, navBackStackEntry: NavBackStackEntry) -> Unit,
    onBottomScreenClick: (route: Any, navBackStackEntry: NavBackStackEntry) -> Unit,
    onSetTitle: (String) -> Unit
) {

    navigation<NavigationRoute.MainGraph>(
        startDestination = NavigationRoute.ChatListRoute
    ) {
        composable<NavigationRoute.ChatListRoute> { from: NavBackStackEntry ->
            ChatListScreen(
                toChat = { chat ->
                    onSetTitle(chat.name)
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
                    nestedNavController.navigate(NavigationRoute.SystemMessageDetailRoute(-1))
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

    composable<NavigationRoute.TranslateRoute> { from: NavBackStackEntry ->
        TranslateScreen()
    }

    composable<NavigationRoute.SettingsRoute> { from: NavBackStackEntry ->
        SettingsScreen(
            onLogout = {
                onGoToAuthScreen(NavigationRoute.AuthGraph, from)
            }
        )
    }

}

data class BottomRouteElement(
    val title: String,
    val icon: ImageVector,
    val route: NavigationRoute,
)