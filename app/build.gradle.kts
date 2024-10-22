plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.flyfish233.lain"
    compileSdk = 35

    ndkVersion = "28.0.12433566"

    defaultConfig {
        applicationId = "com.flyfish233.lain"
        minSdk = 31 // It can be downgraded tho but strongly recommend you to run it on decent CPU
        targetSdk = 35

        versionCode = 1
        val version = "0.0.1"
        val llama = "b3958"
        versionName = "$version-$llama"

        ndk {
            // We only support arm-64 device for best performance, also below cmake flags
            // noinspection ChromeOsAbiSupport
            abiFilters += listOf("arm64-v8a")
        }

        externalNativeBuild {
            cmake {
                val flags = listOf(
                    "-march=armv8.4-a+fp16+dotprod+i8mm+nosve", // nosve: For latest SoCs
                    "-fvisibility=hidden",
                    "-fvisibility-inlines-hidden",
                    "-Ofast",
                    "-flto",
                    "-funroll-loops",
                    "-fno-rtti",
                    "-fno-stack-protector",
                    "-ffixed-x18",
                    "-Wl"
                )
                arguments += "-DCMAKE_C_FLAGS=${flags.joinToString(" ")}"

                // cmake : do not build common library by default when standalone (#9804)
                arguments += "-DLLAMA_BUILD_COMMON=ON"

                arguments += "-DCMAKE_C_VISIBILITY_PRESET=hidden"
                arguments += "-DCMAKE_CXX_VISIBILITY_PRESET=hidden"

                arguments += "-DCMAKE_BUILD_TYPE=Release"
                arguments += "-DGGML_LTO=ON"

                arguments += "-DGGML_LLAMAFILE=OFF" // Use NEON
                arguments += "-DGGML_CCACHE=OFF"

                arguments += "-DGGML_ALL_WARNINGS=OFF"
                arguments += "-DLLAMA_ALL_WARNINGS=OFF"

                arguments += "-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON"

                // arguments += "-DGGML_VULKAN=ON"
            }
        }
    }

    flavorDimensions += listOf("model")
    productFlavors {
        create("defaultFoss") {
            dimension = "model"
        }
        create("lite") {
            dimension = "model"
            versionNameSuffix = " L"
        }
        create("mirrorChina") {
            dimension = "model"
            versionNameSuffix = " C"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/**"
            excludes += "/DebugProbesKt.bin"
        }
    }

    buildFeatures {
        compose = true
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    kotlin {
        jvmToolchain(21)
    }

    sourceSets { // Read `src/README.md`
        getByName("defaultFoss") {
            java.srcDir("src/oss/kotlin")
        }
        getByName("lite") {
            java.srcDir("src/lite/kotlin")
        }
        getByName("mirrorChina") {
            java.srcDir("src/china/kotlin")
        }
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    runtimeOnly(libs.compat)
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(libs.ui)
    implementation(libs.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.tts)
    implementation(libs.markdown) {
        exclude(group = "androidx.appcompat")
        exclude(group = "com.squareup.okio")
    }
    // debugImplementation(libs.leakcanary)
}
