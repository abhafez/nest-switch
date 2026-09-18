plugins {
    // Resolves the JDK 17 toolchain on any machine (CI included) instead of
    // relying on a JDK that happens to be installed locally.
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "nest-switch"
