plugins {
	id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT"
	id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}
val modVersion = property("mod.version").toString()
val minecraftVersion = stonecutter.current.project.substringBeforeLast('-')
val loader = stonecutter.current.project.substringAfterLast('-').substringBeforeLast('+')

version = "$modVersion+$minecraftVersion-$loader"
group = project.property("maven_group").toString()

base {
	archivesName.set(property("archives_base_name").toString())
}

repositories {
	mavenCentral()
	mavenLocal()
}

val configuredVersion = "0.3.2"

dependencies {
	minecraft("com.mojang:minecraft:${minecraftVersion}")
	implementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
	implementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
	// Configured
	implementation(include("de.clickism:configured-core:${configuredVersion}")!!)
	implementation(include("de.clickism:configured-yaml:${configuredVersion}")!!)
	implementation(include("de.clickism:configured-json:${configuredVersion}")!!)
	implementation(include("de.clickism:configured-fabric-noremap-command-adapter:${configuredVersion}")!!)
	// Configured Dependency
	implementation(include("org.yaml:snakeyaml:2.0")!!)
}

stonecutter {
	replacements {
		string(current.parsed < "1.21.11") {
			replace("Identifier", "ResourceLocation")
		}
	}
}

tasks.processResources {
	val props = mapOf(
		"mod_version" to version,
		"minecraft_version" to project.property("mod.minecraft_version"),
		"fabric_loader_version" to project.property("deps.fabric_loader")
	)
	filesMatching("fabric.mod.json") {
		expand(props)
	}
	inputs.properties(props)
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(25))
	}
	val javaVersion = JavaVersion.VERSION_25
	sourceCompatibility = javaVersion
	targetCompatibility = javaVersion
}

tasks.jar {
	from("LICENSE") {
		rename { "${it}_${project.base.archivesName.get()}" }
	}
}

publishMods {
	displayName.set("ClickMobs ${property("mod.version")} for Fabric")
	file.set(tasks.jar.get().archiveFile)
	version.set(project.version.toString())
	changelog.set(rootProject.file("mod/CHANGELOG.md").readText())
	type.set(STABLE)
	modLoaders.add("fabric")
	val mcVersions = property("mod.publishing_target_minecraft_versions").toString().split(',')
	modrinth {
		accessToken.set(System.getenv("MODRINTH_TOKEN"))
		projectId.set("tRdRT5jS")
		requires("fabric-api")
		minecraftVersions.addAll(mcVersions)
	}
	curseforge {
		accessToken.set(System.getenv("CURSEFORGE_TOKEN"))
		projectId.set("1179556")
		clientRequired.set(false)
		serverRequired.set(true)
		requires("fabric-api")
		minecraftVersions.addAll(mcVersions)
	}
}

loom {
	runConfigs.all {
		ideConfigGenerated(true)
		runDir = "../../run"
		if (environment == "client") {
			programArgs("--username=ClickToPlay")
		}
	}
}