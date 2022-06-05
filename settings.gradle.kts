@file:Suppress("UnstableApiUsage")

pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
  plugins {
    kotlin("jvm") version "1.6.10"
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
include(":2021")
