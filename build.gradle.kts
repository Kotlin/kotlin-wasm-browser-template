@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.Companion.fromVersion
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

group = "me.user"
version = "1.0-SNAPSHOT"

val kotlin_repo_url: String? = project.properties["kotlin_repo_url"] as String?
val language_version: String? = project.properties["language_version"] as String?

repositories {
    mavenCentral()

    kotlin_repo_url?.also { maven(it) }
}

kotlin {
    wasmJs {
        binaries.executable()
        browser()

        compilations.configureEach {
            compileTaskProvider.configure {
                language_version?.let {
                    compilerOptions.languageVersion.set(fromVersion(it))
                }
            }
        }
    }

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        wasmJsMain.dependencies {
            implementation(libs.kotlinx.browser)
        }
    }
}
