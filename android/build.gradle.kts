import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    kotlin("android")
    id("convention.publish")
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 23
        namespace = "org.openwallet.kitten.android"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }

    publishing {
        multipleVariants {
            withSourcesJar()
            withJavadocJar()
        }
    }

    tasks.withType<KotlinCompile>  {
        kotlinOptions {
            freeCompilerArgs += "-Xcontext-receivers"
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":api"))
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    testImplementation("junit:junit:4.13.2")
}
