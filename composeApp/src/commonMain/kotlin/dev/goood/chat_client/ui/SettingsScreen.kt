package dev.goood.chat_client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.ArrowRightSolid
import compose.icons.lineawesomeicons.Copy
import compose.icons.lineawesomeicons.CopySolid
import compose.icons.lineawesomeicons.PlaySolid
import dev.goood.chat_client.ui.composable.BallProgerssIndicator
import dev.goood.chat_client.ui.composable.CButton
import dev.goood.chat_client.ui.theme.buttonBackground
import dev.goood.chat_client.viewModels.TranslateViewModel
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {

    val viewModel: TranslateViewModel = koinViewModel()

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(Color.Blue)
    ) {

        // Box 1: 20% height
        Box(
            modifier = Modifier // Start a new modifier chain for this specific Box
                .fillMaxWidth()
                .weight(0.20f) // 20% of parent height
                .background(Color.Yellow)
        )

        // Box 2: 35% height
        Box(
            modifier = Modifier // New chain
                .fillMaxWidth()
                .weight(0.35f) // 35% of parent height
                .background(Color.Green)
        ) {
            TranslateInput(
                viewModel = viewModel,
                modifier = modifier
            )
        }

        // Box 3: 45% height
        Box(
            modifier = Modifier // New chain
                .fillMaxWidth()
                .weight(0.45f) // 45% of parent height
                .background(Color.LightGray)
        ){
            TranslateOutput(
                viewModel = viewModel,
                modifier = modifier
            )
        }
    }
}

@Composable
fun TranslateInput(
    viewModel: TranslateViewModel,
    modifier: Modifier = Modifier
) {

    val inputValue by viewModel.fromString.collectAsStateWithLifecycle()
    val source by viewModel.sourceLanguage.collectAsStateWithLifecycle()
    val target by viewModel.targetLanguage.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .padding(top = 5.dp)
                .fillMaxWidth()
        ) {
            Text(source.name)
            Button(
                onClick = { viewModel.changeDirection() },
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                modifier = modifier.padding(horizontal = 15.dp)
            ){
                Icon(
                    imageVector = LineAwesomeIcons.ArrowRightSolid,
                    contentDescription = null,
                    tint = Color.LightGray
                )
            }
            Text(target.name)
        }
        TextField(
            value = inputValue,
            onValueChange = { viewModel.setInput(it) },
            label = { Text("") },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = buttonBackground,
                unfocusedIndicatorColor = buttonBackground,
                focusedLabelColor = buttonBackground
            ),
            trailingIcon = {
                Box(
                    modifier = modifier.padding(end = 5.dp)
                ) {
                    when (state) {
                        TranslateViewModel.TranslateState.Loading -> {
                            BallProgerssIndicator()
                        }

                        else -> {
                            CButton(
                                icon = LineAwesomeIcons.PlaySolid,
                                onClick = { viewModel.translate() }
                            )
                        }
                    }
                }

            },
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
                .padding(vertical = 10.dp)
        )


    }
}

@Composable
fun TranslateOutput(
    viewModel: TranslateViewModel,
    modifier: Modifier = Modifier
) {

    val result by viewModel.resultString.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = modifier.fillMaxWidth()
    ){
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = modifier
                .padding(all = 5.dp)
                .fillMaxWidth()
        ) {
            Button(
                onClick = {
                    clipboardManager.setText(AnnotatedString(result))
                },
                modifier = modifier
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF274C77)),
                shape = RoundedCornerShape(8.dp)
            ){
                Icon(
                    imageVector = LineAwesomeIcons.CopySolid,
                    contentDescription = null,
                    tint = Color.LightGray
                )
            }
        }
        SelectionContainer {
            Text(
                result,
                modifier = modifier.padding(horizontal = 5.dp)
            )
        }
    }
}