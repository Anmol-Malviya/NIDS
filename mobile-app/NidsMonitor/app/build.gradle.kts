plugins {
    // 1. Standard Android application configuration structure
    alias(libs.plugins.android.application)

    // 2. Activates the Compose layout compiler structures
    alias(libs.plugins.kotlin.compose)

    // 3. ACTIVATES FIREBASE: Uses the correct alias format to fix the line 3 compilation crash
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.nidsmonitor"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.nidsmonitor"
        minSdk = 24
        targetSdk = 35
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
        // Upgrades toolchain rendering to Java 17 compatibility standards
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
    // Core AndroidX and Lifecycle libraries (Forced to stable SDK 35 versions)
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
    implementation("androidx.activity:activity-compose:1.9.0")

    // Jetpack Compose Bill of Materials (BoM Updated to a universally available SDK 35 stable version)
    implementation(platform("androidx.compose:compose-bom:2024.10.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Explicitly declaring material3 version handles the local resolution warning seamlessly
    implementation("androidx.compose.material3:material3:1.3.0")

    // Testing Dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.10.00"))

    // Explicitly versioning the test artifact clears the final resolution warning
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Networking Components
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Jetpack Compose Navigation & Shared ViewModel Support
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // Firebase Ecosystem
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")

    // Add this line to access advanced icons:
    implementation("androidx.compose.material:material-icons-extended")
}