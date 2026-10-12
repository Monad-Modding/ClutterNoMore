plugins {
    id("clutternomore.base")
}

val commonJava = configurations.consumable("commonJava") {
    attributes {
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(COMMON_CATEGORY))
        attribute(commonContent, "java")
    }
}

val commonResources = configurations.consumable("commonResources") {
    attributes {
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(COMMON_CATEGORY))
        attribute(commonContent, "resources")
    }
}

afterEvaluate {
    val main = the<SourceSetContainer>().named("main").get()
    main.java.srcDirs.forEach { directory ->
        artifacts.add(commonJava.name, directory) { builtBy("stonecutterGenerate") }
    }
    main.resources.srcDirs.forEach { directory ->
        artifacts.add(commonResources.name, directory) { builtBy("stonecutterGenerate") }
    }
    if (generatedResources.isDirectory) {
        artifacts.add(commonResources.name, generatedResources)
    }
}
