import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.Companion.fromVersion
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
}

val kotlin_repo_url: String? = project.properties["kotlin_repo_url"] as String?
val language_version: String? = project.properties["language_version"] as String?

repositories {
    mavenCentral()

    kotlin_repo_url?.also { maven(it) }
}

kotlin {
    wasmJs {
        binaries.executable()
        browser {
            commonWebpackConfig {
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        add(project.rootDir.path)
                    }
                }
            }
        }

        compilations.configureEach {
            compileTaskProvider.configure {
                language_version?.let {
                    compilerOptions.languageVersion.set(fromVersion(it))
                }
            }
        }
    }

    sourceSets {
        val wasmJsTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}