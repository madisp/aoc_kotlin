plugins {
  kotlin("jvm")
  id("com.google.devtools.ksp")
}

dependencies {
  kspTest(project(":parser-processor"))

  testImplementation(project(":lib"))
  testImplementation(libs.bundles.test)
}
