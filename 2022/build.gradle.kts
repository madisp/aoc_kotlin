plugins {
  kotlin("jvm")
  id("me.champeau.jmh")
}

dependencies {
  implementation(project(":lib"))

  testImplementation("junit:junit:4.13.2")
  testImplementation("com.google.truth:truth:1.1.3")
}

jmh {
  warmup.set("2s")
  warmupIterations.set(3)

  iterations.set(3)
  timeOnIteration.set("1s")

  fork.set(3)
}
