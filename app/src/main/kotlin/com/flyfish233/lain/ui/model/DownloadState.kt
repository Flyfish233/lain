package com.flyfish233.lain.ui.model

sealed class DownloadState {
    data object NotDownloaded : DownloadState()
    data object Downloading : DownloadState()
    data object Downloaded : DownloadState()
}