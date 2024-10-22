package com.flyfish233.lain.model.preload

import android.content.Context
import android.net.Uri
import com.flyfish233.lain.model.Model
import java.io.File

fun loadI8mmModel(context: Context) = listOf(
    Model(
        model = File(context.getExternalFilesDir(null), "qwen2-0_5b-instruct-Q4_0_4_8.gguf"),
        name = "Qwen2 0.5B",
        url = Uri.parse("https://hf-mirror.com/flyfishstudio/qwen2-0_5b-instruct-Q4_0_4_8.gguf/resolve/main/qwen2-0_5b-instruct-Q4_0_4_8.gguf"),
        desc = "你正在使用来自 hf-mirror.com 加速的模型集，以在中国大陆直连高速下载。",
        space = "352 MB"
    ),
    Model(
        model = File(
            context.getExternalFilesDir(null), "tinyllama-1.1b-chat-v1.0-Q4_0_4_8.gguf"
        ),
        name = "TinyLlama 1.1B",
        url = Uri.parse("https://hf-mirror.com/flyfishstudio/tinyllama-1.1b-chat-v1.0-Q4_0_4_8.gguf/resolve/main/tinyllama-1.1b-chat-v1.0-Q4_0_4_8.gguf"),
        desc = "",
        space = "638 MB"
    ),
    Model(
        model = File(
            context.getExternalFilesDir(null), "SmolLM-1.7B-instruct-v0.2-Q4_0_4_8.gguf"
        ),
        name = "SmolLM 1.7B",
        url = Uri.parse("https://hf-mirror.com/flyfishstudio/SmolLM-1.7B-Instruct-v0.2-Q4_0_4_8.gguf/resolve/main/SmolLM-1.7B-Instruct-v0.2-Q4_0_4_8.gguf"),
        desc = "",
        space = "991 MB"
    ),
    Model(
        model = File(context.getExternalFilesDir(null), "Qwen1.5-1.8B-layla-v4-Q4_0_4_8.gguf"),
        name = "Qwen 1.5 1.8B Layla",
        url = Uri.parse("https://hf-mirror.com/flyfishstudio/Qwen1.5-1.8B-layla-v4-Q4_0_4_8.gguf/resolve/main/Qwen1.5-1.8B-layla-v4-Q4_0_4_8.gguf"),
        desc = "",
        space = "1.12 GB"
    ),
    Model(
        model = File(context.getExternalFilesDir(null), "rocket-3b-Q4_0_4_8.gguf"),
        name = "Rocket 3B",
        url = Uri.parse("https://hf-mirror.com/flyfishstudio/rocket-3b-Q4_0_4_8.gguf/resolve/main/rocket-3b-Q4_0_4_8.gguf"),
        desc = "",
        space = "1.61 GB"
    ),
    Model(
        model = File(context.getExternalFilesDir(null), "gemma_2_2b_it_v2-Q4_0_4_8.gguf"),
        name = "Gemma 2 2B",
        url = Uri.parse("https://hf-mirror.com/flyfishstudio/gemma_2_2b_it_v2-Q4_0_4_8.gguf/resolve/main/gemma_2_2b_it_v2-Q4_0_4_8.gguf"),
        desc = "",
        space = "1.63 GB"
    ),
    Model(
        model = File(context.getExternalFilesDir(null), "Gemmasutra-Mini-2B-v1-Q4_0_4_8.gguf"),
        name = "GemmaSutra Mini 2B",
        url = Uri.parse("https://hf-mirror.com/TheDrummer/Gemmasutra-Mini-2B-v1-GGUF/resolve/main/Gemmasutra-Mini-2B-v1-Q4_0_4_8.gguf"),
        desc = "",
        space = "1.63 GB"
    ),
    Model(
        model = File(
            context.getExternalFilesDir(null), "gemma-2-2b-it-abliterated-Q4_0_4_8.gguf"
        ),
        name = "Gemma 2 2B Abliterated",
        url = Uri.parse("https://hf-mirror.com/flyfishstudio/gemma-2-2b-it-abliterated-Q4_0_4_8.gguf/resolve/main/gemma-2-2b-it-abliterated-Q4_0_4_8.gguf"),
        desc = "",
        space = "1.63 GB"
    ),
    Model(
        model = File(context.getExternalFilesDir(null), "Qwen2.5-3B-Instruct-Q4_0_4_8.gguf"),
        name = "Qwen 2.5 3B",
        url = Uri.parse("https://hf-mirror.com/flyfishstudio/Qwen2.5-3B-Instruct-Q4_0_4_8.gguf/resolve/main/Qwen2.5-3B-Instruct-Q4_0_4_8.gguf"),
        desc = "",
        space = "1.82 GB"
    ),
    Model(
        model = File(
            context.getExternalFilesDir(null), "Phi-3.1-mini-4k-instruct-Q4_0_4_8.gguf"
        ),
        name = "Phi 3.1 Mini 4K",
        url = Uri.parse("https://hf-mirror.com/flyfishstudio/Phi-3.1-mini-4k-instruct-Q4_0_4_8.gguf/resolve/main/Phi-3.1-mini-4k-instruct-Q4_0_4_8.gguf"),
        desc = "",
        space = "2.18 GB"
    ),
    Model(
        model = File(context.getExternalFilesDir(null), "Phi-3.5-mini-instruct-Q4_0_4_8.gguf"),
        name = "Phi 3.5 Mini",
        url = Uri.parse("https://hf-mirror.com/flyfishstudio/Phi-3.5-mini-instruct-Q4_0_4_8.gguf/resolve/main/Phi-3.5-mini-instruct-Q4_0_4_8.gguf"),
        desc = "",
        space = "2.18 GB"
    ),
    Model(
        model = File(
            context.getExternalFilesDir(null), "phi-3.5-mini-instruct_Uncensored-Q4_0_4_8.gguf"
        ),
        name = "Phi 3.5 Mini Abliterated",
        url = Uri.parse("https://hf-mirror.com/flyfishstudio/Phi-3.5-mini-instruct_Uncensored-Q4_0_4_8.gguf/resolve/main/Phi-3.5-mini-instruct_Uncensored-Q4_0_4_8.gguf"),
        desc = "",
        space = "2.18 GB"
    ),
    Model(
        model = File(
            context.getExternalFilesDir(null), "Llama-3.1-Minitron-4B-Width-Base-Q4_0_4_8.gguf"
        ),
        name = "Llama 3.1 Minitron 4B Width Base",
        url = Uri.parse("https://hf-mirror.com/ThomasBaruzier/Llama-3.1-Minitron-4B-Width-Base-GGUF/resolve/main/Llama-3.1-Minitron-4B-Width-Base-Q4_0_4_8.gguf"),
        desc = "",
        space = "2.65 GB"
    ),
    Model(
        model = File(context.getExternalFilesDir(null), "magnum-v2-4b-lowctx-Q4_0_4_8.gguf"),
        name = "Magnum v2 4B Lowctx",
        url = Uri.parse("https://hf-mirror.com/adamo1139/magnum-v2-4b-gguf-lowctx/resolve/main/magnum-v2-4b-lowctx-Q4_0_4_8.gguf"),
        desc = "",
        space = "2.65 GB"
    ),
    Model(
        model = File(context.getExternalFilesDir(null), "llama-2-7b-chat-Q4_0_4_8.gguf"),
        name = "Llama 2 7B Chat",
        url = Uri.parse("https://hf-mirror.com/flyfishstudio/llama-2-7b-chat-Q4_0_4_8.gguf/resolve/main/llama-2-7b-chat-Q4_0_4_8.gguf"),
        desc = "",
        space = "3.83 GB"
    ),
    Model(
        model = File(context.getExternalFilesDir(null), "orca-mini-v3-7b-Q4_0_4_8.gguf"),
        name = "Orca Mini v3 7B",
        url = Uri.parse("https://hf-mirror.com/flyfishstudio/orca_mini_v3_7b-Q4_0_4_8.gguf/resolve/main/orca_mini_v3_7b-Q4_0_4_8.gguf"),
        desc = "",
        space = "3.83 GB"
    ),
    Model(
        model = File(context.getExternalFilesDir(null), "yarn-llama-2-7b-128k-Q4_0_4_8.gguf"),
        name = "Yarn Llama 2 7B 128K",
        url = Uri.parse("https://hf-mirror.com/flyfishstudio/yarn-llama-2-7b-128k-Q4_0_4_8.gguf/resolve/main/yarn-llama-2-7b-128k-Q4_0_4_8.gguf"),
        desc = "",
        space = "3.83 GB"
    ),
    Model(
        model = File(
            context.getExternalFilesDir(null), "Mistral-7B-Instruct-v0.3-Q4_0_4_8.gguf"
        ),
        name = "Mistral 7B",
        url = Uri.parse("https://hf-mirror.com/flyfishstudio/Mistral-7B-Instruct-v0.3-Q4_0_4_8.gguf/resolve/main/Mistral-7B-Instruct-v0.3-Q4_0_4_8.gguf"),
        desc = "",
        space = "4.11 GB"
    ),
    Model(
        model = File(
            context.getExternalFilesDir(null), "openhermes-2.5-mistral-7b-Q4_0_4_8.gguf"
        ),
        name = "OpenHermes 2.5 Mistral 7B",
        url = Uri.parse("https://hf-mirror.com/flyfishstudio/openhermes-2.5-mistral-7b-Q4_0_4_8.gguf/resolve/main/openhermes-2.5-mistral-7b-Q4_0_4_8.gguf"),
        desc = "",
        space = "4.11 GB"
    ),
    Model(
        model = File(
            context.getExternalFilesDir(null), "meta-llama-3.1-8b-instruct-Q4_0_4_8.gguf"
        ),
        name = "Meta Llama 3.1 8B",
        url = Uri.parse("https://hf-mirror.com/flyfishstudio/meta-llama-3.1-8b-instruct-Q4_0_4_8.gguf/resolve/main/meta-llama-3.1-8b-instruct-Q4_0_4_8.gguf"),
        desc = "",
        space = "4.66 GB"
    ),
    Model(
        model = File(
            context.getExternalFilesDir(null), "meta-llama-3.1-8b-abliterated-Q4_0_4_8.gguf"
        ),
        name = "Meta Llama 3.1 8B Abliterated",
        url = Uri.parse("https://hf-mirror.com/flyfishstudio/meta-llama-3.1-8b-instruct-abliterated-Q4_0_4_8.gguf/resolve/main/meta-llama-3.1-8b-instruct-abliterated-Q4_0_4_8.gguf"),
        desc = "",
        space = "4.66 GB"
    ),
    Model(
        model = File(context.getExternalFilesDir(null), "Hermes-3-Llama-3.1-8B-Q4_0_4_8.gguf"),
        name = "Hermes 3 Llama 3.1 8B",
        url = Uri.parse("https://hf-mirror.com/flyfishstudio/Hermes-3-Llama-3.1-8B-Q4_0_4_8.gguf/resolve/main/Hermes-3-Llama-3.1-8B-Q4_0_4_8.gguf"),
        desc = "",
        space = "4.66 GB"
    ),
    Model(
        model = File(
            context.getExternalFilesDir(null), "Llama-3.1-8B-Lexi-Uncensored_V2-Q4_0_4_8.gguf"
        ),
        name = "Llama 3.1 8B Lexi Abliterated V2",
        url = Uri.parse("https://hf-mirror.com/dabr1/Llama-3.1-8B-Lexi-Uncensored_V2-Q4_0_4_8.gguf/resolve/main/Llama-3.1-8B-Lexi-Uncensored_V2-Q4_0_4_8.gguf"),
        desc = "",
        space = "4.66 GB"
    ),
    Model(
        model = File(
            context.getExternalFilesDir(null),
            "DarkIdol-Llama-3.1-8B-Instruct-1.2-Uncensored-Q4_0_4_8.gguf"
        ),
        name = "DarkIdol Llama 3.1 8B 1.2 Abliterated",
        url = Uri.parse("https://hf-mirror.com/dabr1/DarkIdol-Llama-3.1-8B-Instruct-1.2-Uncensored-Q4_0_4_8.gguf/resolve/main/DarkIdol-Llama-3.1-8B-Instruct-1.2-Uncensored-Q4_0_4_8.gguf"),
        desc = "",
        space = "4.66 GB"
    ),
    Model(
        model = File(
            context.getExternalFilesDir(null), "gemma-2-9b-it-abliterated-Q4_0_4_8.gguf"
        ),
        name = "Gemma 2 9B Abliterated",
        url = Uri.parse("https://hf-mirror.com/flyfishstudio/gemma-2-9b-it-abliterated-Q4_0_4_8.gguf/resolve/main/gemma-2-9b-it-abliterated-Q4_0_4_8.gguf"),
        desc = "",
        space = "5.44 GB"
    ),
    Model(
        model = File(context.getExternalFilesDir(null), "Gemma-2-Ataraxy-9B-Q4_0_4_8.gguf"),
        name = "Gemma 2 Ataraxy 9B",
        url = Uri.parse("https://hf-mirror.com/flyfishstudio/Gemma-2-Ataraxy-9B-Q4_0_4_8.gguf/resolve/main/Gemma-2-Ataraxy-9B-Q4_0_4_8.gguf"),
        desc = "",
        space = "5.44 GB"
    ),
)