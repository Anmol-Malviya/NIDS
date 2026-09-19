plugins {
    // 1. Standard Android application configuration structure
    alias(libs.plugins.android.application)

    // 2. kotlin.compose (2.x) already applies kotlin.android internally in AGP 9.x.
    //    Adding kotlin.android separately caused: "extension 'kotlin' already registered".
    //    Fix: kotlin.compose alone is sufficient for both Kotlin + Compose compilation.
    alias(libs.plugins.kotlin.compose)

    // 3. ACTIVATES FIREBASE
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.nidsmonitor"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.nidsmonitor"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // JVM target set via the new Kotlin 2.x / AGP 9.x compilerOptions DSL.
    // kotlinOptions{} is removed in Kotlin 2.x when applied via kotlin.compose.
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        compose = true
    }

    lint {
        baseline = file("lint-baseline.xml")
        abortOnError = false
    }
}

dependencies {
    // BUG FIX #1: Replaced all hardcoded version strings with version catalog aliases
    // to eliminate Gradle resolution conflicts with libs.versions.toml

    // Core AndroidX and Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose Bill of Materials (managed via version catalog)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Jetpack Compose Navigation & Shared ViewModel Support
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Networking Components (Retrofit 3.x via version catalog)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Firebase Ecosystem (via version catalog)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics)

    // Extended Material Icons
    implementation("androidx.compose.material:material-icons-extended")

    // Testing Dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}