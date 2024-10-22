package com.flyfish233.lain.ui.model

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.flyfish233.lain.R
import com.flyfish233.lain.ui.theme.AppTheme
import com.flyfish233.lain.utils.SpHelper

class ModelActivity : ComponentActivity() {
    private val viewModel: ModelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(Modifier.safeDrawingPadding()) {
                        MainCompose(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun MainCompose(viewModel: ModelViewModel) {
    val models by viewModel.models.collectAsState()
    LazyColumn {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Text(
                        stringResource(R.string.in_use), style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        viewModel.currentModel.collectAsState().value.name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                SettingsCard(viewModel)
            }
        }
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.model_list_info),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = if (viewModel.useI8mmModels.value) stringResource(R.string.i8mm_desc) else stringResource(
                            R.string.neon_desc
                        ), style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        itemsIndexed(models) { index, modelStatus ->
            ModelCard(index, modelStatus, viewModel)
        }

    }
}

@Composable
private fun ModelCard(
    index: Int,
    modelStatus: ModelStatus,
    viewModel: ModelViewModel,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = modelStatus.model.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = modelStatus.model.desc)
                Text(text = "大小：${modelStatus.model.space}")
            }

            when (modelStatus.state) {
                is DownloadState.NotDownloaded -> {
                    Button(onClick = { viewModel.startDownload(index) }) {
                        Text("下载")
                    }
                }

                is DownloadState.Downloading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.cancelDownload(index) }) {
                            Text("取消下载")
                        }
                    }
                }

                is DownloadState.Downloaded -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { viewModel.useModel(index) }) {
                            Text("使用")
                        }
                        Button(onClick = { viewModel.deleteModel(index) }) {
                            Text("删除")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(viewModel: ModelViewModel) {
    val context = LocalContext.current
    val autoTts = remember { mutableStateOf(SpHelper.readKeyValue(context, "auto_tts", false)) }
    val tts = remember { mutableStateOf(SpHelper.readKeyValue(context, "tts", false)) }
    val autoScroll =
        remember { mutableStateOf(SpHelper.readKeyValue(context, "auto_scroll", true)) }
    val nLen = remember { mutableIntStateOf(SpHelper.readKeyValue(context, "n_len", 512)) }
    val metered = remember { mutableStateOf(SpHelper.readKeyValue(context, "metered", false)) }

    ExpandableCard(title = "设置") {
        Column {
            if (!viewModel.hasI8mm) {
                Text(
                    "您的处理器不支持 i8mm 指令集，无法使用 i8mm 模型。",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(
                        start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp
                    )
                )
            } else {
                OptionSwitch(
                    title = "使用 i8mm 指令集模型",
                    checked = viewModel.useI8mmModels.value,
                    onCheckedChange = { viewModel.switchModelList() },
                )
            }

            OptionSwitch(title = "通过计费网络下载",
                subtitle = "允许使用流量等网络下载模型",
                checked = metered.value,
                onCheckedChange = {
                    metered.value = it
                    SpHelper.writeKeyValue(context, "metered", it)
                })

            OptionSwitch(title = "自动滚动",
                subtitle = "自动滚动聊天",
                checked = autoScroll.value,
                onCheckedChange = {
                    autoScroll.value = it
                    SpHelper.writeKeyValue(context, "auto_scroll", it)
                })

            OptionSwitch(title = "自动朗读",
                subtitle = "自动朗读每条消息",
                checked = autoTts.value,
                onCheckedChange = {
                    autoTts.value = it
                    SpHelper.writeKeyValue(context, "auto_tts", it)
                })

            OptionSwitch(title = "朗读按钮",
                subtitle = "显示手动朗读按钮",
                checked = tts.value,
                onCheckedChange = {
                    tts.value = it
                    SpHelper.writeKeyValue(context, "tts", it)
                })

            OptionSwitch(title = "JSON 分享",
                subtitle = "以 JSON 分享聊天记录",
                checked = tts.value,
                onCheckedChange = {
                    tts.value = it
                    SpHelper.writeKeyValue(context, "json", it)
                })

            OptionText(title = "单次回复限制",
                subtitle = "Token 数量",
                value = nLen.intValue,
                onValueChange = {
                    nLen.intValue = it
                    SpHelper.writeKeyValue(context, "n_len", it)
                })
        }
    }
}

@Stable
@Composable
private fun OptionText(
    title: String,
    subtitle: String = "",
    value: Int,
    onValueChange: (Int) -> Unit,
) {
    var textFieldValue by remember { mutableStateOf(value.toString()) }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(
                BorderStroke(1.dp, Color.Gray), shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title, style = MaterialTheme.typography.bodyMedium
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    textFieldValue = newValue
                    val parsedValue = newValue.toIntOrNull()
                    if (parsedValue == null || parsedValue <= 1) {
                        isError = true
                    } else {
                        isError = false
                        onValueChange(parsedValue)
                    }
                },
                isError = isError,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }
        if (isError) {
            Text(
                text = "请输入大于 1 的数字",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Stable
@Composable
private fun OptionSwitch(
    title: String,
    subtitle: String = "",
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(
                BorderStroke(1.dp, Color.Gray), shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title, style = MaterialTheme.typography.bodyMedium
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
    }
}


@Stable
@Composable
private fun ExpandableCard(
    title: String,
    subtitle: String = "",
    content: @Composable () -> Unit,
) {
    val expanded = remember { mutableStateOf(false) }

    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        modifier = Modifier.clickable(onClick = { expanded.value = !expanded.value })
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title, fontWeight = FontWeight.Bold
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Icon(
                    imageVector = if (expanded.value) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = "Expand",
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            AnimatedVisibility(
                visible = expanded.value,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                content()
            }
        }
    }
}