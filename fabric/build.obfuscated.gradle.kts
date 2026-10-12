plugins {
    id("clutternomore.loader")
    id("net.fabricmc.fabric-loom-remap")
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-$mcVersion:${versioned("parchment")}@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:${setting("fabric.loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${versioned("fabric_api")}")
    implementation("folk.sisby:kaleido-config:${setting("kaleido")}")
    include("folk.sisby:kaleido-config:${setting("kaleido")}")
    modCompileOnly("mezz.jei:jei-$mcVersion-fabric:${integration("jei")}") { isTransitive = false }
    integration("emi")?.let { modCompileOnly("dev.emi:emi-fabric:$it") { isTransitive = false } }
    modCompileOnly("dev.isxander:controlify:${integration("controlify")}") { isTransitive = false }
    modCompileOnly("maven.modrinth:mouse-tweaks:${integration("mousetweaks")}")
    modCompileOnly("com.terraformersmc:modmenu:${integration("modmenu")}") { isTransitive = false }
    modCompileOnly("maven.modrinth:fractal-lib:${integration("fractal")}")
    integration("create")?.let { modCompileOnly("maven.modrinth:create-fabric:$it") }
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
