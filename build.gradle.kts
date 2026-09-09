import org.teavm.gradle.api.JSModuleType
import org.teavm.gradle.api.OptimizationLevel

plugins {
    `java-library`
    alias(libs.plugins.teavm) // order matters?
}

val thisVersion = "0.1.6"

group = "run.slicer"
version = "$thisVersion-${libs.versions.cfr.get()}"
description = "A JavaScript port of the CFR decompiler."

repositories {
    mavenCentral()
    maven("https://teavm.org/maven/repository")
}

dependencies {
    api(libs.cfr)
    teavmAnnotationProcessor(libs.teavm.extension.annotation.processor) // hmm?
    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java.toolchain {
    languageVersion = JavaLanguageVersion.of(21)
}

val debugging = false // set to true if you want an unobfuscated build for debugging
teavm {
    js {
        mainClass = "run.slicer.cfr.Main"
        moduleType = JSModuleType.ES2015
        obfuscated = !debugging
        if (debugging) {
            optimization = OptimizationLevel.NONE
        }
    }

    wasmGC {
        mainClass = "run.slicer.cfr.Main"
        modularRuntime = true
        obfuscated = !debugging
        disassembly = debugging
        if (debugging) {
            optimization = OptimizationLevel.NONE
        }
    }
}

tasks {
    disasmWasmGC {
        html = false
    }

    register<Copy>("copyDist") {
        group = "build"
        description = "Copies the necessary files to the dist directory for distribution."
        dependsOn(generateJavaScript)

        from(
            "README.md", "LICENSE", "LICENSE-CFR", "cfr.js", "cfr.d.ts",
            generateWasmGC, copyWasmGCRuntime
        )
        into("dist")

        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        doLast {
            copy {
                from(generateJavaScript)
                into("dist")
                rename("cfr.js", "cfr.runtime.js")

                duplicatesStrategy = DuplicatesStrategy.INCLUDE
            }
        }
        doLast {
            file("dist/package.json").writeText(
                """
                    {
                      "name": "@run-slicer/cfr",
                      "version": "${project.version}",
                      "description": "A JavaScript port of the CFR decompiler (https://github.com/leibnitz27/cfr).",
                      "main": "cfr.js",
                      "types": "cfr.d.ts",
                      "keywords": [
                        "decompiler",
                        "java",
                        "decompilation",
                        "cfr"
                      ],
                      "author": "run-slicer",
                      "license": "MIT"
                    }
                """.trimIndent()
            )
        }
    }

    test {
        useJUnitPlatform()

        dependsOn("copyDist") // comment out if you want to edit the output and test
    }

    build {
        dependsOn("copyDist")
    }

    clean {
        delete("dist")
    }
}
