plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = ProjectConfig.SdkVersions.compileSdkVersion

    defaultConfig {
        minSdk = ProjectConfig.SdkVersions.minSdkVersion
        targetSdk = ProjectConfig.SdkVersions.targetSdkVersion
    }

    buildTypes {
        release {
            isMinifyEnabled = true
        }
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

    // AndroidX
    implementation("androidx.appcompat:appcompat:1.3.1")
}
