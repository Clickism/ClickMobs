pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7.10"
}

rootProject.name = "ClickMobs"

include("paper", "mod")

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"
    create("mod") {
        versions("1.21.11", "1.21.10", "1.21.8", "1.21.5", "1.21.4", "1.21.1", "1.20.1")
        vcsVersion = "1.21.11"
    }
}