import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    // alias(libs.plugins.kotlin.native.cocoapods)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.library)
    alias(libs.plugins.publish)
    alias(libs.plugins.compatibility)
}

kotlin {
    jvmToolchain(17)

    jvm()
    androidTarget {
        publishLibraryVariants("release")
    }

    // iosX64()
    // iosArm64()
    // iosSimulatorArm64()

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
        commonMain {
            dependencies {
                implementation(compose.foundation)
                implementation(compose.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(compose.desktop.uiTestJUnit4)
                implementation(compose.desktop.currentOs)
            }
        }

        jvmMain {
            dependencies {
                implementation(compose.desktop.common)
                implementation(libs.icepdf.core)
            }
        }

        all {
            languageSettings {
                enableLanguageFeature("ContextReceivers")
            }
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

tasks.withType<KotlinCompile> {
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

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)

    signAllPublications()

    coordinates("dev.zt64", "compose-pdf", version.toString())

    configure(KotlinMultiplatform(sourcesJar = true))

    pom {
        name = "compose-pdf"
        description = "Compose Multiplatform library that displays PDF files"
        inceptionYear = "2023"
        url = "https://github.com/zt64/compose-pdf"
        licenses {
            license {
                name = "MIT License"
                url = "https://opensource.org/license/mit/"
            }
        }
        developers {
            developer {
                id = "zt64"
                name = "Zt"
                url = "https://zt64.dev"
            }
            developer {
                id = "wingio"
                name = "Wing"
                url = "https://wingio.xyz"
            }
        }
        scm {
            url = "https://github.com/zt64/compose-pdf"
            connection = "scm:git:github.com/zt64/compose-pdf.git"
            developerConnection = "scm:git:ssh://github.com/zt64/compose-pdf.git"
        }
    }
}