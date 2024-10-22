package com.flyfish233.lain.model

import android.util.Log
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

@Suppress("unused")
class Llama private constructor() {
    // Native methods
    private external fun systemInfo(): String
    private external fun newSampler(): Long
    private external fun freeSampler(sampler: Long)
    private external fun loadModel(filename: String): Long
    private external fun freeModel(model: Long)
    private external fun newContext(model: Long): Long
    private external fun freeContext(context: Long)
    private external fun backendInit(numa: Boolean)
    private external fun backendFree()
    private external fun newBatch(nTokens: Int, embd: Int, nSeqMax: Int): Long
    private external fun freeBatch(batch: Long)
    private external fun kvCacheClear(context: Long)
    private external fun completionInit(context: Long, batch: Long, text: String, nLen: Int): Int
    private external fun completionLoop(
        context: Long, batch: Long, sampler: Long, nLen: Int, ncur: IntVar,
    ): String?

    private val tag = "LLAMA_JNI"
    private val state = AtomicReference<State>(State.Idle)

    @Volatile
    private var stopGeneration = false

    val isSending = MutableStateFlow(false)
    val isPreLoading = MutableStateFlow(false)

    private val stopIndicators = listOf(
        "User",
        "user",
        "system",
        "assistant",
        "<|im_end|>",
        "<|end|>",
        // "<|end_of_text|>"
    )
    private val threadPool = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    init {
        System.loadLibrary("llama-android")

        // logToAndroid()
        backendInit(false)
        Log.d(tag, systemInfo())
    }

    fun stopTextGeneration() {
        stopGeneration = true
        isSending.value = false
    }

    suspend fun load(filename: String) = withContext(threadPool) {
        if (state.compareAndSet(State.Idle, State.Loading)) {
            try {
                Log.i(tag, "Loading model $filename")
                val model = loadModel(filename).takeIf { it != 0L } ?: error("loadModel() failed")
                val context = newContext(model).takeIf { it != 0L } ?: error("newContext() failed")
                val batch = newBatch(2048, 0, 1).takeIf { it != 0L } ?: error("newBatch() failed")
                val sampler = newSampler().takeIf { it != 0L } ?: error("newSampler() failed")

                state.set(State.Loaded(model, context, batch, sampler))
                Log.i(tag, "Loaded model $filename")
            } catch (e: Exception) {
                state.set(State.Idle)
                throw e
            }
        } else {
            error("Model already loaded or in loading process")
        }
    }

    // @OptIn(ExperimentalEncodingApi::class)
    fun send(message: String, nLen: Int): Flow<String> = flow {
        stopGeneration = false
        isPreLoading.value = true

        val currentState = state.get()
        if (currentState is State.Loaded) {
            var stopTriggered = false
            val ncur =
                IntVar(completionInit(currentState.context, currentState.batch, message, nLen))
            isPreLoading.value = false
            isSending.value = true

            while (ncur.value <= nLen && !stopGeneration) {
                val output = completionLoop(
                    currentState.context, currentState.batch, currentState.sampler, nLen, ncur
                )
                if (output != null) {
                    // Log.d(tag, "tok: $output, b64: ${Base64.encode(output.toByteArray())}")
                    if (output in stopIndicators) {
                        stopTriggered = true
                        break
                    }
                } else {
                    stopTriggered = true
                    break
                }
                emit(output)
            }

            if (stopTriggered) {
                stopTextGeneration()
                kvCacheClear(currentState.context)
            }
        } else {
            error("No model loaded")
        }
        isSending.value = false
    }.flowOn(threadPool)


    suspend fun unload() = withContext(threadPool) {
        when (val currentState = state.get()) {
            is State.Loaded -> {
                freeContext(currentState.context)
                freeModel(currentState.model)
                freeBatch(currentState.batch)
                freeSampler(currentState.sampler)
                state.set(State.Idle)
            }

            else -> Log.w(tag, "No model loaded")
        }
    }

    suspend fun terminate() {
        unload()
        threadPool.close()
        backendFree()
    }

    companion object {
        private class IntVar(value: Int) {
            @Volatile
            var value: Int = value
                private set

            fun inc() {
                synchronized(this) {
                    value += 1
                }
            }
        }

        private sealed interface State {
            data object Idle : State
            data object Loading : State
            class Loaded(
                val model: Long,
                val context: Long,
                val batch: Long,
                val sampler: Long,
            ) : State
        }

        @Volatile
        private var instance: Llama? = null

        fun getInstance(): Llama = instance ?: synchronized(this) {
            instance ?: Llama().also { instance = it }
        }
    }
}

/*
private external fun benchModel(
    context: Long,
    model: Long,
    batch: Long,
    pp: Int,
    tg: Int,
    pl: Int,
    nr: Int,
): String
*/

//    suspend fun bench(pp: Int, tg: Int, pl: Int, nr: Int = 1): String {
//        return withContext(runLoop) {
//            when (val state = threadLocalState.get()) {
//                is State.Loaded -> {
//                    Log.d(tag, "bench(): $state")
//                    benchModel(state.context, state.model, state.batch, pp, tg, pl, nr)
//                }
//
//                else -> throw IllegalStateException("No model loaded")
//            }
//        }
//    }