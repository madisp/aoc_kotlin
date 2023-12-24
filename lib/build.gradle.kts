plugins {
    kotlin("jvm")
}

dependencies {
  implementation(rootProject.files("z3/com.microsoft.z3.jar"))

  testImplementation("junit:junit:4.13.2")
  testImplementation("com.google.truth:truth:1.1.3")
}
