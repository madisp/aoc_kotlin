@file:Suppress("UnstableApiUsage")

pluginManagement {
  includeBuild("build-logic")
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositories {
    mavenCentral()
  }
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

rootProject.name = "aoc_kotlin"

include(":lib", ":parser-processor", ":parser-processor-tests", ":test-lib")
include(":2015", ":2016", ":2019", ":2021", ":2022", ":2023", ":2024")
