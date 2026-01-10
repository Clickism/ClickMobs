import org.gradle.api.internal.artifacts.dependencies.DefaultImmutableVersionConstraint.strictly

plugins {
    id("net.neoforged.moddev") version "2.0.137"
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

stonecutter {
    replacements {
        string(current.parsed < "1.21.11") {
            replace("Identifier", "ResourceLocation")
        }
    }
}

tasks.processResources {
    val properties = mapOf(
        "mod_version" to modVersion,
        "minecraft_version_range" to project.property("mod.minecraft_version_range"),
        "minecraft_version" to project.property("mod.minecraft_version"),
    )
    filesMatching(listOf("META-INF/neoforge.mods.toml", "META-INF/mods.toml")) {
        expand(properties)
    }
    inputs.properties(properties)
}


publishMods {
    displayName.set("ClickMobs ${property("mod.version")} for Neoforge")
    file.set(tasks.remapJar.get().archiveFile)
    version.set(project.version.toString())
    changelog.set(rootProject.file("mod/CHANGELOG.md").readText())
    type.set(STABLE)
    modLoaders.add("neoforge")
    val mcVersions = property("mod.publishing_target_minecraft_versions").toString().split(',')
    modrinth {
        accessToken.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set("tRdRT5jS")
        minecraftVersions.addAll(mcVersions)
    }
    curseforge {
        accessToken.set(System.getenv("CURSEFORGE_TOKEN"))
        projectId.set("1179556")
        clientRequired.set(false)
        serverRequired.set(true)
        minecraftVersions.addAll(mcVersions)
    }
}
