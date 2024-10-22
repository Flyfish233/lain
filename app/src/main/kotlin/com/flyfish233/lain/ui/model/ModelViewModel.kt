package com.flyfish233.lain.ui.model

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flyfish233.lain.R
import com.flyfish233.lain.model.Llama
import com.flyfish233.lain.model.Model
import com.flyfish233.lain.model.preload.loadI8mmModel
import com.flyfish233.lain.model.preload.loadNeonModel
import com.flyfish233.lain.utils.SpHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class ModelViewModel(private val context: Application) : AndroidViewModel(context) {
    private val downloadManager: DownloadManager by lazy {
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }
    private val downloadIdMap = mutableMapOf<Long, Int>()

    val models = MutableStateFlow<List<ModelStatus>>(emptyList())
    val hasI8mm = hasI8mm()
    val useI8mmModels = mutableStateOf(false)
    val currentModel = MutableStateFlow(
        Model(
            model = File(SpHelper.readKeyValue(context, "modelPath", "none")),
            name = SpHelper.readKeyValue(
                context, "model_name", context.getString(R.string.no_model)
            ),
        )
    )

    init {
        if (hasI8mm) useI8mmModels.value = true
        loadModel()
        checkOngoingDownloads()
    }

    fun switchModelList() {
        useI8mmModels.value = !useI8mmModels.value
        loadModel()
    }

    private fun loadModel() {
        setModels(
            if (useI8mmModels.value) {
                loadI8mmModel(context)
            } else {
                loadNeonModel(context)
            }
        )
    }

    fun startDownload(index: Int) {
        val modelStatus = models.value[index]
        if (modelStatus.state is DownloadState.NotDownloaded) {
            val request = createDownloadRequest(modelStatus.model)
            val downloadId = downloadManager.enqueue(request)
            downloadIdMap[downloadId] = index
            updateModelStatus(
                index, modelStatus.copy(downloadId = downloadId, state = DownloadState.Downloading)
            )
            observeDownloadProgress(downloadId)
        }
    }

    fun cancelDownload(index: Int) {
        models.value[index].downloadId?.let { downloadId ->
            downloadManager.remove(downloadId)
            downloadIdMap.remove(downloadId)
            updateModelStatus(
                index,
                models.value[index].copy(downloadId = null, state = DownloadState.NotDownloaded)
            )
        }
    }

    fun deleteModel(index: Int) {
        models.value[index].model.model.takeIf { it.exists() }?.delete()
        updateModelStatus(index, models.value[index].copy(state = DownloadState.NotDownloaded))
    }

    fun useModel(index: Int) {
        models.value[index].run {
            if (state is DownloadState.Downloaded) {
                with(model) {
                    SpHelper.writeKeyValue(context, "model_path", model.absolutePath)
                    SpHelper.writeKeyValue(context, "model_name", name)
                }
                currentModel.value = model
                viewModelScope.launch {
                    Llama.getInstance().apply {
                        unload()
                        load(model.model.absolutePath)
                    }
                    Toast.makeText(
                        context, context.getString(R.string.loaded, model.name), Toast.LENGTH_SHORT
                    ).show()
                    SpHelper.writeKeyValue(context, "first_time", false)
                }
            }
        }
    }

    private fun setModels(modelList: List<Model>) {
        models.value = modelList.map { model ->
            val state =
                if (model.model.exists()) DownloadState.Downloaded else DownloadState.NotDownloaded
            ModelStatus(model, null, state)
        }
    }

    private fun handleDownloadComplete(downloadId: Long) {
        downloadIdMap.remove(downloadId)?.let { index ->
            val modelStatus = models.value[index]
            val isDownloaded = downloadManager.getUriForDownloadedFile(downloadId) != null
            updateModelStatus(
                index, modelStatus.copy(
                    downloadId = null,
                    state = if (isDownloaded) DownloadState.Downloaded else DownloadState.NotDownloaded
                )
            )
        }
    }

    private fun checkOngoingDownloads() {
        val query = DownloadManager.Query().setFilterByStatus(
            DownloadManager.STATUS_RUNNING or DownloadManager.STATUS_PAUSED or DownloadManager.STATUS_PENDING
        )

        downloadManager.query(query)?.use { cursor ->
            val modelStatuses = models.value.toMutableList()
            while (cursor.moveToNext()) {
                val downloadId =
                    cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_ID))
                val uri =
                    cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))
                        ?.let(Uri::parse)
                val localFile = uri?.path?.let(::File)

                modelStatuses.forEachIndexed { index, modelStatus ->
                    if (modelStatus.model.model.absolutePath == localFile?.absolutePath) {
                        modelStatuses[index] = modelStatus.copy(
                            downloadId = downloadId, state = DownloadState.Downloading
                        )
                        downloadIdMap[downloadId] = index
                    }
                }
            }

            modelStatuses.forEachIndexed { index, modelStatus ->
                if (modelStatus.state == DownloadState.NotDownloaded && modelStatus.model.model.exists()) {
                    modelStatuses[index] = modelStatus.copy(state = DownloadState.Downloaded)
                }
            }

            models.value = modelStatuses
        }
    }

    private fun createDownloadRequest(model: Model): DownloadManager.Request {
        return DownloadManager.Request(model.url).apply {
            setDestinationUri(Uri.fromFile(model.model))
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setTitle(model.name)
            setAllowedOverMetered(SpHelper.readKeyValue(context, "metered", false))
        }
    }

    private fun observeDownloadProgress(downloadId: Long) {
        viewModelScope.launch {
            var isInProgress = true
            while (isInProgress) {
                downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
                    ?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val bytesDownloaded =
                                cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                            val bytesTotal =
                                cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                            if (bytesTotal > 0) {
                                val progress = (bytesDownloaded * 100) / bytesTotal
                                if (progress >= 99.9) {
                                    isInProgress = false
                                    handleDownloadComplete(downloadId)
                                }
                            }
                        }
                    }
                delay(1000)
            }
        }
    }

    private fun updateModelStatus(index: Int, newStatus: ModelStatus) {
        models.value = models.value.toMutableList().apply { this[index] = newStatus }
    }

    @Deprecated("Now LLAMAINFO fix this issue, use LLAMAINFO.")
    private fun hasI8mm(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("cat /proc/cpuinfo")
            var hasI8mm = false
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                reader.lineSequence().forEach { line ->
                    if (line.contains("i8mm")) {
                        hasI8mm = true
                        return@use
                    }
                }
            }
            hasI8mm
        } catch (_: Exception) {
            false
        }
    }
}
