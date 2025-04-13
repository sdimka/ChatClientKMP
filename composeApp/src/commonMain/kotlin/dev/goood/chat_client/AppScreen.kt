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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.goood.chat_client.ui.chatScreen.ChatScreen
import dev.goood.chat_client.ui.LoginScreen
import dev.goood.chat_client.ui.MainScreen
import dev.goood.chat_client.ui.systemMessages.SystemMessagesScreen
import dev.goood.chat_client.ui.SettingsScreen
import dev.goood.chat_client.ui.systemMessages.SystemMessageDetailScreen
import org.koin.compose.viewmodel.koinViewModel


sealed class Screen(val route: String, val title: String, val icon:  ImageVector? = null) {
    data object Login: Screen("login_screen", "Login")
    data object Main: Screen("main_screen", "Main", Icons.AutoMirrored.Filled.List)
    data object ChatDetail: Screen("chat_screen", "Chat", Icons.AutoMirrored.Filled.Send)
    data object SystemMessages: Screen("sys_mess_screen", "System Messages",Icons.AutoMirrored.Filled.Send)
    data object SysMessagesDetail: Screen("sys_mess_detail", "System Message Edit", Icons.AutoMirrored.Filled.List )
    data object Settings: Screen("settings_screen", "Settings", Icons.AutoMirrored.Filled.ArrowForward )

    companion object {
        fun fromRoute(route: String): Screen {
            return when (route.substringBefore("/")) { // Extract base route
                "login_screen" -> Login
                "main_screen" -> Main
                "chat_screen" -> ChatDetail // Note: for navigation, you'll still need to provide the {chatID}
                "sys_mess_screen" -> SystemMessages
                "sys_mess_detail" -> SysMessagesDetail
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
            containerColor = Color.White
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
        Screen.ChatDetail -> {
            bottomBarState.value = false
        }
        Screen.SystemMessages -> {
            bottomBarState.value = true
        }
        Screen.SysMessagesDetail -> {
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
            startDestination =  if (authState is AppViewModel.AuthState.Authorized) Screen.Main.route else Screen.Login.route,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            composable(route = Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                    navController.navigate(Screen.Main.route) { popUpTo(0) }
                },
                    snackBarHostState = snackBarHostState)
            }

            composable(route = Screen.Main.route) {
                MainScreen(
                    toChat = { chat ->
                        viewModel.setCurrentChat(chat)
                        navController.navigate(Screen.ChatDetail.route + "/${chat.id}") {
                            popUpTo(navController.graph.findStartDestination().id)
                        }
                    },
                    snackBarHostState = snackBarHostState
                )
            }

            composable(
                route = Screen.ChatDetail.route + "/{chatID}",
                arguments = listOf(navArgument("chatID") { type = NavType.IntType })
            ){
                stackEntry ->
                    val chatID = stackEntry.arguments?.getInt("chatID")
                    ChatScreen(
                        chatID,
                        snackBarHostState = snackBarHostState
                    )
            }

            composable(route = Screen.SystemMessages.route) {
                SystemMessagesScreen(
                    toDetail = { messID: Int ->
                        navController.navigate(Screen.SysMessagesDetail.route + "/${messID}") {
                            popUpTo(navController.graph.findStartDestination().id)
                        }
                    },
                    snackBarHostState = snackBarHostState
                )
            }

            composable(
                route = Screen.SysMessagesDetail.route + "/{sysMessID}",
                arguments = listOf(navArgument("sysMessID") { type = NavType.IntType})
            ){
                stackEntry ->
                    val messID = stackEntry.arguments?.getInt("sysMessID")
                SystemMessageDetailScreen(
                    messID,
                    snackBarHostState = snackBarHostState
                )
            }
            composable(route = Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavController, bottomBarState: MutableState<Boolean>) {
    val items = listOf(
        Screen.Main,
        Screen.SystemMessages,
        Screen.Settings
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
                            item.icon?.let {
                                Icon(
                                    imageVector = it,
                                    contentDescription = item.title
                                )
                            }
                        },
                        label = { Text(text = item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
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