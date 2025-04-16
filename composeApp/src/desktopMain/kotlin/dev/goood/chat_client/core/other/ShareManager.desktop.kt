package dev.goood.chat_client.core.other

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import kotlin.io.path.copyTo
import kotlin.io.path.exists

actual class ShareManager {

    actual fun shareText(text: String) {
        val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val stringSelection = StringSelection(text)
        clipboard.setContents(stringSelection, null)
        // Optionally: You might want to display a confirmation dialog or notification here
        println("Copied text to clipboard: $text") // For debugging, replace with UI feedback
        // For example:
        // JOptionPane.showMessageDialog(null, "Text copied to clipboard!", "Shared", JOptionPane.INFORMATION_MESSAGE)
    }

    actual suspend fun shareFile(file: ShareFileModel): Result<Unit> {
        return try {
            val desktopFile = File(file.fileName)
            if (desktopFile.exists()) {
                // Option 1: Open with default application
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(desktopFile)
                    return Result.success(Unit)
                }

                // Option 2:  File chooser to copy (more control, but requires user interaction)
                val fileChooser = JFileChooser().apply {
                    dialogTitle = "Choose Destination Folder"
                    fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                    approveButtonText = "Copy Here"
                }
                val result = fileChooser.showOpenDialog(null) // 'null' for parent component means it's a top-level dialog

                if (result == JFileChooser.APPROVE_OPTION) {
                    val destinationFolder = fileChooser.selectedFile
                    if (destinationFolder != null && destinationFolder.isDirectory) {
                        val destinationFile = File(destinationFolder, desktopFile.name)
                        desktopFile.copyTo(destinationFile, overwrite = false) // Or true, if overwrite is allowed
                        JOptionPane.showMessageDialog(
                            null,
                            "File copied to: ${destinationFile.absolutePath}",
                            "File Shared",
                            JOptionPane.INFORMATION_MESSAGE
                        )
                        return Result.success(Unit)
                    } else {
                        return Result.failure(Exception("Invalid destination folder selected"))
                    }
                } else {
                    return Result.failure(Exception("Sharing canceled or no destination selected"))
                }
            } else {
                println("File not found: ${file.fileName}")
                Result.failure(Exception("File not found: ${file.fileName}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun openFileWithFileManager(): ShareFileModel? {
        return try {
            val fileChooser = JFileChooser().apply {
                dialogTitle = "Select a File to Open"
                fileSelectionMode = JFileChooser.FILES_ONLY
            }

            val result = fileChooser.showOpenDialog(null) // 'null' for parent component

            if (result == JFileChooser.APPROVE_OPTION) {
                val selectedFile = fileChooser.selectedFile
                if (selectedFile != null && selectedFile.isFile) {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                        Desktop.getDesktop().open(selectedFile) // Open file with default app
                    }
                    ShareFileModel(fileName = selectedFile.name, bytes = selectedFile.readBytes())
                } else {
                    null // No file selected or not a valid file
                }
            } else {
                null // User canceled the selection
            }
        } catch (e: Exception) {
            e.printStackTrace()  // For debugging, replace with proper logging in a real app
            null // Handle exceptions appropriately
        }
    }

}

@Composable
actual fun rememberShareManager(): ShareManager {
    return remember { ShareManager() }
}