package com.flyfish233.lain.ui.model

import com.flyfish233.lain.model.Model

data class ModelStatus(
    val model: Model,
    val downloadId: Long? = null,
    val state: DownloadState = DownloadState.NotDownloaded,
)