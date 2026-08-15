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

fun buildConfigString(value: String): String =
    "\"${value.replace("\\", "\\\\").replace("\"", "\\\"")}\""

val debugSupabaseUrl = providers.gradleProperty("SUPABASE_URL")
    .orElse(providers.environmentVariable("SUPABASE_URL"))
    .orElse("https://hpvsvsvrdlucuxcdrgbg.supabase.co")
    .get()
val debugSupabaseKey = providers.gradleProperty("SUPABASE_KEY")
    .orElse(providers.environmentVariable("SUPABASE_KEY"))
    .orElse("sb_publishable_8jSWIC_m-NjRTbux2ZoYvA_I8ypilp7")
    .get()
val releaseSupabaseUrl = providers.gradleProperty("RELEASE_SUPABASE_URL")
    .orElse(providers.environmentVariable("RELEASE_SUPABASE_URL"))
    .orElse("")
    .get()
val releaseSupabaseKey = providers.gradleProperty("RELEASE_SUPABASE_KEY")
    .orElse(providers.environmentVariable("RELEASE_SUPABASE_KEY"))
    .orElse("")
    .get()

val verifyReleaseConfig = tasks.register("verifyReleaseConfig") {
    inputs.property("releaseSupabaseUrl", releaseSupabaseUrl)
    inputs.property("releaseSupabaseKey", releaseSupabaseKey)
    doLast {
        check(inputs.properties["releaseSupabaseUrl"].toString().isNotBlank()) {
            "RELEASE_SUPABASE_URL must be provided for release builds"
        }
        check(inputs.properties["releaseSupabaseKey"].toString().isNotBlank()) {
            "RELEASE_SUPABASE_KEY must be provided for release builds"
        }
    }
}

tasks.configureEach {
    if (name == "preReleaseBuild") {
        dependsOn(verifyReleaseConfig)
    }
}

android {
    namespace = "com.example.hockey_app"
    compileSdk = 37

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
            buildConfigField("String", "SUPABASE_URL", buildConfigString(debugSupabaseUrl))
            buildConfigField("String", "SUPABASE_KEY", buildConfigString(debugSupabaseKey))
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Aquí deberías usar variables de entorno en producción
            buildConfigField("String", "SUPABASE_URL", buildConfigString(releaseSupabaseUrl))
            buildConfigField("String", "SUPABASE_KEY", buildConfigString(releaseSupabaseKey))
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
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Supabase
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)
    implementation(libs.supabase.compose.auth)
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
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.coil.compose)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.crashlytics)

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
