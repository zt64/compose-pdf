import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    // alias(libs.plugins.kotlin.native.cocoapods)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.library)
    alias(libs.plugins.binary.compatibility)

    id("maven-publish")
    id("signing")
}

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
        archivesName
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}


// -- Publishing --

val sonatypeUsername: String? = System.getenv("SONATYPE_USERNAME")
val sonatypePassword: String? = System.getenv("SONATYPE_PASSWORD")

afterEvaluate {
    publishing {
        repositories {
            if(sonatypeUsername == null || sonatypePassword == null) {
                mavenLocal()
            } else {
                maven {
                    credentials {
                        username = sonatypeUsername
                        password = sonatypePassword
                    }
                    setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                }
            }
        }

        publications.withType<MavenPublication> {
            artifactId = artifactId.replaceFirst("lib", "compose-pdf")
            pom {
                name.set("compose-pdf")
                description.set("Compose Multiplatform library that displays PDF files")
                url.set("https://github.com/zt64/compose-pdf")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/license/mit/")
                    }
                }
                developers {
                    developer {
                        id.set("zt64")
                        name.set("Zt")
                        url.set("https://zt64.dev")
                    }
                    developer {
                        id.set("wingio")
                        name.set("Wing")
                        url.set("https://wingio.xyz")
                    }
                }
                scm {
                    url.set("https://github.com/zt64/compose-pdf")
                    connection.set("scm:git:github.com/zt64/compose-pdf.git")
                    developerConnection.set("scm:git:ssh://github.com/zt64/compose-pdf.git")
                }
            }
        }
    }
}

if (sonatypeUsername != null && sonatypePassword != null) {
    signing {
        useInMemoryPgpKeys(
            System.getenv("SIGNING_KEY_ID"),
            System.getenv("SIGNING_KEY"),
            System.getenv("SIGNING_PASSWORD"),
        )
        sign(publishing.publications)
    }

    val dependsOnTasks = mutableListOf<String>()
    tasks.withType<AbstractPublishToMaven>().configureEach {
        dependsOnTasks.add(name.replace("publish", "sign").replaceAfter("Publication", ""))
        dependsOn(dependsOnTasks)
    }
}