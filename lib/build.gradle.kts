import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    // alias(libs.plugins.kotlin.native.cocoapods)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.library)
    alias(libs.plugins.binary.compatibility)

    `maven-publish`
}

group = "dev.zt64.compose.pdf"
version = "1.0.0"
description = "Compose multiplatform PDF implementation"

kotlin {
    jvmToolchain(17)

    jvm("desktop")
    androidTarget {
        publishAllLibraryVariants()
    }
    // ios()

    explicitApi()

    // cocoapods {
    //     version = "1.0.0"
    //     summary = "Some description for the Shared Module"
    //     ios.deploymentTarget = "14.1"
    //     framework {
    //         baseName = "shared"
    //         isStatic = true
    //     }
    // }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.foundation)
                implementation(compose.runtime)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(compose.desktop.uiTestJUnit4)
                implementation(compose.desktop.currentOs)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation(libs.icepdf.core)
            }
        }

        // val iosMain by getting

        all {
            languageSettings {
                enableLanguageFeature("ContextReceivers")
            }
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        // if (project.findProperty("composeCompilerReports") == "true") {
            freeCompilerArgs += arrayOf(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                        project.layout.buildDirectory.get().asFile.absolutePath + "/compose_compiler"
            )
        // }
        // if (project.findProperty("composeCompilerMetrics") == "true") {
            freeCompilerArgs += arrayOf(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                        project.layout.buildDirectory.get().asFile.absolutePath + "/compose_compiler"
            )
        // }
    }
}

android {
    namespace = "dev.zt64.compose.pdf"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/zt64/compose-pdf")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}