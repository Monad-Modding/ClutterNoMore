plugins {
    id("clutternomore.loader")
    id("net.fabricmc.fabric-loom")
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    implementation("net.fabricmc:fabric-loader:${setting("fabric.loader")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${versioned("fabric_api")}")
    implementation("folk.sisby:kaleido-config:${setting("kaleido")}")
    include("folk.sisby:kaleido-config:${setting("kaleido")}")
    compileOnly("mezz.jei:jei-$mcVersion-fabric:${integration("jei")}") { isTransitive = false }
    integration("rrv")?.let { compileOnly("cc.cassian.rrv:reliable-recipe-viewer-fabric:$it") { isTransitive = false } }
    compileOnly("dev.isxander:controlify:${integration("controlify")}") { isTransitive = false }
    compileOnly("maven.modrinth:mouse-tweaks:${integration("mousetweaks")}")
    compileOnly("com.terraformersmc:modmenu:${integration("modmenu")}") { isTransitive = false }
    compileOnly("maven.modrinth:fractal-lib:${integration("fractal")}")
}

loom {
    accessWidenerPath = accessWidener
    runs {
        register("data") {
            inherit(getByName("server"))
            name("Data Generation")
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${generatedResources.absolutePath}")
            vmArg("-Dfabric-api.datagen.modid=${mod("id")}")
            runDir("build/datagen")
        }
    }
}

tasks.named<ProcessResources>("processResources") {
    from(accessWidener) {
        rename { "${mod("id")}.accesswidener" }
    }
}
