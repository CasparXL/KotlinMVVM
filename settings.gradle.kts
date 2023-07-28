pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
dependencyResolutionManagement {
    //RepositoriesMode.PREFER_PROJECT : 解析依赖库时 , 优先使用本地仓库 , 本地仓库没有该依赖 , 则使用远程仓库 ;
    //RepositoriesMode.FAIL_ON_PROJECT_REPOS : 解析依赖库时 , 强行使用远程仓库 , 不管本地仓库有没有该依赖库 ;
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
    //导入依赖文件
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

rootProject.name = "KotlinMVVM"
include(":app")
include(":base")
