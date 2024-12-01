plugins {
  `kotlin-dsl`
}

group = "pink.madis.aoc.buildlogic"

java {
  sourceCompatibility = JavaVersion.VERSION_23
  targetCompatibility = JavaVersion.VERSION_23
}

dependencies {
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

  compileOnly(libs.gradle.jmh)
//  compileOnly(libs.plugins.jmh)
}

gradlePlugin {
  plugins {
    register("puzzleConvention") {
      id = "pink.madis.aoc-puzzles"
      implementationClass = "PuzzleConvention"
    }
  }
}
