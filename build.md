# Build

## Prerequisite

- JDK `21`
- Gradle `8.10`
- Android SDK `35`
- Android NDK `27.1.12297006`
- CMake `3.22.1`

Use latest Android Studio to automatically manage these requirements, or build with GitHub Actions.

## SVE supported SoCs

Some SoCs support
SVE [gpages.juszkiewicz.com.pl/arm-socs-table/arm-socs.html](https://gpages.juszkiewicz.com.pl/arm-socs-table/arm-socs.html)
, for example, Snapdragon 8 Gen 1, Dimensity 9000 and Tensor G3, but later they removed SVE support.
Force enabling SVE would cause a crash on newer devices, including 8 Gen 2 and Dimensity 9300. To
ensure compatibility, Lain compiled with SVE disabled (+nosve). If you want to enable SVE,
you can remove `+nosve` and replace to `+sve`, then load Q4_0_8_8 quantized models.

## Non-ARM64 device

Lain works on these devices but disabled them intended for performance-oriented design.

1. Add or replace `abiFilters += listOf("arm64-v8a")` to your desired platform.
2. Check list `val flags` and change `-march` for your platform.

## Build Variants

Different build variants only for preloaded model list, without any other changes.

Build with `./gradlew buildDefaultFossRelease` or `./gradlew buildDefaultFossDebug` to get the APK.

- Default version includes all models, including uncensored ones.
- Lite version does not include any uncensored models, to ensure compliance with Google Play
  policies.
- China version includes all models, but use HF-Mirror, a Hugging Face mirror in China, to download
  models.