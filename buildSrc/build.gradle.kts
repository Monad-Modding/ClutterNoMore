plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    maven("https://maven.kikugie.dev/releases") { name = "KikuGie" }
}

dependencies {
    implementation(libs.stonecutter)
    implementation(libs.mod.publish)
}
