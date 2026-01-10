import org.gradle.api.internal.artifacts.dependencies.DefaultImmutableVersionConstraint.strictly

plugins {
    id("net.neoforged.moddev") version "2.0.137"
    id("com.gradleup.shadow") version "9.3.0"
}
val modVersion = property("mod.version").toString()

group = project.property("maven_group").toString()
version = "${modVersion}+${stonecutter.current.project}"

repositories {
    mavenCentral()
    mavenLocal()
}

val minConfiguredVersion = "0.3"
val configuredVersion = "0.3.1"

val shade by configurations.creating

dependencies {
    // Configured
    listOf(
        "de.clickism:configured-core:${configuredVersion}",
        "de.clickism:configured-yaml:${configuredVersion}",
        "de.clickism:configured-json:${configuredVersion}",
        "de.clickism:configured-neoforge-command-adapter:${configuredVersion}"
    ).forEach {
        jarJar(implementation(it)!!) {
            strictly("[$minConfiguredVersion,)")
        }
    }
    // Configured Dependency
    jarJar(implementation("org.yaml:snakeyaml:2.0")!!)
}

neoForge {
    version = property("deps.neoforge").toString()

    runs {
        register("client") {
            client()
            gameDirectory = file("run/")
            ideName = "NeoForge Client (${stonecutter.active?.version})"
            programArgument("--username=ClickToPlay")
        }
        register("server") {
            server()
            gameDirectory = file("run/")
            ideName = "NeoForge Server (${stonecutter.active?.version})"
        }
    }

    mods {
        register(property("mod.id").toString()) {
            sourceSet(sourceSets["main"])
        }
    }
}

base {
    archivesName.set(property("archives_base_name").toString())
}

tasks.processResources {
    val properties = mapOf(
        "mod_version" to modVersion,
        "minecraft_version" to project.property("mod.minecraft_version"),
    )
    filesMatching(listOf("META-INF/neoforge.mods.toml", "META-INF/mods.toml")) {
        expand(properties)
    }
    inputs.properties(properties)
}

tasks.jarJar {
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    configurations = listOf(shade)
    archiveClassifier.set("")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    enableAutoRelocation = false
    mergeServiceFiles()
    // Stop Gson and Snakeyaml from being relocated
    val prefix = "de.clickism.clickmobs.shadow"
    relocate("de.clickism.configured", "$prefix.configured")
    relocate("org.yaml.snakeyaml", "$prefix.snakeyaml")
    dependencies {
        exclude(dependency("com.google.code.gson:gson"))
    }
}