plugins {
    // Auto-provisions the JDK 21 toolchain for devs who don't have it installed
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "intellij-spicedb-plugin"
