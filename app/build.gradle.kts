import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.android.application.get().pluginId)
    id(libs.plugins.kotlin.get().pluginId)
    id(libs.plugins.hilt.get().pluginId)
    id("kotlin-parcelize")
    id("kotlin-kapt")
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
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true"
                )
            }
        }
    }
    buildTypes {
        release {
            resValue("bool","log_enable", "false")
            isMinifyEnabled = true   //开启混淆
            isShrinkResources = true  //移出无用资源
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            resValue("bool","log_enable", "true")
            isMinifyEnabled = false   //开启混淆
            isShrinkResources = false  //移出无用资源
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    //避免打正式包时一直下载groovy.jar的问题
    buildFeatures {
        viewBinding = true
        resValues = true
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
    //kotlin的java17支持
    kotlinOptions {
        jvmTarget = "17"
    }
    packaging {
        resources {
            excludes.add("META-INF/rxjava.properties")
            excludes.add("META-INF/INDEX.LIST")
            excludes.add("META-INF/io.netty.versions.properties")
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
    implementation(platform(libs.kotlin.bom))
    implementation("androidx.appcompat:appcompat-resources:1.6.1")
    implementation(libs.bundles.androidx.view)
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.coroutines)
    implementation(libs.flycoTabLayout)
    implementation(libs.chart)
    implementation(libs.androidx.multidex)
    implementation(libs.androidx.startup.runtime)
    implementation(libs.bundles.retrofit)
    implementation(libs.crash)
    implementation(libs.google.gson)
    implementation(libs.toast)
    implementation(libs.adapter)
    implementation(libs.google.material) {
        exclude(libs.androidx.recyclerview.get().module.group)
    }
    implementation("io.ktor:ktor-server-netty:2.3.5")
    implementation("io.ktor:ktor-server-core:2.3.5")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-server-status-pages:2.3.5")
    implementation("io.ktor:ktor-serialization-gson:2.3.5")
    implementation(libs.log.timber)
    implementation(libs.hilt.android)
    implementation(libs.mmkv)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.room)
    implementation(libs.bundles.camerax)
    implementation(libs.bundles.smart)
    implementation(libs.glide)
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
