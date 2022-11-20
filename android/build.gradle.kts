plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
}

dependencies {
    implementation(project(":common"))
    api("androidx.compose.ui:ui-tooling-preview:1.3.0-rc01")
    api("androidx.compose.material3:material3:1.0.0-rc01")
    api("androidx.activity:activity-compose:1.6.0")
    api("androidx.appcompat:appcompat:1.5.1")
    api("androidx.core:core-ktx:1.9.0")
}

android {
    compileSdk = (rootProject.extra["android.sdk.target.version"] as String).toInt()
    buildToolsVersion = rootProject.extra["android.build_tool.version"] as String
    testOptions {
        unitTests.apply {
            isReturnDefaultValues = true
        }
    }
    defaultConfig {
        applicationId = rootProject.extra["organization"] as String? + ".test.toasterAtSnackbar"
        minSdk = (rootProject.extra["android.sdk.min.version"] as String).toInt()
        targetSdk = (rootProject.extra["android.sdk.target.version"] as String).toInt()
        versionCode = 1
        versionName = rootProject.extra["version"] as String?

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    sourceSets {
        getByName("main") {
            manifest.srcFile("src/main/AndroidManifest.xml")
            kotlin.srcDirs("src/main/kotlin")
            res.srcDirs("src/main/res")
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = rootProject.extra["compose.ext.version"] as String
    }
    packagingOptions {
        resources {
            merges += setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "/META-INF/LICENSE*"
            )
            pickFirsts += "/bundle.properties"
        }
    }
}
