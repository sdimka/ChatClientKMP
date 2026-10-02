package dev.goood.chat_client.ui.systemMessages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.goood.chat_client.model.ChatModel
import dev.goood.chat_client.ui.composable.BallProgerssIndicator
import dev.goood.chat_client.ui.composable.CButton
import dev.goood.chat_client.ui.composable.DropDownMenu
import dev.goood.chat_client.ui.composable.SegmentedButtons
import dev.goood.chat_client.ui.theme.buttonBackground
import dev.goood.chat_client.viewModels.ModelPickerViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AddModelDialog(
    snackBarHostState: SnackbarHostState,
    onDismiss: () -> Unit,
    onSaved: (ChatModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<ModelPickerViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sources by viewModel.sourceList.collectAsStateWithLifecycle()
    val models by viewModel.modelList.collectAsStateWithLifecycle()
    val selectedSource by viewModel.selectedSource.collectAsStateWithLifecycle()
    val selectedModel by viewModel.selectedModel.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadModels()
    }

    LaunchedEffect(state) {
        when (val currentState = state) {
            is ModelPickerViewModel.State.SaveError -> {
                snackBarHostState.showSnackbar(currentState.message)
                viewModel.consumeSaveError()
            }
            is ModelPickerViewModel.State.Success -> onSaved(currentState.model)
            else -> Unit
        }
    }

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss,
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            modifier = modifier
                .wrapContentSize()
                .widthIn(max = 560.dp)
                .shadow(8.dp, RoundedCornerShape(24.dp)),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Add provider model",
                    color = buttonBackground,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(24.dp),
                )

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                when (val currentState = state) {
                    ModelPickerViewModel.State.Loading -> {
                        BallProgerssIndicator(modifier = Modifier.padding(48.dp))
                    }
                    is ModelPickerViewModel.State.LoadError -> {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Text(currentState.message, color = Color.DarkGray)
                            CButton(text = "Retry", onClick = viewModel::loadModels)
                        }
                    }
                    else -> {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                        ) {
                            Text("Source", fontWeight = FontWeight.Medium, color = Color.DarkGray)
                            SegmentedButtons(
                                choiceList = sources,
                                onSelected = viewModel::selectSource,
                                modifier = Modifier.fillMaxWidth(),
                            )

                            Text("Model", fontWeight = FontWeight.Medium, color = Color.DarkGray)
                            when {
                                currentState is ModelPickerViewModel.State.LoadingModels -> {
                                    BallProgerssIndicator(modifier = Modifier.padding(16.dp))
                                }
                                currentState is ModelPickerViewModel.State.ModelLoadError -> {
                                    Column(
                                        horizontalAlignment = Alignment.Start,
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        Text(currentState.message, color = Color.DarkGray)
                                        CButton(
                                            text = "Retry",
                                            onClick = viewModel::retrySelectedSource,
                                        )
                                    }
                                }
                                selectedSource == null -> {
                                    Text("Select a source to load its provider models.")
                                }
                                models.isEmpty() -> {
                                    Text("No provider models are available for this source.")
                                }
                                else -> {
                                    DropDownMenu(
                                        itemList = models,
                                        selectedItem = selectedModel,
                                        onSelected = viewModel::selectModel,
                                        itemLabel = { it.displayName },
                                        itemSupportingText = { model ->
                                            buildString {
                                                if (model.description.isNotBlank()) append(model.description)
                                                if (viewModel.isModelSaved(model)) {
                                                    if (isNotEmpty()) append(" · ")
                                                    append("Already added")
                                                }
                                            }
                                        },
                                        itemEnabled = { !viewModel.isModelSaved(it) },
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray),
                        border = BorderStroke(1.5.dp, Color.LightGray),
                    ) {
                        Text("Cancel")
                    }
                    CButton(
                        text = if (state is ModelPickerViewModel.State.Saving) "Adding…" else "Add model",
                        onClick = viewModel::addSelectedModel,
                        enabled = selectedModel != null && state !is ModelPickerViewModel.State.Saving,
                        modifier = Modifier.weight(1f).height(44.dp),
                    )
                }
            }
        }
    }
}
