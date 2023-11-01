plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.ahiho.testvpn"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ahiho.testvpn"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        ndkVersion = "26.1.10909125"
        vectorDrawables.useSupportLibrary = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        missingDimensionStrategy("implementation", "ui")   // Skeleton is no option for us because we need the log activity
        missingDimensionStrategy("ovpnimpl", "ovpn23")

        // This excludes some pretty old ABIs such armeabi or mips. LibSodium-JNI still includes binaries for these, which
        // leads to some devices selecting these as the app ABI, but the OpenVPN library did not include a binary for these,
        // thus resulting in a crash.
        ndk {
            abiFilters.add("arm64-v8a")
            abiFilters.add("x86")
            abiFilters.add("x86_64")
            abiFilters.add("armeabi-v7a")
        }
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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    kapt {
        javacOptions {
            // Increase the max count of errors from annotation processors.
            // Default is 100.
            // See: https://github.com/google/dagger/issues/306#issuecomment-405525757
            option("-Xmaxerrs", 2000)
            // This will fail the build on warnings which are run in a build phase executed by kapt.
            option("-Werror")
        }
    }
    packaging {
        resources {
            excludes.add("DebugProbesKt.bin")
        }
    }
    lint {
        disable.add("GradleDependency")
        disable.add("UnsafeNativeCodeLocation")
        disable.add("RtlSymmetry")
        disable.add("RtlHardcoded")
        disable.add("MissingTranslation")
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.view.material)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(project(":ics-openvpn-main"))
}