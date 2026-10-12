pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie" }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.kikugie.stonecutter") version "0.9.8"
}

stonecutter {
    create(rootProject) {
        vcsVersion = "1.21.1"

        branch("common") {
            versions("1.20.1", "1.21.1").buildscript("build.obfuscated.gradle.kts")
            versions("26.1.2", "26.2", "26.3")
        }
        branch("fabric") {
            versions("1.20.1", "1.21.1").buildscript("build.obfuscated.gradle.kts")
            versions("26.1.2", "26.2", "26.3")
        }
        branch("neoforge") {
            versions("1.21.1", "26.1.2", "26.2", "26.3")
        }
        branch("forge") {
            versions("1.20.1")
        }
    }
}

rootProject.name = "ClutterNoMore"
