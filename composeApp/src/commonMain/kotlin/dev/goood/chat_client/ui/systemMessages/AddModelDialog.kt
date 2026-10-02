package dev.goood.chat_client.ui.systemMessages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.CheckCircleSolid
import compose.icons.lineawesomeicons.SearchSolid
import compose.icons.lineawesomeicons.TimesSolid
import dev.goood.chat_client.model.ChatModelList
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.goood.chat_client.model.ChatModel
import dev.goood.chat_client.ui.composable.BallProgerssIndicator
import dev.goood.chat_client.ui.composable.CButton
import dev.goood.chat_client.ui.composable.SegmentedButtons
import dev.goood.chat_client.ui.theme.buttonBackground
import dev.goood.chat_client.viewModels.ModelPickerViewModel
import dev.goood.chat_client.viewModels.matchRanges
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
    val filteredModels by viewModel.filteredModels.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

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
                                    ModelSearchList(
                                        models = models,
                                        filteredModels = filteredModels,
                                        query = searchQuery,
                                        selectedModel = selectedModel,
                                        isModelSaved = viewModel::isModelSaved,
                                        onQueryChange = viewModel::updateSearchQuery,
                                        onSelect = viewModel::selectModel,
                                        onMoveSelection = viewModel::moveSelection,
                                        onSubmit = viewModel::submitSearch,
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


@Composable
private fun ModelSearchList(
    models: ChatModelList,
    filteredModels: ChatModelList,
    query: String,
    selectedModel: ChatModel?,
    isModelSaved: (ChatModel) -> Boolean,
    onQueryChange: (String) -> Unit,
    onSelect: (ChatModel) -> Unit,
    onMoveSelection: (Int) -> Unit,
    onSubmit: () -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(query) {
        listState.scrollToItem(0)
    }

    // Keep the keyboard-selected row visible without jumping when the user clicks a visible row.
    LaunchedEffect(selectedModel, filteredModels) {
        val index = filteredModels.indexOf(selectedModel)
        if (index < 0) return@LaunchedEffect
        val visible = listState.layoutInfo.visibleItemsInfo
        val fullyVisible = visible.any { item ->
            item.index == index &&
                item.offset >= listState.layoutInfo.viewportStartOffset &&
                item.offset + item.size <= listState.layoutInfo.viewportEndOffset
        }
        if (!fullyVisible) listState.animateScrollToItem(index)
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                    when (event.key) {
                        Key.DirectionDown -> { onMoveSelection(1); true }
                        Key.DirectionUp -> { onMoveSelection(-1); true }
                        Key.Enter, Key.NumPadEnter -> { onSubmit(); true }
                        Key.Escape -> if (query.isNotEmpty()) { onQueryChange(""); true } else false
                        else -> false
                    }
                },
            placeholder = { Text("Search by name, ID or description") },
            leadingIcon = {
                Icon(
                    imageVector = LineAwesomeIcons.SearchSolid,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = LineAwesomeIcons.TimesSolid,
                            contentDescription = "Clear search",
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = buttonBackground,
            ),
        )

        Text(
            text = if (query.isBlank()) {
                "${models.size} models"
            } else {
                "${filteredModels.size} of ${models.size} models"
            },
            fontSize = 12.sp,
            color = Color.Gray,
        )

        if (filteredModels.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text("No models match \u201c${query.trim()}\u201d", color = Color.DarkGray)
                TextButton(onClick = { onQueryChange("") }) {
                    Text("Clear search", color = buttonBackground)
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color.LightGray.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
            ) {
                itemsIndexed(
                    items = filteredModels,
                    key = { _, model -> "${model.sourceID}/${model.name}" },
                ) { index, model ->
                    if (index > 0) HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    ModelRow(
                        model = model,
                        query = query,
                        isSelected = model == selectedModel,
                        isSaved = isModelSaved(model),
                        onClick = { onSelect(model) },
                    )
                }
            }
        }

        selectedModel?.let { model ->
            Text(
                text = "Selected: ${model.displayName}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = buttonBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ModelRow(
    model: ChatModel,
    query: String,
    isSelected: Boolean,
    isSaved: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) buttonBackground.copy(alpha = 0.08f) else Color.White)
            .clickable(enabled = !isSaved, onClick = onClick)
            .alpha(if (isSaved) 0.5f else 1f)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = highlighted(model.displayName, query),
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = highlighted(model.name, query),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (model.description.isNotBlank()) {
                Text(
                    text = model.description,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        when {
            isSaved -> Text(
                text = "Added",
                fontSize = 11.sp,
                color = Color.DarkGray,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            )
            isSelected -> Icon(
                imageVector = LineAwesomeIcons.CheckCircleSolid,
                contentDescription = "Selected",
                tint = buttonBackground,
                modifier = Modifier.size(22.dp),
            )
            else -> Spacer(Modifier.size(22.dp))
        }
    }
}

@Composable
private fun highlighted(text: String, query: String): AnnotatedString {
    val ranges = remember(text, query) { matchRanges(text, query) }
    return buildAnnotatedString {
        append(text)
        ranges.forEach { range ->
            addStyle(
                SpanStyle(fontWeight = FontWeight.Bold, background = buttonBackground.copy(alpha = 0.15f)),
                range.first,
                range.last + 1,
            )
        }
    }
}
