plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}
android {
    namespace = "com.caspar.base"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        multiDexEnabled = true
    }

    sourceSets {
        getByName("main") {
            // res 资源目录配置
            res.srcDirs(
                "src/main/res",
                "src/main/res-sw",
            )
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true   //开启混淆
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false   //开启混淆
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        viewBinding = true
        // for view binding :
        // viewBinding = true
    }
    //使用JAVA11语法解析
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    //kotlin JDK11支持
    kotlinOptions {
        jvmTarget = "17"
    }
    packagingOptions {
        resources {
            excludes.add("META-INF/rxjava.properties")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.2")
    implementation(platform(libs.kotlin.bom))


    implementation(libs.bundles.androidx.view)
    implementation(libs.bundles.lifecycle)
    implementation(libs.google.material)
    implementation(libs.toast)
    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.immersionbar)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.androidtestImpl)
}
