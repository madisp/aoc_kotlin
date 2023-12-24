@file:Suppress("UnstableApiUsage")

pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
  plugins {
    kotlin("jvm") version "1.9.21"
    id("me.champeau.jmh") version "0.7.2"
    id("com.google.devtools.ksp") version "1.9.21-1.0.15"
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.4.0")
  }
}

dependencyResolutionManagement {
  repositories {
    mavenCentral()
  }
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

rootProject.name = "aoc_kotlin"

include(":lib", ":parser-processor", ":parser-processor-tests")
include(":2015", ":2016", ":2019", ":2021", ":2022", ":2023")
