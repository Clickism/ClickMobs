plugins {
	id("fabric-loom") version "1.9-SNAPSHOT"
}

version = "${parent?.name}-${property("mod.version")}+${stonecutter.current.project}"
group = project.property("maven_group").toString()

base {
	archivesName.set(property("archives_base_name").toString())
}

dependencies {
	minecraft("com.mojang:minecraft:${stonecutter.current.project}")
	mappings("net.fabricmc:yarn:${property("deps.yarn_mappings")}:v2")
	modImplementation("net.fabricmc:fabric-loader:${property("deps.loader_version")}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_version")}")
}

tasks.processResources {
	val props = mapOf(
		"version" to version,
		"targetVersion" to project.property("mod.mc_version"),
		"minecraftVersion" to stonecutter.current.version,
		"fabricVersion" to project.property("deps.loader_version")
	)
	filesMatching("fabric.mod.json") {
		expand(props)
	}
	inputs.properties(props)
}

java {
	val j21 = stonecutter.eval(stonecutter.current.version, ">=1.20.5")
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(if (j21) 21 else 17))
	}
	val javaVersion = if (j21) JavaVersion.VERSION_17 else JavaVersion.VERSION_17
	sourceCompatibility = javaVersion
	targetCompatibility = javaVersion
}

tasks.jar {
	from("LICENSE") {
		rename { "${it}_${project.base.archivesName.get()}" }
	}
}

loom {
	runConfigs.all {
		ideConfigGenerated(true)
		runDir = "../../run"
	}
}