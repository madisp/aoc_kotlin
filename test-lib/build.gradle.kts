plugins {
  kotlin("jvm")
}

dependencies {
  api(project(":lib"))
  api(libs.bundles.test)
}
