plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.tghrsyahptra.dicodingeventapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tghrsyahptra.dicodingeventapp"
        minSdk = 24
        targetSdk = 34
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    dependencies {
        // Navigation
        implementation(libs.androidx.navigation.fragment.ktx) // Navigation Fragment KTX
        implementation(libs.navigation.ui.ktx) // Navigation UI KTX

        // Image loading
        implementation(libs.glide) // Glide

        // Networking
        implementation(libs.retrofit) // Retrofit
        implementation(libs.converter.gson) // Gson Converter
        implementation(libs.logging.interceptor) // Logging Interceptor

        // Activity KTX
        implementation(libs.androidx.activity.ktx) // Activity KTX

        // Browser
        implementation(libs.androidx.browser) // Browser

        // Animation
        implementation(libs.lottie) // Lottie
        implementation(libs.shimmer) // Shimmer

        // Splash Screen
        implementation(libs.androidx.core.splashscreen) // Splash Screen

        // Room
        implementation(libs.androidx.room.runtime) // Room Runtime
        ksp(libs.room.compiler) // Room Compiler

        // Coroutine support
        implementation(libs.androidx.lifecycle.viewmodel.ktx) // ViewModel KTX
        implementation(libs.lifecycle.livedata.ktx) // LiveData KTX
        implementation(libs.room.ktx) // Room KTX

        // Settings
        implementation(libs.androidx.preference) // Preference

        // DataStore Preferences
        implementation(libs.androidx.datastore.preferences) // DataStore Preferences

        // Coroutines
        implementation(libs.kotlinx.coroutines.core) // Coroutines Core
        implementation(libs.kotlinx.coroutines.android) // Coroutines Android

        // Work Manager
        implementation(libs.androidx.work.runtime.ktx) // WorkManager
    }
}