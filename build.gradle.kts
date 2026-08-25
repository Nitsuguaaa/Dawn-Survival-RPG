plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.fancyinnovations.com/releases")
    maven ("https://repo.nightexpressdev.com/releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
    compileOnly("de.oliver:FancyNpcs:2.9.2")
    compileOnly("su.nightexpress.excellenteconomy:ExcellentEconomy:2.8.0")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("26.1.2")
        jvmArgs("-Xms2G", "-Xmx2G")
    }

    processResources {
        val props = mapOf("version" to version)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

// Define your paths cleanly at the top
val alphaServerPath = "C:/Users/AC/Desktop/DawnMC-26.1/plugins/DawnBuilds"
val testServerPath = "C:/Users/AC/Desktop/DawnSMP-26.1/plugins"

// Task 1: Deploy to the heavy Alpha Server
tasks.register<Copy>("deployToAlpha") {
    dependsOn(tasks.named("jar"))
    from(layout.buildDirectory.dir("libs"))
    into(alphaServerPath)
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    // Capture paths as local variables to avoid Configuration Cache issues
    val destPath = alphaServerPath

    doLast {
        logger.lifecycle("✅ Deployed to Alpha Server: $destPath")
    }
}

// Task 2: Deploy to the lightweight Test Server
tasks.register<Copy>("deployToTest") {
    dependsOn(tasks.named("jar"))
    from(layout.buildDirectory.dir("libs"))
    into(testServerPath)
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    // Capture paths as local variables to avoid Configuration Cache issues
    val destPath = testServerPath

    doLast {
        logger.lifecycle("✅ Deployed to Test Server: $destPath")
    }
}

// Master Task: Deploys to both simultaneously
tasks.register("deployAll") {
    dependsOn("deployToAlpha", "deployToTest")
    doLast {
        logger.lifecycle("🚀 Deployed to ALL servers!")
    }
}

// Ensure building the jar automatically deploys to BOTH servers
tasks.named("jar") {
    finalizedBy("deployAll")
}

