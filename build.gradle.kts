plugins {
    id("java")
    kotlin("jvm") version "1.9.25"
    id("org.jetbrains.intellij.platform") version "2.1.0"
}

group = "dev.hafez"
version = "1.0.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        // Compiles and runs against plain Community — the plugin only uses core
        // platform APIs (FileIconProvider, GotoRelatedProvider, VFS), so it works
        // in every IntelliJ Platform IDE, no JS/TS plugin dependency needed.
        create("IC", "2023.3")
        instrumentationTools()
    }
    testImplementation("junit:junit:4.13.2")
}

tasks.test {
    useJUnit()
}

kotlin {
    jvmToolchain(17)
}

intellijPlatform {
    pluginConfiguration {
        version.set(project.version.toString())
        ideaVersion {
            sinceBuild.set("233")
            untilBuild.set(provider { null })
        }
    }
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    buildSearchableOptions {
        enabled = false
    }
}
