package dev.goood.chat_client.ui.systemMessages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.CogSolid
import compose.icons.lineawesomeicons.PlusSquareSolid
import compose.icons.lineawesomeicons.Sass
import compose.icons.lineawesomeicons.Save
import compose.icons.lineawesomeicons.UndoSolid
import dev.goood.chat_client.services.SystemMessagesService
import dev.goood.chat_client.ui.composable.CButton
import dev.goood.chat_client.ui.composable.buttonBackground
import dev.goood.chat_client.ui.composable.grayBackground
import dev.goood.chat_client.viewModels.SMDetailViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SystemMessageDetailScreen(
    messID: Int?,
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {

    val viewModel = koinViewModel<SMDetailViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val enableState = state is SystemMessagesService.State.Success
    val currentMessage by viewModel.selectedMessage.collectAsStateWithLifecycle()

    LaunchedEffect(LocalLifecycleOwner.current) {
        if (messID != null) {
            viewModel.getCurrentMessage(messID)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .background(grayBackground)
            .padding(all = 10.dp)
    ) {
        if (currentMessage != null) {


            Text(
                text = currentMessage!!.id.toString()
            )
            MNTextFiled(
                value = currentMessage!!.title,
                title = "Title:",
                onValueChange = { viewModel.setTitle(it) },
                enabled = enableState,
                modifier = modifier.padding(bottom = 10.dp)
            )

            MNTextFiled(
                value = currentMessage!!.content,
                title = "Content:",
                onValueChange = { viewModel.setContent(it) },
                enabled = enableState,
                modifier = modifier.padding(bottom = 10.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
            ) {
                CButton(
                    icon = LineAwesomeIcons.Save,
                    onClick = {
                        viewModel.updateMessage()
                    },
                    enabled = enableState
                )

                CButton(
                    icon = LineAwesomeIcons.UndoSolid,
                    onClick = {
                    },
                    enabled = enableState
                )
            }
        }
    }
}

@Composable
fun MNTextFiled(
    value: String,
    title: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text( title ) },
        enabled = enabled,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = buttonBackground,
            unfocusedIndicatorColor = buttonBackground,
            focusedLabelColor = buttonBackground
        ),
        modifier = modifier.fillMaxWidth()
    )
}