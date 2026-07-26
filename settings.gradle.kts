pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://teavm.org/maven/repository")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "cfr"
