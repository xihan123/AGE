pluginManagement {
    repositories {
//        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
//        maven("https://maven.aliyun.com/repository/gradle-plugin")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
//        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://androidx.dev/storage/compose-compiler/repository/")
    }
}

rootProject.name = "AGE动漫"
include(":app")
