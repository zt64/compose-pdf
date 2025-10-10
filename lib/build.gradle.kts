@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.util.*

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    // alias(libs.plugins.kotlin.native.cocoapods)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.jb)
    alias(libs.plugins.android.library)
    alias(libs.plugins.publish)
    alias(libs.plugins.compatibility)
}

kotlin {
    jvmToolchain(17)
    explicitApi()

    applyDefaultHierarchyTemplate {
        common {
            group("jvmCommon") {
                withAndroidTarget()
                withJvm()
            }
        }
    }

    jvm()
    androidTarget {
        publishLibraryVariants("release")
    }
    // apple() // See https://github.com/zt64/compose-pdf/issues/28

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
                implementation(libs.kotlin.test)
                implementation(libs.coroutines.test)
            }
        }

        jvmMain {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(compose.desktop.uiTestJUnit4)
                implementation(libs.icepdf.core)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.core.ktx)
            }
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.addAll("-Xexpect-actual-classes")
    }
}

composeCompiler {
    reportsDestination = project.layout.buildDirectory.dir("compose_compiler")
    metricsDestination = project.layout.buildDirectory.dir("compose_compiler")
}

android {
    namespace = "dev.zt64.compose.pdf"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)

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

fun KotlinMultiplatformExtension.apple(skipCheck: Boolean = false, configure: KotlinNativeTarget.() -> Unit = {}) {
    if (!skipCheck) {
        val isMacOs = System
            .getProperty("os.name")
            .lowercase(Locale.getDefault())
            .contains("mac")

        if (!isMacOs) return
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        macosX64(),
        macosArm64()
    ).forEach(configure)
}