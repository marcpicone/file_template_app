pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        kotlin("jvm").version("1.9.22")
        id("org.jetbrains.compose").version("1.6.11")
        id("org.jetbrains.kotlin.jvm") version "1.9.22"
    }
}

rootProject.name = "file_template_app"
rootProject.buildFileName = "build.gradle.kts"

includeBuild("build_src")

include(
    ":file_template_app",
)
