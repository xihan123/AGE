pluginManagement {
    repositories {
//        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
//        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") {
//            content {
//                // 排除不需要的包
//                excludeGroupByRegex("com.github.*")
//            }
//        }
        google()
//        {
//            content {
//                // 排除不需要的包
//                excludeGroupByRegex("com.github.*")
//            }
//        }
        mavenCentral()
        maven("https://jitpack.io"){
            content {
                includeGroupByRegex("com.github.*")
            }
        }
        maven("https://s01.oss.sonatype.org/content/repositories/releases")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
        maven("https://androidx.dev/storage/compose-compiler/repository/"){
            content {
                // 排除不需要的包
                excludeGroupByRegex("com.github.*")
            }
        }
    }
}

rootProject.name = "AGE动漫"
include(":app")

