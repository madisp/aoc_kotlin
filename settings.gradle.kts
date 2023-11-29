@file:Suppress("UnstableApiUsage")

pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
  plugins {
    kotlin("jvm") version "1.9.21"
    id("me.champeau.jmh") version "0.7.2"
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
include(":2015", ":2019", ":2021", ":2022", ":2023")
