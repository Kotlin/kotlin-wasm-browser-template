import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.Companion.fromVersion
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

group = "me.user"
version = "1.0-SNAPSHOT"

val kotlin_repo_url: String? = project.properties["kotlin_repo_url"] as String?
val kotlinLanguageVersionOverride = providers.gradleProperty("kotlin_language_version")
    .map(org.jetbrains.kotlin.gradle.dsl.KotlinVersion::fromVersion)
    .orNull
val kotlinApiVersionOverride = providers.gradleProperty("kotlin_api_version")
    .map(org.jetbrains.kotlin.gradle.dsl.KotlinVersion::fromVersion)
    .orNull
val kotlinAdditionalCliOptions = providers.gradleProperty("kotlin_additional_cli_options")
    .map { it.split(" ") }
    .orNull

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
                if (kotlinLanguageVersionOverride != null) {
                    compilerOptions {
                        languageVersion.set(kotlinLanguageVersionOverride)
                        logger.info("<KUP> ${this@configure.path} : set LV to $kotlinLanguageVersionOverride")
                    }
                }
                if (kotlinApiVersionOverride != null) {
                    compilerOptions {
                        apiVersion.set(kotlinApiVersionOverride)
                        logger.info("<KUP> ${this@configure.path} : set APIV to $kotlinApiVersionOverride")
                    }
                }
                if (kotlinAdditionalCliOptions != null) {
                    compilerOptions {
                        freeCompilerArgs.addAll(kotlinAdditionalCliOptions)
                        logger.info(
                            "<KUP> ${this@configure.path} : added ${
                                kotlinAdditionalCliOptions.joinToString(
                                    " "
                                )
                            }"
                        )
                    }
                }
                compilerOptions {
                    // output reported warnings even in the presence of reported errors
                    freeCompilerArgs.add("-Xreport-all-warnings")
                    logger.info("<KUP> ${this@configure.path} : added -Xreport-all-warnings")
                    // output kotlin.git-searchable names of reported diagnostics
                    freeCompilerArgs.add("-Xrender-internal-diagnostic-names")
                    logger.info("<KUP> ${this@configure.path} : added -Xrender-internal-diagnostic-names")
                    freeCompilerArgs.add("-Wextra")
                    logger.info("<KUP> ${this@configure.path}: added -Wextra")
                    freeCompilerArgs.add("-Xuse-fir-experimental-checkers")
                    logger.info("<KUP> ${this@configure.path}: added -Xuse-fir-experimental-checkers")
                }
            }
        }
    }

    sourceSets {
      val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val wasmJsMain by getting {
            dependencies {
                implementation(libs.kotlinx.browser)
            }
        }
        val wasmJsTest by getting
    }
}
