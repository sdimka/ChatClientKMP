package dev.goood.chat_client.ui.filesDialog

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.PlusSquareSolid
import compose.icons.lineawesomeicons.QuestionCircle
import compose.icons.lineawesomeicons.TrashAlt
import compose.icons.lineawesomeicons.WindowClose
import dev.goood.chat_client.model.MFile
import dev.goood.chat_client.ui.composable.BallProgerssIndicator
import dev.goood.chat_client.ui.composable.CButton
import dev.goood.chat_client.ui.composable.DeleteDialogImp
import dev.goood.chat_client.ui.composable.SwipeableWithActions
import dev.goood.chat_client.ui.platformComposable.PlatformDragAndDropArea
import dev.goood.chat_client.viewModels.FileDialogViewModel
import dev.goood.chat_client.viewModels.FileDialogViewModel.State
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.size
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.ExperimentalTime


@Composable
fun FilesDialog(
    chatID: Int?,
    selectedFilesList: StateFlow<List<MFile>>,
    selectedFilesListUpdate: (file: MFile, operation: (List<MFile>, MFile) -> List<MFile>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<FileDialogViewModel>() // Don't create a new instance
    val selectedFileName by viewModel.selectedFileName.collectAsState()
    val acceptedExtensions by viewModel.acceptedExtensions.collectAsStateWithLifecycle()
    val uploadsSupported = acceptedExtensions?.isNotEmpty() != false
    val scope = rememberCoroutineScope()

    val state by viewModel.state.collectAsStateWithLifecycle()

    val uploadState = viewModel.uploadState
    val animatedProgress by animateFloatAsState(
        targetValue = uploadState.progress,
        animationSpec = tween(durationMillis = 100),
        label = "File upload progress bar"
    )

//    val selectedFiles = remember { mutableListOf<MFile>() }

    LaunchedEffect(LocalLifecycleOwner.current, chatID) { // Add chatID to LaunchedEffect
        viewModel.setCurrentChat(chatID)
    }

    // Newly uploaded files are attached to the next message right away.
    LaunchedEffect(viewModel) {
        viewModel.uploadedFiles.collect { uploaded ->
            selectedFilesListUpdate(uploaded) { list, f -> if (f in list) list else list + f }
        }
    }

    val isEnabled by remember {
        derivedStateOf {
            !uploadState.isUploading && state is State.Success && uploadsSupported
        }
    }

    fun sendFile() {
        scope.launch {
            // Only offer the file types the chat's provider accepts (all files when unknown).
            val type = acceptedExtensions?.takeIf { it.isNotEmpty() }
                ?.let { FileKitType.File(it) }
                ?: FileKitType.File()
            val nFile = FileKit.openFilePicker(type = type)
            if (nFile != null) {
                viewModel.uploadFile(nFile)
            }
        }
    }

    fun sendFile(file: String) {
        viewModel.uploadFile(PlatformFile(file))
    }

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss,

        ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = modifier.fillMaxSize()
                .background(Color.LightGray)
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
                    selectedFiles = selectedFilesList,
                    selectedFilesUpdate = selectedFilesListUpdate,
                    onRetry = { viewModel.setCurrentChat(chatID) },
                    modifier = modifier
                        .weight(1f)
                        .fillMaxHeight()
                )

                val statusText = when {
                    !uploadsSupported -> "File attachments aren't supported by this chat's provider."
                    uploadState.errorMessage != null -> uploadState.errorMessage
                    else -> selectedFileName ?: ""
                }
                Text(
                    text = statusText,
                    fontSize = 14.sp,
                    color = if (uploadState.errorMessage != null || !uploadsSupported) Color(0xFFB3261E) else Color.Unspecified,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = modifier
                        .heightIn(min = 50.dp)
                        .padding(vertical = 5.dp)
                )

                PlatformDragAndDropArea(
                    modifier = modifier
                        .size(200.dp, 100.dp)
                        .padding(bottom = 5.dp),
                    content = {},
                    onFilesDropped = { files ->
                        files.forEach { file ->
                            sendFile(file)
//                            println("File: $file")
                        }
                    }
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {

                    CButton(
                        text = "New file",
                        icon = LineAwesomeIcons.PlusSquareSolid,
                        enabled = isEnabled,
                        onClick = {
                            sendFile()
                        },
                        modifier = modifier.padding(bottom = 10.dp)
                    )

                }
                if (uploadState.isUploading) {
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .height(16.dp)
                    )
                }

                CButton(
                    text = "Close",
                    icon = LineAwesomeIcons.WindowClose,
                    onClick = {
                        onDismiss()
                              },
                    modifier = modifier.padding(bottom = 10.dp)
                )
            }
        }
    }

    val deleteDialogState by viewModel.deleteFileDialogState.collectAsState()
    deleteDialogState?.let { fileToDelete ->
        DeleteDialogImp(
            item = fileToDelete,
            title = "Delete file",
            getItemName = { it.displayName },
            onDismiss = { viewModel.setFileDialogState( null ) },
            onDelete = { file ->
                viewModel.deleteFile(file.id)
                viewModel.setFileDialogState( null )
            }
        )
    }
}

@Composable
fun FileList(
    viewModel: FileDialogViewModel,
    state: State,
    selectedFiles: StateFlow<List<MFile>>,
    selectedFilesUpdate: (file: MFile, operation: (List<MFile>, MFile) -> List<MFile>) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
){
    val fileList by viewModel.fileList.collectAsStateWithLifecycle()

    val selected by selectedFiles.collectAsStateWithLifecycle()

    fun isSelected(file: MFile): Boolean = file in selected

    when (state) {
        is State.Error -> {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(state.message, color = Color.DarkGray, textAlign = TextAlign.Center)
                CButton(text = "Retry", onClick = onRetry)
            }
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
                items(fileList, key = { it.id }) { file ->
                    FileElement(
                        file = file,
                        isSelected = isSelected(file),
                        onSelected = { selected ->
                            if (selected) {
                                selectedFilesUpdate(file) { list, f -> list + f }
                            } else {
                                selectedFilesUpdate(file) { list, f -> list - f }
                            }
                        },
                        onDelete = { chatToDel -> viewModel.setFileDialogState(chatToDel) }
                    )
                }
            }
        }
    }
}

@Composable
fun FileElement(
    file: MFile,
    isSelected: Boolean,
    onSelected: (Boolean) -> Unit = { _ -> },
    onDelete: (MFile) -> Unit = { _ -> },
    modifier: Modifier = Modifier
) {
    val checkedState = remember(isSelected) { mutableStateOf(isSelected) }

    SwipeableWithActions(
        isRevealed = false,
        actions = {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = modifier.padding(end = 8.dp)
            ) {
                IconButton(
                    onClick = {
                        onDelete(file)
                    }
                ) {
                    Icon(
                        imageVector = LineAwesomeIcons.TrashAlt,
                        contentDescription = "Delete file icon",
                        tint = if (checkedState.value) Color.Green else Color.Gray
                    )
                }
            }
        }
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 5.dp)
                .padding(horizontal = 10.dp)
        ) {
            Checkbox(
                checked = checkedState.value,
                onCheckedChange = {
                    checkedState.value = it
                    onSelected(it)
                }
            )

            Column(
                modifier = Modifier
                    .padding(end = 15.dp)
                    .weight(1f)
            ) {
                Text(
                    text = file.displayName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
                val details = listOfNotNull(
                    file.bytes?.let { "${it / 1024} KB" },
                    "Processing…".takeIf { file.isProcessing },
                ).joinToString(" · ")
                if (details.isNotEmpty()) {
                    Text(
                        text = details,
                        fontSize = 10.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Text(
                text = file.createdAt?.let(::dateMillisToString) ?: "",
                fontSize = 8.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .wrapContentWidth(
                        Alignment.End,
                        unbounded = false
                    ) // Shrink to fit content, aligned end
                    .defaultMinSize(minWidth = 50.dp)
            )
        }
    }
}


@OptIn(ExperimentalTime::class)
private fun dateMillisToString(epoch: Long): String {
    return try {
        val instant = Instant.fromEpochSeconds(epoch)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        val formatter = LocalDateTime.Format {
            date(LocalDate.Formats.ISO)
            char(' ')
            time(LocalTime.Formats.ISO)
        }

        dateTime.format(formatter)
    } catch (e: Exception) {
        "Invalid date"
    }
}