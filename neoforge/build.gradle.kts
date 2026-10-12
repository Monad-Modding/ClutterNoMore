plugins {
    id("clutternomore.loader")
    id("net.neoforged.moddev")
}

neoForge {
    version = versioned("neoforge")
    validateAccessTransformers = true
    accessTransformers.files.setFrom(rootProject.file("neoforge/src/main/resources/META-INF/accesstransformer.cfg"))
    versionedOrNull("parchment")?.let { mappings ->
        parchment {
            minecraftVersion = mcVersion
            mappingsVersion = mappings
        }
    }
    mods {
        register(mod("id")) {
            sourceSet(sourceSets.main.get())
        }
    }
    runs {
        register("client") {
            client()
        }
        register("server") {
            server()
        }
    }
}

dependencies {
    implementation("folk.sisby:kaleido-config:${setting("kaleido")}")
    jarJar("folk.sisby:kaleido-config:${setting("kaleido")}")
    if (!mcAtLeast("1.21.9")) {
        "additionalRuntimeClasspath"("folk.sisby:kaleido-config:${setting("kaleido")}")
    }
    compileOnly("mezz.jei:jei-$mcVersion-neoforge:${integration("jei")}") { isTransitive = false }
    integration("emi")?.let { compileOnly("dev.emi:emi-neoforge:$it") { isTransitive = false } }
    integration("rrv")?.let { compileOnly("cc.cassian.rrv:reliable-recipe-viewer-neoforge:$it") { isTransitive = false } }
    compileOnly("dev.isxander:controlify:${integration("controlify")}") { isTransitive = false }
    compileOnly("maven.modrinth:mouse-tweaks:${integration("mousetweaks")}")
    compileOnly("maven.modrinth:fractal-lib:${integration("fractal")}")
    integration("create")?.let { compileOnly("com.simibubi.create:create-$mcVersion:$it:slim") { isTransitive = false } }
    integration("item_group_api")?.let { compileOnly("org.sinytra.forgified-fabric-api:fabric-item-group-api-v1:$it") { isTransitive = false } }
    integration("creative_tab_api")?.let { compileOnly("net.fabricmc.fabric-api:fabric-creative-tab-api-v1:$it") { isTransitive = false } }
}
