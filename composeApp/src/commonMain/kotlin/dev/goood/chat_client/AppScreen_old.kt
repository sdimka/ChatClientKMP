package dev.goood.chat_client

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.ArrowLeftSolid
import compose.icons.lineawesomeicons.HeadsetSolid
import compose.icons.lineawesomeicons.HourglassEndSolid
import compose.icons.lineawesomeicons.ListSolid
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


//    Scaffold(
//        snackbarHost = {
//            SnackbarHost(hostState = snackBarHostState)
//        },
//        topBar = {
//            AppTopBar(
//                currentScreen = scrState,
//                canNavigateBack = navController.previousBackStackEntry != null,
//                navigateUp = {
//                    navController.popBackStack() //navigateUp()
////                    viewModel.setCurrentChat(null)
//                }
//            )
//        },
//        bottomBar = {
//            BottomBar(navController, bottomBarState )
//        }
//    ) { innerPadding ->
//
//        NavHost(
//            navController = navController,
//            startDestination =  if (authState is AppViewModel.AuthState.Authorized) Screen.Main else Screen.Login,
//            modifier = Modifier
//                .padding(innerPadding)
//                .fillMaxSize()
//        ) {
//            composable<Screen.Login> {
//                LoginScreen(
//                    onLoginSuccess = {
//                    navController.navigate(Screen.Main) { popUpTo(0) }
//                },
//                    snackBarHostState = snackBarHostState)
//            }
//
//            composable<Screen.Main> {
//                ChatListScreen(
//                    toChat = { chat ->
//                        viewModel.setCurrentChat(chat)
//                        navController.navigate(Screen.ChatDetail(chat.id)) {
//                            popUpTo(Screen.Main)// navController.graph.findStartDestination())
//                        }
//                    },
//                    snackBarHostState = snackBarHostState
//                )
//            }
//
//            composable<Screen.ChatDetail> { stackEntry ->
//                val chatId = stackEntry.toRoute<Screen.ChatDetail>().chatID
//                ChatScreen(
//                    chatId,
//                    snackBarHostState = snackBarHostState
//                    )
//            }
//
//            composable<Screen.SystemMessages> {
//                SystemMessagesScreen(
//                    toDetail = { messID: Int ->
//                        navController.navigate(Screen.SysMessagesDetail(messID))
//                    },
//                    toNew = {
//                        navController.navigate(Screen.SysMessagesDetail(-1))
//                    },
//                    snackBarHostState = snackBarHostState
//                )
//            }
//
//            composable<Screen.SysMessagesDetail>{
//                stackEntry ->
//                    val messID = stackEntry.toRoute<Screen.SysMessagesDetail>().sysMessageID
//                SystemMessageDetailScreen(
//                    messID,
//                    snackBarHostState = snackBarHostState
//                )
//            }
//
//            composable<Screen.Settings> {
//                SettingsScreen()
//            }
//        }
//    }
//}

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