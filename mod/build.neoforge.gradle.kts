plugins {
    id("net.neoforged.moddev") version "2.0.137"
}
val modVersion = property("mod.version").toString()

group = project.property("maven_group").toString()
version = "${modVersion}+${stonecutter.current.project}"

repositories {
    mavenCentral()
}

val configuredVersion = "0.3"

dependencies {
    // Configured
    implementation("de.clickism:configured-core:${configuredVersion}")
    implementation("de.clickism:configured-yaml:${configuredVersion}")
    implementation("de.clickism:configured-json:${configuredVersion}")
//    modImplementation(include("de.clickism:configured-fabric-command-adapter:${configuredVersion}")!!)
    // Configured Dependency
    implementation("org.yaml:snakeyaml:2.0")
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
//    sourceSets["main"].resources.srcDir("${rootDir}/versions/datagen/${stonecutter.current.version.split("-")[0]}/src/main/generated")
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