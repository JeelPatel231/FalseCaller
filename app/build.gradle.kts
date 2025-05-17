plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.1.20-2.0.0"
    id("kotlin-parcelize")
    kotlin("plugin.serialization") version "2.1.20"
}

android {
    namespace = "tel.jeelpa.falsecaller"
    compileSdk = 35

    defaultConfig {
        applicationId = "tel.jeelpa.falsecaller"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    implementation("com.google.accompanist:accompanist-permissions:0.37.3")

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // TODO: remove this dependency
    implementation("me.zhanghai.compose.preference:library:1.1.1")

    val koin_version = "4.0.3"
    implementation(project.dependencies.platform("io.insert-koin:koin-bom:$koin_version"))
    implementation("io.insert-koin:koin-core")
    implementation("io.insert-koin:koin-android")
    implementation("io.insert-koin:koin-compose-viewmodel")
    implementation("io.insert-koin:koin-compose-viewmodel-navigation")


    implementation(platform("io.arrow-kt:arrow-stack:2.1.0"))
    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-fx-coroutines")
    implementation("io.arrow-kt:arrow-optics:2.1.0")
    ksp("io.arrow-kt:arrow-optics-ksp-plugin:2.1.0")

    val paging_version = "3.3.6"
    implementation("androidx.paging:paging-runtime:$paging_version")
    implementation("androidx.paging:paging-compose:$paging_version")

    val coil_version = "3.2.0"
    implementation("io.coil-kt.coil3:coil-compose:$coil_version")
    implementation("io.coil-kt.coil3:coil-network-okhttp:$coil_version")

    val compose_destinations_version = "2.1.1"
    implementation("io.github.raamcosta.compose-destinations:core:$compose_destinations_version")
    ksp("io.github.raamcosta.compose-destinations:ksp:$compose_destinations_version")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}