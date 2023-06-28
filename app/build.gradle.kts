plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.jing.ddys"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.jing.ddys"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    kotlin {
        jvmToolchain(11)
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles("proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
}

dependencies {

    implementation("androidx.leanback:leanback-paging:1.1.0-alpha09")
    val roomVersion = "2.5.1"
    val coilVersion = "2.4.0"
    val composeTvVersion = "1.0.0-alpha07"
    implementation("androidx.core:core-ktx:1.10.1")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2022.10.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.leanback:leanback:1.0.0")
    implementation("androidx.compose.material:material-icons-extended:1.4.3")

    implementation("androidx.leanback:leanback:1.0.0")


    implementation("com.google.accompanist:accompanist-permissions:0.30.1")

    // paging
    implementation("androidx.paging:paging-compose:3.2.0-rc01")

    // compose tv
    implementation("androidx.tv:tv-foundation:$composeTvVersion")
    implementation("androidx.tv:tv-material:$composeTvVersion")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    // room
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-paging:$roomVersion")

    // coil
    implementation("io.coil-kt:coil:$coilVersion")
    implementation("io.coil-kt:coil-compose:$coilVersion")

    // koin
    implementation("io.insert-koin:koin-core:3.4.2")
    implementation("io.insert-koin:koin-android:3.4.2")
    implementation("io.insert-koin:koin-androidx-compose:3.4.5")


    // okhttp
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    implementation("org.jsoup:jsoup:1.16.1")
    implementation("com.google.code.gson:gson:2.10.1")

    val exoPlayerVersion = "2.18.1"

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    implementation("com.google.android.exoplayer:exoplayer-core:$exoPlayerVersion")
    implementation("com.google.android.exoplayer:exoplayer-hls:$exoPlayerVersion")
    implementation("com.google.android.exoplayer:exoplayer-common:$exoPlayerVersion")
    implementation("com.google.android.exoplayer:extension-leanback:$exoPlayerVersion")
    implementation("com.google.android.exoplayer:extension-okhttp:$exoPlayerVersion")
}