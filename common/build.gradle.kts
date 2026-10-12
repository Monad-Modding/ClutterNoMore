plugins {
    id("clutternomore.common")
    id("net.fabricmc.fabric-loom")
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    compileOnly("net.fabricmc:fabric-loader:${setting("fabric.loader")}")
    compileOnly("folk.sisby:kaleido-config:${setting("kaleido")}")
    compileOnly("mezz.jei:jei-$mcVersion-fabric:${integration("jei")}") { isTransitive = false }
    integration("rrv", "fabric")?.let { compileOnly("cc.cassian.rrv:reliable-recipe-viewer-fabric:$it") { isTransitive = false } }
    compileOnly("dev.isxander:controlify:${integration("controlify", "fabric")}") { isTransitive = false }
    compileOnly("maven.modrinth:mouse-tweaks:${integration("mousetweaks", "fabric")}")
}

loom {
    accessWidenerPath = accessWidener
}
