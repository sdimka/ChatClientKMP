package dev.goood.chat_client.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.goood.chat_client.model.User
import dev.goood.chat_client.viewModels.LoginViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {

    val viewModel = koinViewModel<LoginViewModel>()
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()


    LaunchedEffect(viewModel.state.value) {
        viewModel.state.collect {
            when (it) {
                is LoginViewModel.LoginState.Success -> {
                    onLoginSuccess()
                }
                is LoginViewModel.LoginState.Error -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(it.message)
                    }
                }
                null -> println("Something went wrong")
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth(),
    ) {

        Text(
            text = viewModel.getString(),
            modifier = modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") }
        )

        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") }
        )

        Button(
            onClick = {
                viewModel.auth(
                    User(
                        email = email.value,
                        password = password.value
                    )
                )
//                onLogin()
            },
            modifier = modifier.padding(top = 16.dp),
        ) {
            Text(text = "Continue")
        }
    }


}