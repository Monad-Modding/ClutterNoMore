plugins {
    id("clutternomore.common")
    id("net.fabricmc.fabric-loom-remap")
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-$mcVersion:${versioned("parchment")}@zip")
    })
    modCompileOnly("net.fabricmc:fabric-loader:${setting("fabric.loader")}")
    compileOnly("folk.sisby:kaleido-config:${setting("kaleido")}")
    modCompileOnly("mezz.jei:jei-$mcVersion-fabric:${integration("jei")}") { isTransitive = false }
    integration("emi")?.let { modCompileOnly("dev.emi:emi-fabric:$it") { isTransitive = false } }
    modCompileOnly("dev.isxander:controlify:${integration("controlify", "fabric")}") { isTransitive = false }
    modCompileOnly("maven.modrinth:mouse-tweaks:${integration("mousetweaks", "fabric")}")
}

loom {
    accessWidenerPath = accessWidener
}
