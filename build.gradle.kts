// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.hilt) apply false
    id("org.jetbrains.dokka") version "1.9.10"
}
allprojects {
    apply(plugin = "org.jetbrains.dokka")
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin") {
                if (requested.name.startsWith("kotlin-stdlib")) {
                    useVersion("2.0.0")
                }
            }
        }
    }
    tasks.withType(JavaCompile::class.java) {
        options.encoding = "UTF-8"
        version = 17
    }
    tasks.withType(Javadoc::class.java) {
        options {
            encoding("UTF-8")
            charset("UTF-8")
            description = "http://docs.oracle.com/javase/17/docs/api"
        }
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_17.toString()
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-Xcontext-receivers"
            )
        }
    }
    buildDir = File(rootDir, "build/${path.replace(':', '/')}")
    tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
        moduleName.set(project.name)
        moduleVersion.set(project.version.toString())
        outputDirectory.set(buildDir.resolve("dokka/$name"))
        failOnWarning.set(false)
        suppressObviousFunctions.set(true)
        suppressInheritedMembers.set(false)
        offlineMode.set(false)
        dokkaSourceSets {
            configureEach {
                suppress.set(false)
                displayName.set(name)
                documentedVisibilities.set(
                    setOf(
                        org.jetbrains.dokka.DokkaConfiguration.Visibility.PUBLIC,
                        org.jetbrains.dokka.DokkaConfiguration.Visibility.INTERNAL,
                        org.jetbrains.dokka.DokkaConfiguration.Visibility.PRIVATE,
                    )
                )
                reportUndocumented.set(false)
                skipEmptyPackages.set(true)
                skipDeprecated.set(false)
                suppressGeneratedFiles.set(true)
                jdkVersion.set(17)
                languageVersion.set("2.0")
                apiVersion.set("2.0")
                noStdlibLink.set(false)
                noJdkLink.set(false)
                noAndroidSdkLink.set(false)
            }
        }
    }
}

task("clean") {
    delete(rootProject.buildDir)
}
