package dev.goood.chat_client.ui.filesDialog

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.PlusSquareSolid
import compose.icons.lineawesomeicons.WindowClose
import dev.goood.chat_client.core.other.ShareFileModel
import dev.goood.chat_client.ui.composable.CButton
import dev.goood.chat_client.viewModels.FileDialogViewModel
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun FilesDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<FileDialogViewModel>()
    val file by viewModel.selectedFile.collectAsState()
    val scope = rememberCoroutineScope()

    val state = viewModel.state
    val animatedProgress by animateFloatAsState(
        targetValue = state.progress,
        animationSpec = tween(durationMillis = 100),
        label = "File upload progress bar"
    )

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss,

        ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = modifier.fillMaxSize()
                .padding(20.dp)
        ) {
            FileList(

            )
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

                Text(
                    text = file?.fileName ?: "",
                    fontSize = 14.sp,
                    modifier = modifier
                        .height(50.dp)
                        .padding(vertical = 5.dp)
                )

                CButton(
                    text = "Send file",
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

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(16.dp)
                )

                Spacer(modifier = modifier.weight(1F))

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
    viewModel: FileDialogViewModel
){


}