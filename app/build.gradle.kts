import com.android.build.gradle.internal.scope.ProjectInfo.Companion.getBaseName
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}


android {
    namespace = libs.versions.applicationId.get()
    signingConfigs {
        create("release"){
            keyAlias = "kotlinmvvm"
            keyPassword = "123456"
            storeFile = file("../signature/releaseSign.jks")
            storePassword = "123456"
        }
    }
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = libs.versions.applicationId.get()
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        multiDexEnabled = true  //开启分包
        // 使用矢量图支持库（为了兼容 API 21 以下）
        // 阿里巴巴矢量图库：https://www.iconfont.cn/
        vectorDrawables.useSupportLibrary = true
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
                arguments["room.incremental"] = "true"
                arguments["room.expandProjection"] = "true"
            }
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true   //开启混淆
            isShrinkResources = true  //移出无用资源
            buildConfigField("boolean", "LOG_ENABLE", "false")//控制日志是否打印
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false   //开启混淆
            isShrinkResources = false  //移出无用资源
            buildConfigField("boolean", "LOG_ENABLE", "true")//控制日志是否打印
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    //避免打正式包时一直下载groovy.jar的问题
    buildFeatures {
        viewBinding = true
    }
    //使用JAVA11语法解析
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
    }
    //kotlin的java11支持
    kotlinOptions {
        jvmTarget = "11"
    }
    packagingOptions {
        resources {
            excludes.add("META-INF/rxjava.properties")
        }
    }
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
    //apk输出格式
    applicationVariants.all {
        val dtfInput = DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.getDefault())
        val format = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).format(dtfInput)
        // Apk 输出配置
        val buildType = buildType.name
        outputs.all {
            if (this is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                outputFileName = if (buildType == "release") {
                    "${rootProject.name}_v${versionCode}_${format}_release.apk"
                } else {
                    "${rootProject.name}_v${versionCode}_${format}_debug.apk"
                }
            }
        }
    }
}

dependencies {
    //依赖库配置
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    // 基础库
    implementation(project(":base"))
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.2")
    implementation(libs.bundles.androidx.view)
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.coroutines)
    implementation(libs.flycoTabLayout)
    implementation(libs.chart)
    implementation(libs.androidx.multidex)
    implementation(libs.bundles.retrofit) {
        exclude(libs.kotlin.stdlib.get().module.group)
    }
    implementation(libs.crash)
    implementation(libs.google.gson)
    implementation(libs.toast)
    implementation(libs.adapter)
    implementation(libs.google.material) {
        exclude(libs.androidx.recyclerview.get().module.group)
    }
    implementation(libs.hilt.android)
    implementation(libs.mmkv)
    implementation(libs.bundles.room)
    implementation(libs.bundles.camerax)
    implementation(libs.bundles.smart)
    implementation(libs.coil)
    implementation(libs.lottie)
    kapt(libs.bundles.kapt)
    debugImplementation(libs.leakcanary)
    testImplementation(libs.bundles.testImplementation)
    androidTestImplementation(libs.bundles.androidtestImpl)
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}
