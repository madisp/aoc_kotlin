plugins {
  kotlin("jvm")
}

dependencies {
  implementation(rootProject.files("z3/com.microsoft.z3.jar"))
  testImplementation(libs.bundles.test)
}
