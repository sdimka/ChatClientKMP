package dev.goood.chat_client.ui.platformComposable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragData.FilesList
import androidx.compose.ui.draganddrop.dragData
import androidx.compose.ui.graphics.Color
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent
import java.awt.dnd.DropTargetEvent
import java.io.File
import java.net.URI
import javax.swing.JPanel
import kotlin.io.path.toPath

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
actual fun PlatformDragAndDropArea(
    onFilesDropped: (List<String>) -> Unit,
    content: @Composable (() -> Unit),
    modifier: Modifier
) {

    var isDragging by remember { mutableStateOf(false) }
//    var files by remember { mutableStateOf<List<File>>(emptyList()) }

    val dragAndDropTarget = remember {
        object : DragAndDropTarget {
            override fun onEntered(event: DragAndDropEvent) {
                if (event.dragData() is FilesList) {
                    isDragging = true
                }
            }

            override fun onExited(event: DragAndDropEvent) {
                isDragging = false
            }

            override fun onDrop(event: DragAndDropEvent): Boolean {
                isDragging = false
                val dragData = event.dragData()
                if (dragData is FilesList) {
                    try {
                        val fileUris = dragData.readFiles()
                        val files = fileUris.mapNotNull { uriString ->
                            try {
                                URI(uriString).toPath().toString()
                            } catch (e: Exception) {
                                println("Error converting URI to File: $uriString, error: ${e.message}")
                                null
                            }
                        }
                        if (files.isNotEmpty()) {
                            onFilesDropped(files)
                            return true
                        }
                    } catch (e: Exception) {
                        println("Error reading dropped files: ${e.message}")
                        return false
                    }
                }
                return false
            }

            override fun onEnded(event: DragAndDropEvent) {
                isDragging = false
            }
        }
    }

    Box(modifier = modifier
        .background(if(isDragging) Color.Red else Color(0x2200AAFF))
        .dragAndDropTarget(
            shouldStartDragAndDrop = { event ->
                event.dragData() is FilesList
            },
            target = dragAndDropTarget
        )
    ) {
        Text(text = "Drop files here")
    }

}

@Composable
fun DragAndDropArea(
    modifier: Modifier = Modifier,
    fileExtensions: List<String> = emptyList(),
    onFilesDropped: (List<File>) -> Unit,
    content: @Composable (isDragOver: Boolean) -> Unit
) {
    var isDragOver by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
//            .fillMaxSize()

    ) {
        // Create SwingPanel just for drag-and-drop target
        SwingPanel(
            background = Color.Transparent,
            modifier = modifier.fillMaxSize(),
            factory = {
                object : JPanel() {
                    init {
                        dropTarget = object : DropTarget() {
                            override fun dragEnter(dtde: DropTargetDragEvent) {
                                isDragOver = true
                            }

                            override fun dragExit(dte: DropTargetEvent) {
                                isDragOver = false
                            }

                            override fun drop(evt: DropTargetDropEvent) {
                                isDragOver = false
                                try {
                                    evt.acceptDrop(DnDConstants.ACTION_COPY)
                                    val data = evt.transferable
                                        .getTransferData(DataFlavor.javaFileListFlavor) as? List<*>
                                        ?: return
                                    val files = data.filterIsInstance<File>()
                                    val filtered = if (fileExtensions.isNotEmpty()) {
                                        files.filter { it.extension.lowercase() in fileExtensions }
                                    } else {
                                        files
                                    }
                                    onFilesDropped(filtered)
                                    evt.dropComplete(true)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    evt.rejectDrop()
                                }
                            }
                        }
                    }
                }
            },
            update = {} // no dynamic updates needed
        )

        content(isDragOver)
    }
}