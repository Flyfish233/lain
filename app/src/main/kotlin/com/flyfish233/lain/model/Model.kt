package com.flyfish233.lain.model

import android.net.Uri
import java.io.File

data class Model(
    val model: File,
    val name: String,
    val url: Uri = Uri.EMPTY,
    val desc: String = "",
    val space: String = "",
)