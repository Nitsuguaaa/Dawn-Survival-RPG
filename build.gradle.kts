plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
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

// 1. Ensure 'jar' task finishes by running the deployment
        tasks.named("jar") {
            finalizedBy("deployToTestServer")
        }

tasks.register<Copy>("deployToTestServer") {
    dependsOn(tasks.named("jar"))

    from(layout.buildDirectory.dir("libs"))

    val targetDir = file("C:/Users/AC/Desktop/DawnMC-26.1/plugins")
    into(targetDir)

    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    // Create local references to prevent capturing the Gradle script object
    val sourcePath = layout.buildDirectory.dir("libs").get().asFile.absolutePath
    val destPath = targetDir.absolutePath

    doFirst {
        logger.lifecycle("deployToTestServer: copying from $sourcePath to $destPath")
    }
    doLast {
        logger.lifecycle("deployToTestServer: done. Files at $destPath")
    }
}

