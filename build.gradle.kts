import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.native.cocoapods) apply false

    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.compose.jb) apply false

    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.application) apply false

    alias(libs.plugins.compatibility) apply false
    alias(libs.plugins.ktlint) apply false
}

allprojects {
    apply {
        plugin("org.jlleitschuh.gradle.ktlint")
    }

    configure<KtlintExtension> {
        version = "1.2.1"
    }

    group = "dev.zt64"
    version = "1.1.1"
}

subprojects {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
            freeCompilerArgs.addAll("-Xexpect-actual-classes", "-Xcontext-receivers")
        }
    }
}