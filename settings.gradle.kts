pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://devrepo.kakao.com/nexus/repository/kakaomap-releases/")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://devrepo.kakao.com/nexus/repository/kakaomap-releases/")
    }
}

rootProject.name = "RestIntro"
include(":app")
 