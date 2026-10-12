plugins {
    id("clutternomore.base")
    id("me.modmuss50.mod-publish-plugin")
}

val common: Project = project(":common:${stonecutterBuild.current.project}")

val commonSource = configurations.dependencyScope("commonSource")

val commonJava = configurations.resolvable("commonJava") {
    extendsFrom(commonSource.get())
    attributes {
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(COMMON_CATEGORY))
        attribute(commonContent, "java")
    }
}

val commonResources = configurations.resolvable("commonResources") {
    extendsFrom(commonSource.get())
    attributes {
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(COMMON_CATEGORY))
        attribute(commonContent, "resources")
    }
}

dependencies {
    add(commonSource.name, project(common.path))
    compileOnly(project(common.path))
}

val loaderClassPrefix: String = when (platform) {
    "neoforge" -> "NeoForge"
    else -> platform.replaceFirstChar { it.uppercase() }
}

val metadata: Map<String, String> = mapOf(
    "id" to mod("id"),
    "name" to mod("name"),
    "version" to version.toString(),
    "group" to mod("group"),
    "authors" to mod("authors"),
    "license" to mod("license"),
    "description" to mod("description"),
    "java" to javaVersion.toString(),
    "fabric_loader" to setting("fabric.loader"),
    "fabric_minecraft" to (versionedOrNull("fabric_minecraft") ?: ""),
    "neoforge_minecraft" to (versionedOrNull("neoforge_minecraft") ?: ""),
    "neoforge_range" to (versionedOrNull("neoforge_range") ?: ""),
    "neoforge_loader" to (versionedOrNull("neoforge_loader") ?: ""),
    "forge_minecraft" to (versionedOrNull("forge_minecraft") ?: ""),
    "forge_loader" to (versionedOrNull("forge_loader") ?: ""),
    "mixin_plugin" to "${mod("group")}.$platform.${loaderClassPrefix}MixinPlugin",
)

tasks.named<JavaCompile>("compileJava") {
    dependsOn(commonJava)
    source(commonJava)
}

val mainClasses: FileCollection = the<SourceSetContainer>().named("main").get().output.classesDirs
val mixinRefmap: String? = if (platform == "forge") refmap else null

tasks.named<ProcessResources>("processResources") {
    dependsOn(commonResources, "compileJava")
    from(commonResources)
    exclude(".cache/**")
    inputs.properties(metadata)
    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml", "META-INF/mods.toml", "pack.mcmeta", "*.mixins.json")) {
        expand(metadata)
    }
    if (!mcAtLeast("1.21")) {
        includeEmptyDirs = false
        eachFile {
            path = pluralDataPath(path)
        }
    }
    doLast {
        destinationDir.walkTopDown().filter { it.isFile && it.name.endsWith(".mixins.json") }.forEach { pruneMixinConfig(it, mainClasses, mixinRefmap) }
    }
}

tasks.named<Jar>("sourcesJar") {
    dependsOn(commonJava, commonResources)
    from(commonJava)
    from(commonResources)
}

val loaderName: String = when (platform) {
    "neoforge" -> "Neoforge"
    else -> platform.replaceFirstChar { it.uppercase() }
}

publishMods {
    type = if (platform == "forge") BETA else STABLE
    displayName = "${mod("name")} ${mod("version")} for $mcVersion $loaderName"
    version = "${mod("version")}+$mcVersion-$platform"
    changelog = provider { rootProject.file("CHANGELOG.md").readText() }
    modLoaders.add(platform)

    modrinth {
        projectId = setting("publish.modrinth")
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.add(mcVersion)
        minecraftVersions.addAll(additionalVersions)
        if (platform == "fabric") {
            requires("fabric-api")
        }
    }

    curseforge {
        projectId = setting("publish.curseforge")
        accessToken = providers.environmentVariable("CURSEFORGE_API_KEY")
        minecraftVersions.add(mcVersion)
        minecraftVersions.addAll(additionalVersions)
        clientRequired = true
        serverRequired = true
        if (platform == "fabric") {
            requires("fabric-api")
        }
    }
}

afterEvaluate {
    val releaseJar = (tasks.findByName("remapJar") ?: tasks.findByName("reobfJar") ?: tasks.getByName("jar")) as AbstractArchiveTask
    val releaseSources = (tasks.findByName("remapSourcesJar") ?: tasks.getByName("sourcesJar")) as AbstractArchiveTask
    publishMods {
        file = releaseJar.archiveFile
        additionalFiles.from(releaseSources.archiveFile)
    }
}
