plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.firebase.perf)
}

android {
    namespace = "com.example.hockey_app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.hockey_app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            buildConfigField("String", "SUPABASE_URL", "\"https://hpvsvsvrdlucuxcdrgbg.supabase.co\"")
            buildConfigField("String", "SUPABASE_KEY", "\"sb_publishable_8jSWIC_m-NjRTbux2ZoYvA_I8ypilp7\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Aquí deberías usar variables de entorno en producción
            buildConfigField("String", "SUPABASE_URL", "\"https://hpvsvsvrdlucuxcdrgbg.supabase.co\"")
            buildConfigField("String", "SUPABASE_KEY", "\"sb_publishable_8jSWIC_m-NjRTbux2ZoYvA_I8ypilp7\"")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

hilt {
    enableAggregatingTask = false
}

// kotlin block removed as jvmTarget is default and kotlin-android plugin is removed

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    constraints {
        implementation("androidx.concurrent:concurrent-futures:1.1.0")
        implementation("androidx.concurrent:concurrent-futures-ktx:1.1.0")
        implementation("io.ktor:ktor-client-core:3.1.1")
        implementation("io.ktor:ktor-client-okhttp:3.1.1")
        implementation("io.ktor:ktor-client-android:3.1.1")
        implementation("io.ktor:ktor-client-plugins:3.1.1")
        implementation("io.ktor:ktor-client-content-negotiation:3.1.1")
        implementation("io.ktor:ktor-client-auth:3.1.1")
        implementation("io.ktor:ktor-client-logging:3.1.1")
        implementation("io.ktor:ktor-serialization-kotlinx-json:3.1.1")
    }
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Supabase
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)
    implementation(libs.supabase.storage)
    implementation(libs.supabase.realtime)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.bundled.plugins)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.collections.immutable)

    // DI - Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Navigation & UI
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.crashlytics)

    // AI
    implementation(libs.google.generativeai)

    // GitHub Skills
    implementation(libs.timber)
    implementation(libs.lottie.compose)
    debugImplementation(libs.leakcanary)
    debugImplementation(libs.chucker.library)
    releaseImplementation(libs.chucker.no.op)
    implementation(libs.accompanist.permissions)
    implementation(libs.constraintlayout.compose)
    implementation(libs.firebase.performance)

    // Design Skills
    implementation(libs.compose.shimmer)
    implementation(libs.konfetti.compose)
    implementation(libs.vicoCompose)
    implementation(libs.vicoM3)
    implementation(libs.material.dialogs.core)
    implementation(libs.material.dialogs.datetime)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.turbine)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
