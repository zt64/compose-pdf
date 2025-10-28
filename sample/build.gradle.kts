import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    // alias(libs.plugins.kotlin.native.cocoapods)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.jb)
    alias(libs.plugins.android.application)
}

kotlin {
    jvmToolchain(17)

    jvm()
    androidTarget()
    // ios()

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
                implementation(project(":lib"))

                implementation(compose.foundation)
                implementation(compose.runtime)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)

                implementation(libs.filekit)
                implementation(libs.zoomable)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.appcompat)
            }
        }

        jvmMain {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

android {
    namespace = "dev.zt64.compose.pdf.sample"
    compileSdk = 36

    defaultConfig {
        targetSdk = 36
        minSdk = 24
    }

    buildTypes {
        release {
            isMinifyEnabled = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

compose.desktop {
    application {
        mainClass = "dev.zt64.compose.pdf.sample.MainKt"

        nativeDistributions {
            modules("java.instrument", "java.net.http", "jdk.security.auth")
            targetFormats(TargetFormat.Deb)

            packageName = "ComposePdf"

            linux {
                modules("jdk.security.auth")
            }
        }
    }
}