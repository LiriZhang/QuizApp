import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.jetbrains.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.devtools.ksp") version "2.3.6"
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        val iosArm64Main by getting {
            kotlin.srcDir("build/generated/ksp/iosArm64/iosArm64Main/kotlin")
        }
        val iosSimulatorArm64Main by getting {
            kotlin.srcDir("build/generated/ksp/iosSimulatorArm64/iosSimulatorArm64Main/kotlin")
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            
            // JetBrains Lifecycle & ViewModel for KMP
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")
            // Thay vì dùng bản androidx thuần Android, hãy dùng bản multiplatform của JetBrains:
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10") // hoặc phiên bản mới nhất

            // Coil 3
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)

            // Ktor Client & Kotlinx Serialization
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.common)
            implementation(libs.sqlite.bundled)
        }
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.navigation.compose)
            implementation(libs.androidx.compose.material.icons)
            implementation(libs.media3.exoplayer)
            implementation(libs.media3.ui)
            implementation(libs.google.admob)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.ktx)
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

dependencies {
    "kspAndroid"(libs.room.compiler)
    "kspIosArm64"(libs.room.compiler)
    "kspIosSimulatorArm64"(libs.room.compiler)
}

tasks.withType<KotlinCompilationTask<*>>().configureEach {
    if (name.contains("Ios")) {
        val targetName = name.removePrefix("compile").removeSuffix("Kotlin")
        val kspTask = tasks.findByName("ksp$targetName")
        if (kspTask != null) {
            dependsOn(kspTask)
        }
    }
}

android {
    namespace = "com.exam.quizapp"
    compileSdk = 37

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.exam.quizapp.shared.resources"
}
