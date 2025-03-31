package dev.goood.chat_client.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.goood.chat_client.model.User
import dev.goood.chat_client.ui.composable.CButton
import dev.goood.chat_client.viewModels.LoginViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {

    val viewModel: LoginViewModel  = koinViewModel()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val loginState = viewModel.state.collectAsState(initial = null)

    // ToDo: Fix
//    LaunchedEffect(viewModel.state.value) {
//        when (val state = loginState.value) {
//            is LoginViewModel.LoginState.Success -> {
//                isLoading = false // Ensure isLoading is reset
//                onLoginSuccess()
//            }
//            is LoginViewModel.LoginState.Error -> {
//                isLoading = false // Ensure isLoading is reset
//                scope.launch {
//                    snackbarHostState.showSnackbar(state.message)
//                }
//            }
//            LoginViewModel.LoginState.Loading -> isLoading = true
//            null -> Unit // Handle null state appropriately, maybe log or show a default message
//        }
//    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth(),
    ) {
        when (val state = loginState.value) {
        is LoginViewModel.LoginState.Success -> {
            isLoading = false // Ensure isLoading is reset
            onLoginSuccess()
        }
        is LoginViewModel.LoginState.Error -> {
            isLoading = false // Ensure isLoading is reset
            scope.launch {
                snackbarHostState.showSnackbar(state.message)
            }
        }
        LoginViewModel.LoginState.Loading -> isLoading = true
        null -> Unit // Handle null state appropriately, maybe log or show a default message
    }

        Text(
            text = viewModel.getString(),
            modifier = modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") }
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = modifier.padding(top = 16.dp)
            )
        } else {
            CButton(
                text = "Login",
                onClick = {
                    viewModel.auth(
                        User(
                            email = email,
                            password = password
                        )
                    )
                },
                enabled = email.isNotBlank() && password.isNotBlank(),
                modifier = modifier.padding(top = 16.dp),
            )
        }
    }
}