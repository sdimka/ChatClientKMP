package dev.goood.chat_client

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.ArrowLeftSolid
import compose.icons.lineawesomeicons.HeadsetSolid
import compose.icons.lineawesomeicons.HourglassEndSolid
import compose.icons.lineawesomeicons.ListSolid
import dev.goood.chat_client.ui.chatScreen.ChatScreen
import dev.goood.chat_client.ui.LoginScreen
import dev.goood.chat_client.ui.MainScreen
import dev.goood.chat_client.ui.systemMessages.SystemMessagesScreen
import dev.goood.chat_client.ui.SettingsScreen
import dev.goood.chat_client.ui.systemMessages.SystemMessageDetailScreen
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel


@Serializable
sealed class Screen(val route: String, val title: String) {
    @Serializable
    data object Login: Screen("login_screen", "Login")
    @Serializable
    data object Main: Screen("main_screen", "Main")
    @Serializable
    data class ChatDetail (val chatID: Int): Screen("chat_screen", "Chat")
    @Serializable
    data object SystemMessages: Screen("sys_mess_screen", "System Messages")
    @Serializable
    data class SysMessagesDetail(val sysMessageID: Int): Screen("sys_mess_detail", "System Message Edit")
    @Serializable
    data object Settings: Screen("settings_screen", "Settings")

    companion object {
        fun fromRoute(route: String): Screen {
            return when (route.substringBefore("/")) { // Extract base route
                "login_screen" -> Login
                "main_screen" -> Main
                "chat_screen" -> ChatDetail(0) // Note: for navigation, you'll still need to provide the {chatID}
                "sys_mess_screen" -> SystemMessages
                "sys_mess_detail" -> SysMessagesDetail(0)
                "settings_screen" -> Settings
                else -> {
                    println("Unknown route: $route")
                    Main
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    currentScreen: Screen?,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {

    val viewModel: AppViewModel = koinViewModel()
    val selectedChat by viewModel.selectedChat.collectAsState()

    TopAppBar(
        title = {
            Row {
                currentScreen?.let { Text(it.title) }

                if (selectedChat != null) {
                    Text(
                        text =  "/" + selectedChat!!.name
                    )

                    Text(
                        text = "(${selectedChat!!.model.displayName})",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = Color(0xFFE7ECEF)
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = LineAwesomeIcons.ArrowLeftSolid,
                        contentDescription = "Back"
                    )
                }
            }
        }
    )
}

@Composable
fun AppScreen(
    navController: NavHostController = rememberNavController()
) {

    val viewModel: AppViewModel = koinViewModel()
    val authState by viewModel.authState.collectAsState()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    val bottomBarState = rememberSaveable { ( mutableStateOf(true)) }
    val scrState = backStackEntry?.destination?.route?.let {
        Screen.fromRoute(
            it
        )
    }

    // Control TopBar and BottomBar
    when (scrState) {
        Screen.Login -> {
            bottomBarState.value = false
        }
        Screen.Main -> {
            bottomBarState.value = true
        }
        is Screen.ChatDetail -> {
            bottomBarState.value = false
        }
        Screen.SystemMessages -> {
            bottomBarState.value = true
        }
        is Screen.SysMessagesDetail -> {
            bottomBarState.value = false
        }
        Screen.Settings -> {
            bottomBarState.value = false
        }
        null -> {
            println("Other route: ${backStackEntry?.destination?.route}")
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            AppTopBar(
                currentScreen = scrState,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = {
                    navController.navigateUp()
                    viewModel.setCurrentChat(null)
                }
            )
        },
        bottomBar = {
            BottomBar(navController, bottomBarState )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination =  if (authState is AppViewModel.AuthState.Authorized) Screen.Main else Screen.Login,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            composable<Screen.Login> {
                LoginScreen(
                    onLoginSuccess = {
                    navController.navigate(Screen.Main) { popUpTo(0) }
                },
                    snackBarHostState = snackBarHostState)
            }

            composable<Screen.Main> {
                MainScreen(
                    toChat = { chat ->
                        viewModel.setCurrentChat(chat)
                        navController.navigate(Screen.ChatDetail(chat.id)) {
                            popUpTo(Screen.Main)// navController.graph.findStartDestination())
                        }
                    },
                    snackBarHostState = snackBarHostState
                )
            }

            composable<Screen.ChatDetail> { stackEntry ->
                val chatId = stackEntry.toRoute<Screen.ChatDetail>().chatID
                ChatScreen(
                    chatId,
                    snackBarHostState = snackBarHostState
                    )
            }

            composable<Screen.SystemMessages> {
                SystemMessagesScreen(
                    toDetail = { messID: Int ->
                        navController.navigate(Screen.SysMessagesDetail(messID))
                    },
                    toNew = {
                        navController.navigate(Screen.SysMessagesDetail(-1))
                    },
                    snackBarHostState = snackBarHostState
                )
            }

            composable<Screen.SysMessagesDetail>{
                stackEntry ->
                    val messID = stackEntry.toRoute<Screen.SysMessagesDetail>().sysMessageID
                SystemMessageDetailScreen(
                    messID,
                    snackBarHostState = snackBarHostState
                )
            }

            composable<Screen.Settings> {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavController, bottomBarState: MutableState<Boolean>) {

    data class BarItem(
        val icon: ImageVector,
        val route: Screen
    )

    val items = listOf(
        BarItem(LineAwesomeIcons.ListSolid, Screen.Main),
        BarItem(LineAwesomeIcons.HourglassEndSolid, Screen.SystemMessages),
        BarItem(LineAwesomeIcons.HeadsetSolid, Screen.Settings)
    )

    AnimatedVisibility(
        visible = bottomBarState.value,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        content = {
            NavigationBar(
                containerColor = Color(0xFF75DDDD),
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->

                    NavigationBarItem (
                        icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.route.title
                                )
                        },
                        label = { Text(text = item.route.title) },
                        selected = currentRoute == item.route.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(Screen.Main) {
                                    saveState = true
                                }
//                                popUpTo(navController.graph.findStartDestination()) {
//                                    saveState = true
//                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    )
}