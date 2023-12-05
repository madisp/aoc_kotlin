plugins {
  kotlin("jvm")
  id("com.google.devtools.ksp")
}

dependencies {
  kspTest(project(":parser-processor"))

  testImplementation(project(":lib"))
  testImplementation("junit:junit:4.13.2")
  testImplementation("com.google.truth:truth:1.1.3")
}
