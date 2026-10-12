plugins {
    java
}

group = "${mod("group")}.$platform"
version = "${mod("version")}+$mcVersion"

base {
    archivesName = "${mod("id")}-$platform"
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
    withSourcesJar()
}

repositories {
    mavenCentral()
    exclusiveContent {
        forRepository {
            maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
        }
        filter { includeGroup("org.parchmentmc.data") }
    }
    exclusiveContent {
        forRepository {
            maven("https://maven.fabricmc.net") { name = "Fabric" }
        }
        filter { includeGroupAndSubgroups("net.fabricmc") }
    }
    exclusiveContent {
        forRepository {
            maven("https://maven.blamejared.com") { name = "BlameJared" }
        }
        filter { includeGroup("mezz.jei") }
    }
    exclusiveContent {
        forRepository {
            maven("https://maven.terraformersmc.com/releases") { name = "TerraformersMC" }
        }
        filter {
            includeGroup("dev.emi")
            includeGroup("com.terraformersmc")
        }
    }
    exclusiveContent {
        forRepository {
            maven("https://maven.isxander.dev/releases") {
                name = "Xander"
                metadataSources {
                    mavenPom()
                    artifact()
                    ignoreGradleMetadataRedirection()
                }
            }
        }
        filter { includeGroup("dev.isxander") }
    }
    exclusiveContent {
        forRepository {
            maven("https://api.modrinth.com/maven") { name = "Modrinth" }
        }
        filter { includeGroup("maven.modrinth") }
    }
    exclusiveContent {
        forRepository {
            maven("https://repo.sleeping.town") { name = "Sleeping Town" }
        }
        filter { includeGroup("folk.sisby") }
    }
    exclusiveContent {
        forRepository {
            maven("https://maven.cassian.cc") { name = "Cassian" }
        }
        filter { includeGroupAndSubgroups("cc.cassian") }
    }
    exclusiveContent {
        forRepository {
            maven("https://maven.createmod.net") { name = "Create" }
        }
        filter { includeGroup("com.simibubi.create") }
    }
    exclusiveContent {
        forRepository {
            maven("https://maven.su5ed.dev/releases") { name = "Sinytra" }
        }
        filter {
            includeGroupAndSubgroups("org.sinytra")
            includeGroupAndSubgroups("dev.su5ed")
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = javaVersion
}

replaceSince("1.21.2", "DirectionProperty", "EnumProperty<Direction>")
replaceSince("1.21.11", "ResourceLocation", "Identifier")
replaceSince("1.21.11", "readResourceLocation", "readIdentifier")
replaceSince("1.21.11", "writeResourceLocation", "writeIdentifier")
replaceSince("1.21.11", "getKey().location()", "getKey().identifier()")
replaceSince("26", "GuiGraphics", "GuiGraphicsExtractor")
replaceSince("26", "blockOccudes", "blockOccludes")
replaceSince("26", "FabricDataOutput", "FabricPackOutput")
replaceSince("26.2", "net.minecraft.advancements.Criterion", "net.minecraft.advancements.triggers.Criterion")
replaceSince("26.2", "Minecraft.getInstance().screen", "Minecraft.getInstance().gui.screen()")
replaceSince("26.2", "Minecraft.getInstance().setScreen(", "Minecraft.getInstance().gui.setScreen(")
replaceSince("26.2", "minecraft.screen", "minecraft.gui.screen()")
