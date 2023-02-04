plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}
android {
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
            isMinifyEnabled=true   //开启混淆
            buildConfigField("boolean", "LOG_ENABLE", "false")//控制日志是否打印
            proguardFiles(getDefaultProguardFile("proguard-android.txt"),"proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false   //开启混淆
            buildConfigField("boolean", "LOG_ENABLE", "true")//控制日志是否打印
            proguardFiles(getDefaultProguardFile("proguard-android.txt"),"proguard-rules.pro")
        }
    }
    buildFeatures {
        viewBinding = true
        // for view binding :
        // viewBinding = true
    }
    //使用JAVA8语法解析
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    //kotlin JDK1.8支持
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packagingOptions {
        resources {
            excludes.add("META-INF/rxjava.properties")
        }
    }
    namespace = "com.caspar.base"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar","*.aar"))))
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.2")
    implementation(libs.bundles.kotlin)

    implementation(libs.bundles.androidx.view)
    implementation(libs.bundles.lifecycle)
    implementation(libs.google.material)
    implementation(libs.toast)
    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.immersionbar)
    implementation(libs.bundles.kotlin)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.androidtestImpl)
}
