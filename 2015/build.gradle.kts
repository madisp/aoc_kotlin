plugins {
  kotlin("jvm")
  id("com.google.devtools.ksp")
}

dependencies {
  implementation(project(":lib"))

  ksp(project(":parser-processor"))

  testImplementation("junit:junit:4.13.2")
  testImplementation("com.google.truth:truth:1.1.3")
}
