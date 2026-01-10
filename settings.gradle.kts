pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.8.2"
}

rootProject.name = "ClickMobs"

include("paper", "mod")

stonecutter {
    kotlinController = true
    create("mod") {
        fun version(version: String, vararg loaders: String) {
            loaders.forEach {
                this.version("$version-$it", version)
                    .buildscript = "build.$it.gradle.kts"
            }
        }
        version("1.20.1", "fabric")
        version("1.21.1", "fabric")
        version("1.21.4", "fabric")
        version("1.21.5", "fabric")
        version("1.21.8", "fabric")
        version("1.21.10", "fabric")
        version("1.21.11", "fabric", "neoforge")
        vcsVersion = "1.21.11-fabric"
    }
}