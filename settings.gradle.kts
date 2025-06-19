pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.5"
}

rootProject.name = "ClickMobs"

include("spigot", "fabric")

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"
    create("fabric") {
        versions("1.21.6", "1.21.5", "1.21.4", "1.21.1", "1.20.1")
        vcsVersion = "1.21.6"
    }
}