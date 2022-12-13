@file:Suppress("UnstableApiUsage")

pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
  plugins {
    kotlin("jvm") version "1.7.21"
    id("me.champeau.jmh") version "0.6.8"
  }
}

dependencyResolutionManagement {
  repositories {
    mavenCentral()
  }
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

rootProject.name = "aoc_kotlin"

include(":lib")
include(":2015", ":2021", ":2022")
