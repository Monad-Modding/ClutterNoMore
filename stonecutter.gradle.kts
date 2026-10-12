plugins {
    id("dev.kikugie.stonecutter")
    alias(libs.plugins.fabric.loom) apply false
    alias(libs.plugins.fabric.loom.remap) apply false
    alias(libs.plugins.moddev) apply false
    alias(libs.plugins.moddev.legacyforge) apply false
}

stonecutter active "1.21.1"

stonecutter tasks {
    order("publishModrinth") { branch.id != "common" }
    order("publishCurseforge") { branch.id != "common" }
}

for (version in stonecutter.versions.map { it.version }.distinct()) tasks.register("publish$version") {
    group = "publishing"
    dependsOn(stonecutter.tasks.named("publishMods") { metadata.version == version && branch.id != "common" })
}
