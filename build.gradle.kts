import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.19.0"
    id("org.jetbrains.grammarkit") version "2022.3.2.2"
}

group = "community.spicedb"
version = "2.0.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create("IC", "2024.3.4.1")
        testFramework(TestFrameworkType.Platform)
    }
    testImplementation("junit:junit:4.13.2")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

sourceSets {
    main {
        java.srcDirs("src/main/gen")
    }
}

tasks {
    generateLexer {
        sourceFile.set(file("src/main/grammars/SpiceDb.flex"))
        targetOutputDir.set(file("src/main/gen/com/authzed/intellij/spicedb/lexer"))
        purgeOldFiles.set(true)
    }

    generateParser {
        sourceFile.set(file("src/main/grammars/SpiceDb.bnf"))
        targetRootOutputDir.set(file("src/main/gen"))
        pathToParser.set("com/authzed/intellij/spicedb/parser/SpiceDbParser.java")
        pathToPsiRoot.set("com/authzed/intellij/spicedb/psi")
        purgeOldFiles.set(false)
    }

    compileJava {
        dependsOn(generateLexer, generateParser)
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "243"
        }
    }
    buildSearchableOptions = false
}
