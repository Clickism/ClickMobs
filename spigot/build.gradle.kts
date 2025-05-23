plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
    id("io.github.patrick.remapper") version "1.4.2"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

val pluginVersion = property("plugin_version").toString()
version = "$name-$pluginVersion"

group = "me.clickism"

base {
    archivesName.set(project.property("archives_base_name").toString())
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "spigotmc-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.20.1-R0.1-SNAPSHOT:remapped-mojang")
    compileOnly("org.jetbrains:annotations:22.0.0")
    implementation("me.clickism:configured:0.1")
}

val targetJavaVersion = 17
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.runServer {
    dependsOn(tasks.remap)
    minecraftVersion("1.21.5")
}

tasks.remap {
    version.set("1.20.1")
}

tasks.jar {
    enabled = false
}

tasks.build {
    dependsOn(tasks.remap)
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    archiveClassifier.set("")
    isEnableRelocation = true
    relocationPrefix = "me.clickism.shadow"
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.processResources {
    val props = mapOf("version" to pluginVersion)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}