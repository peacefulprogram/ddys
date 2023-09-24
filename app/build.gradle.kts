plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.jing.ddys"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jing.ddys"
        minSdk = 21
        targetSdk = 33
        versionCode = 5
        versionName = "1.2.1"

        vectorDrawables {
            useSupportLibrary = true
        }

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    kotlin {
        jvmToolchain(17)
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
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}

dependencies {

    implementation("androidx.leanback:leanback-paging:1.1.0-alpha10")
    val roomVersion = "2.5.2"
    val coilVersion = "2.4.0"
    val composeTvVersion = "1.0.0-alpha09"
    implementation("androidx.core:core-ktx:1.12.0")
//    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.0"))
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.leanback:leanback:1.0.0")
    implementation("androidx.compose.material:material-icons-extended:1.5.1")

    implementation("androidx.leanback:leanback:1.0.0")


    implementation("com.google.accompanist:accompanist-permissions:0.30.1")

    // paging
    implementation("androidx.paging:paging-compose:3.2.1")

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


    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    val media3Version = "1.1.1"
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    implementation("androidx.media3:media3-ui:$media3Version")
    implementation("androidx.media3:media3-exoplayer-hls:$media3Version")
    implementation("androidx.media3:media3-ui-leanback:$media3Version")
    implementation("androidx.media3:media3-datasource-okhttp:$media3Version")


    implementation("wang.harlon.quickjs:wrapper-android:0.20.2")

}