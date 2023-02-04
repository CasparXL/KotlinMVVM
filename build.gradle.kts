// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.4.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
//    id 'com.android.library' version '7.4.1' apply false
}
allprojects {
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin") {
                if (requested.name.startsWith("kotlin-stdlib")) {
                    useVersion(libs.versions.kotlin.get())
                }
            }
        }
    }
    tasks.withType(JavaCompile::class.java) {
        options.encoding = "UTF-8"
    }
    tasks.withType(Javadoc::class.java) {
        options {
            encoding("UTF-8")
            charset("UTF-8")
            description = "http://docs.oracle.com/javase/11/docs/api"
        }
    }
    buildDir = File(rootDir, "build/${path.replace(':', '/')}")
}

task("clean"){
    delete(rootProject.buildDir)
}
