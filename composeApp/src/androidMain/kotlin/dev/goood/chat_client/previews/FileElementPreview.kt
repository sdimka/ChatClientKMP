package dev.goood.chat_client.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import dev.goood.chat_client.di.appModulePreview
import dev.goood.chat_client.model.MFile
import dev.goood.chat_client.ui.filesDialog.FileElement
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication

@Composable
@Preview(showSystemUi = true)
fun FileElementPreview() {
    val context = LocalContext.current

    val file = MFile(
        id = "2",
        obj = "Obj",
        bytes = 123456,
        createdAt = 1613677385,
        filename = "salesOverview1_some_very_long_file_name.pdf",
        purpose = "assistants"
    )

    KoinApplication(application = {
        // If you need Context
        androidContext(context)
        modules(appModulePreview)
    }) {
        FileElement(
            file = file,
            isSelected = false,
            onSelected = {},
            onDelete = {},
            modifier = Modifier
        )
    }
}