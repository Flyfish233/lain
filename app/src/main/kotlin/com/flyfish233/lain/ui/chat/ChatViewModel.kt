package com.flyfish233.lain.ui.chat

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flyfish233.lain.R
import com.flyfish233.lain.model.Llama
import com.flyfish233.lain.model.Model
import com.flyfish233.lain.utils.SpHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.io.File

class ChatViewModel(private val context: Application) : AndroidViewModel(context) {
    private val llama: Llama by lazy { Llama.getInstance() }
    private val tag: String = "ChatViewModel"
    private val initPrompt =
        "You are a knowledgeable, efficient, and direct AI assistant. Provide concise answers, focusing on the key information needed. Offer suggestions tactfully when appropriate to improve outcomes. Engage in productive collaboration with the user. Speak in Chinese."

    val isPreLoading: MutableStateFlow<Boolean> = llama.isPreLoading
    val isSending: MutableStateFlow<Boolean> = llama.isSending

    private var initial by mutableStateOf(true)
    var message = mutableStateOf("")
    val messages = mutableStateOf(listOf<Map<String, String>>())

    init {
        if (SpHelper.readKeyValue(context, "first_time", true)) {
            addMessage("log", context.resources.getString(R.string.welcome))
        } else {
            load(
                Model(
                    File(SpHelper.readKeyValue(context, "model_path", "none")),
                    SpHelper.readKeyValue(context, "model_name", "")
                )
            )
        }

    }

    fun load(model: Model) {
        viewModelScope.launch {
            try {
                llama.load(model.model.absolutePath)
                addMessage("log", context.resources.getString(R.string.initialized))
            } catch (exc: IllegalStateException) {
                if (exc.message?.contains("already loaded") == true) {
                    llama.unload()
                    try {
                        llama.load(model.model.absolutePath)
                    } catch (exc: IllegalStateException) {
                        addMessage("error", exc.message ?: "")
                        addMessage(
                            "error",
                            context.resources.getString(R.string.load_failed, model.name)
                        )
                    }
                    addMessage("log", context.resources.getString(R.string.reloaded, model.name))
                } else {
                    addMessage("error", exc.message ?: "")
                    addMessage(
                        "error",
                        context.resources.getString(R.string.load_failed, model.name)
                    )
                }
            }
        }
    }

    fun send(prompt: String = SpHelper.readKeyValue(getApplication(), "prompt", initPrompt)) {
        val userMessage = trim(message.value)
        message.value = ""
        if (userMessage != "" && userMessage != " ") {
            if (initial) {
                addMessage("system", prompt)
                initial = false
            }

            addMessage("user", userMessage)
            val text = parseTemplateJson(messages.value) + "assistant \n"

            viewModelScope.launch {
                llama.send(text, SpHelper.readKeyValue(getApplication(), "n_len", 2048)).catch {
                    Log.e(tag, "send() failed", it)
                    addMessage("error", it.message ?: "")
                }.collect { response ->
                    addMessage("assistant", response)
                }
            }
        }
    }

    fun clear() {
        messages.value = listOf()
        initial = true
    }

    fun stop() {
        llama.stopTextGeneration()
    }

    fun share(): String {
        return if (SpHelper.readKeyValue(getApplication(), "share_json", false)) {
            messagesToString(messages.value)
        } else {
            parseTemplateJson(messages.value)
        }
    }

    fun updateMessage(newMessage: String) {
        message.value = newMessage
    }

    private fun addMessage(role: String, content: String) {
        val newMessage = mapOf("role" to role, "content" to content)

        messages.value = if (messages.value.isNotEmpty() && messages.value.last()["role"] == role) {
            val lastMessageContent = messages.value.last()["content"] ?: ""
            val updatedContent = "$lastMessageContent$content"
            val updatedLastMessage = messages.value.last() + ("content" to updatedContent)
            messages.value.toMutableList().apply {
                set(messages.value.lastIndex, updatedLastMessage)
            }
        } else {
            messages.value + listOf(newMessage)
        }
    }

    override fun onCleared() {
        super.onCleared()

        viewModelScope.launch {
            try {
                llama.terminate()
            } catch (exc: IllegalStateException) {
                addMessage("error", exc.message ?: "")
            }
        }
    }
}

private fun messagesToString(messages: List<Map<String, String>>): String {
    val filtered = messages.filter { it["role"] != "log" && it["role"] != "error" }
    return filtered.joinToString("\n") { "${it["role"]}: ${it["content"]}" }
}

private fun trim(input: String): String {
    return input.replace("\\s+".toRegex(), " ").trim()
}

private fun parseTemplateJson(chatData: List<Map<String, String>>): String {
    var chatStr = ""
    for (data in chatData) {
        val role = data["role"]
        val content = data["content"]
        if (role != "log") {
            chatStr += "$role \n$content \n"
        }

    }
    return chatStr
}