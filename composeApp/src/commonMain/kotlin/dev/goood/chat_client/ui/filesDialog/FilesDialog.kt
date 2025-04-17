package dev.goood.chat_client.ui.filesDialog

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.PlusSquareSolid
import compose.icons.lineawesomeicons.WindowClose
import dev.goood.chat_client.core.other.ShareFileModel
import dev.goood.chat_client.model.MFile
import dev.goood.chat_client.ui.composable.BallProgerssIndicator
import dev.goood.chat_client.ui.composable.CButton
import dev.goood.chat_client.viewModels.FileDialogViewModel
import dev.goood.chat_client.viewModels.FileDialogViewModel.State
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun FilesDialog(
    chatID: Int?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<FileDialogViewModel>()
    val file by viewModel.selectedFile.collectAsState()
    val scope = rememberCoroutineScope()

    val state by viewModel.state.collectAsStateWithLifecycle()

    val uploadState = viewModel.uploadState
    val animatedProgress by animateFloatAsState(
        targetValue = uploadState.progress,
        animationSpec = tween(durationMillis = 100),
        label = "File upload progress bar"
    )

    LaunchedEffect(LocalLifecycleOwner.current) {
        viewModel.setCurrentChat(chatID)
    }

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss,

        ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = modifier.fillMaxSize()
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = "File Dialog",
                    fontSize = 25.sp,
                    modifier = modifier
                        .height(50.dp)
                        .padding(vertical = 10.dp)
                )

                FileList(
                    viewModel = viewModel,
                    state = state,
                    modifier = modifier
                        .weight(1f)
                        .fillMaxHeight()
                )

                Text(
                    text = file?.fileName ?: "",
                    fontSize = 14.sp,
                    modifier = modifier
                        .height(50.dp)
                        .padding(vertical = 5.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {

                    CButton(
                        text = "To chat",
                        icon = LineAwesomeIcons.WindowClose,
                        onClick = {},
                        modifier = modifier.padding(bottom = 10.dp)
                    )

                    CButton(
                        text = "New file",
                        icon = LineAwesomeIcons.PlusSquareSolid,
                        onClick = {
                            scope.launch {
                                val nFile = FileKit.openFilePicker()
                                if (nFile != null) {
                                    viewModel.sendFile(
                                        ShareFileModel(
                                            fileName = nFile.name,
                                            bytes = nFile.readBytes(),
                                        )
                                    )
                                }
                            }
                        },
                        modifier = modifier.padding(bottom = 10.dp)
                    )

                }

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(16.dp)
                )

//                Spacer(modifier = modifier.weight(1F))

                CButton(
                    icon = LineAwesomeIcons.WindowClose,
                    onClick = onDismiss,
                    modifier = modifier.padding(bottom = 10.dp)
                )
            }
        }
    }
}

@Composable
fun FileList(
    viewModel: FileDialogViewModel,
    state: State,
    modifier: Modifier = Modifier,
){
    val fileList by viewModel.fileList.collectAsStateWithLifecycle()

    when (state) {
        is State.Error -> {

        }
        State.Loading -> {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BallProgerssIndicator()
            }
        }
        State.Success -> {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(top = 15.dp)
            ) {
                items(fileList) { file ->
                    FileElement(
                        file = file,
                    )
                }
            }
        }
    }
}

@Composable
fun FileElement(
    file: MFile,
    modifier: Modifier = Modifier
){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .padding(horizontal = 10.dp)
    ) {
        Column {
            Text(
                text = file.filename
            )
            Text(
                text = "${file.bytes / 1024} KB",
                fontSize = 10.sp,
                color = Color.DarkGray
            )
        }

        Column {
            Text(
                text = dateMillisToString(file.createdAt),
                fontSize = 10.sp,
                color = Color.DarkGray
            )
        }

    }
}

private fun dateMillisToString(epoch: Long): String {
    val instant = Instant.fromEpochSeconds(epoch)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault()) // Or specify a timezone if needed

    val formatter = LocalDateTime.Format {
        date(LocalDate.Formats.ISO)
        char(' ')
        time(LocalTime.Formats.ISO)
    }
    return dateTime.format(formatter)
}