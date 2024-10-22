package com.flyfish233.lain.ui.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import com.flyfish233.lain.R
import com.flyfish233.lain.model.Model
import com.flyfish233.lain.ui.model.ModelActivity
import com.flyfish233.lain.ui.theme.AppTheme
import com.flyfish233.lain.utils.SpHelper
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import nl.marc_apps.tts.rememberTextToSpeechOrNull
import java.io.File

@ExperimentalMaterial3Api
class ChatActivity : ComponentActivity() {
    private val clipboardManager by lazy { getSystemService<ClipboardManager>()!! }
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.safeDrawingPadding()) {
                        AppBar(this@ChatActivity, viewModel)
                        HorizontalDivider()
                        MainCompose(viewModel, clipboardManager)
                    }
                }
            }
        }
    }
}

@Composable
private fun MainCompose(
    viewModel: ChatViewModel, clipboardManager: ClipboardManager,
) {
    val isScrollEnabled = SpHelper.readKeyValue("auto_scroll", true)
    var autoScrollEnabled by remember { mutableStateOf(isScrollEnabled) }
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val messages = viewModel.messages.value

    // Listen to the scroll state and determine if it is at the bottom
    LaunchedEffect(scrollState) {
        snapshotFlow {
            val lastVisibleItemIndex =
                scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItemsCount = scrollState.layoutInfo.totalItemsCount
            lastVisibleItemIndex >= totalItemsCount - 1
        }.distinctUntilChanged().collect { isAtBottom ->
            autoScrollEnabled = isAtBottom
        }
    }

    // When the message list changes and auto-scroll is enabled, scroll to the bottom of the list
    LaunchedEffect(messages.size) {
        if (autoScrollEnabled && messages.isNotEmpty()) {
            scrollState.scrollToItem(messages.size - 1)
        }
    }

    // Auto Text-to-speech
    if (SpHelper.readKeyValue("auto_tts", false)) {
        val tts = rememberTextToSpeechOrNull()
        val previousIsSending = remember { mutableStateOf(viewModel.isSending.value) }

        LaunchedEffect(viewModel.isSending.collectAsState().value) {
            Log.d("ChatActivity", "isSending: ${viewModel.isSending.value}")
            if (previousIsSending.value && !viewModel.isSending.value) {
                Log.d("ChatActivity", "isSending: ${viewModel.isSending.value}")
                coroutineScope.launch {
                    tts?.apply {
                        say(viewModel.messages.value.last()["content"] ?: "")
                    }
                }
            }
            previousIsSending.value = viewModel.isSending.value
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = scrollState, modifier = Modifier.weight(1f)
        ) {
            coroutineScope.launch {
                if (autoScrollEnabled) {
                    scrollState.animateScrollToItem(viewModel.messages.value.size)
                }
            }

            itemsIndexed(viewModel.messages.value) { _, messageMap ->
                SelectionContainer {
                    ChatMessage(messageMap, clipboardManager)
                }
            }
        }

        MessageInput(viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun AppBar(
    activity: ComponentActivity,
    viewModel: ChatViewModel,
) {
    val buttonInteractionSource = remember { MutableInteractionSource() }
    TopAppBar(title = { Text("Lain") }, actions = {
        IconButton(onClick = {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, viewModel.share())
            }
            val chooser = Intent.createChooser(intent, activity.getString(R.string.share))
            activity.startActivity(chooser)
        }) {
            Icon(
                imageVector = Icons.Rounded.IosShare,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "Share"
            )
        }
        Box {
            IconButton(
                interactionSource = buttonInteractionSource,
                onClick = {}, // Box()
            ) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Reload",
                )
            }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .combinedClickable(
                        onLongClick = {
                            viewModel.stop()
                            Log.d("ChatActivity", "Clear")
                            viewModel.load(
                                Model(
                                    model = File(
                                        SpHelper.readKeyValue(
                                            activity, "modelPath", "none"
                                        )
                                    ),
                                    name = SpHelper.readKeyValue(activity, "model_name", "none"),
                                )
                            )
                        },
                        onClick = {
                            viewModel.stop()
                            viewModel.clear()
                        },
                        interactionSource = buttonInteractionSource,
                        indication = null,
                    )
            )
        }
        IconButton(onClick = {
            val intent = Intent(activity, ModelActivity::class.java)
            activity.startActivity(intent)
        }) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "Settings",
            )
        }
    })
}

@Composable
private fun ChatMessage(messageMap: Map<String, String>, clipboardManager: ClipboardManager) {
    val role = messageMap["role"] ?: ""
    val content = messageMap["content"] ?: ""

    if (role != "system") {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = if (role == "assistant") MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = when (role) {
                            "assistant" -> "🤖"
                            "log" -> "📱"
                            "error" -> "⛔"
                            else -> "🤗"
                        }, style = MaterialTheme.typography.labelSmall
                    )
                    Row(
                        horizontalArrangement = Arrangement.End
                    ) {
                        Icon(imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = "Copy",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    clipboardManager.setPrimaryClip(
                                        ClipData.newPlainText("AI", content)
                                    )
                                })

                        if (SpHelper.readKeyValue("tts", false)) {
                            val tts = rememberTextToSpeechOrNull()
                            val ttsScope = rememberCoroutineScope()

                            Icon(imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                                contentDescription = "Copy",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { ttsScope.launch { tts?.apply { say(content) } } })
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MarkdownText(
                        markdown = content,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp)),
                        isTextSelectable = true
                    )

                }
            }
        }
    }
}

@Composable
private fun MessageInput(viewModel: ChatViewModel) {
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.padding(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (viewModel.isPreLoading.collectAsState().value) {
                OutlinedTextField(enabled = false,
                    value = stringResource(R.string.generating),
                    onValueChange = { },
                    shape = RoundedCornerShape(12.dp),
                    label = { Text(stringResource(R.string.status)) })
                CircularProgressIndicator()
                return@Row
            }

            OutlinedTextField(
                value = viewModel.message.value,
                onValueChange = { viewModel.updateMessage(it) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                label = { Text(stringResource(R.string.message)) },
            )
            if (!viewModel.isSending.collectAsState().value) {
                IconButton(onClick = {
                    viewModel.send()
                    focusManager.clearFocus()
                }) {
                    Icon(
                        imageVector = Icons.Rounded.ExpandLess, contentDescription = "Send"
                    )
                }
            } else {
                IconButton(onClick = { viewModel.stop() }) {
                    Icon(
                        imageVector = Icons.Rounded.Close, contentDescription = "Stop"
                    )
                }
            }
        }
    }
}