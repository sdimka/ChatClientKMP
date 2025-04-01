package dev.goood.chat_client

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.goood.chat_client.ui.LoginScreen
import dev.goood.chat_client.ui.MainScreen
import dev.goood.chat_client.ui.SettingsScreen
import org.koin.compose.viewmodel.koinViewModel


sealed class Screen(val route: String, val title: String, val icon:  ImageVector? = null) {
    data object Login: Screen("login_screen", "Login")
    data object Main: Screen("main_screen", "Main", Icons.AutoMirrored.Filled.List)
    data object Other: Screen("other_screen", "Other", Icons.AutoMirrored.Filled.Send)
    data object Settings: Screen("settings_screen", "Settings", Icons.AutoMirrored.Filled.ArrowForward )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    currentScreen: String?,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { currentScreen?.let { Text(it) } },
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
    val currentScreen =  backStackEntry?.destination
    val snackbarHostState = remember { SnackbarHostState() }

    val bottomBarState = rememberSaveable { ( mutableStateOf(true)) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // Control TopBar and BottomBar
    when (navBackStackEntry?.destination?.route) {
        Screen.Login.route -> {
            bottomBarState.value = false
        }
        Screen.Main.route -> {
            bottomBarState.value = true
        }
        Screen.Other.route -> {
            bottomBarState.value = true
        }
        Screen.Settings.route -> {
            bottomBarState.value = false

        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            AppTopBar(
                currentScreen = currentScreen?.route,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
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
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(route = Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                    navController.navigate(Screen.Main.route) { popUpTo(0) }
                },
                    snackbarHostState = snackbarHostState)
            }

            composable(route = Screen.Main.route) {
                MainScreen()
            }

            composable(route = Screen.Other.route) {
                MainScreen()
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
        Screen.Other,
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