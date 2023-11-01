pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "Test VPN"
include(":app")
include(":ics-openvpn-main")
project(":ics-openvpn-main").projectDir = File(rootDir, "/ics-openvpn/main")

