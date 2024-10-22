pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") {
            content { includeGroup("com.github.jeziellago") } // Markdown
        }
    }
}

plugins {
    id("de.fayard.refreshVersions") version "0.60.5"
}

rootProject.name = "Lain"

include(":app")