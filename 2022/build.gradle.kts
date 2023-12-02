plugins {
  kotlin("jvm")
  id("me.champeau.jmh")
  id("com.google.devtools.ksp")
}

dependencies {
  implementation(project(":lib"))

  ksp(project(":parser-processor"))

  testImplementation("junit:junit:4.13.2")
  testImplementation("com.google.truth:truth:1.1.3")
}

val jmhIncludes = project.properties["jmhFilter"]?.toString()

jmh {
  warmup.set("2s")
  warmupIterations.set(3)

  iterations.set(3)
  timeOnIteration.set("1s")

  fork.set(3)

  if (jmhIncludes != null) {
    includes.add(jmhIncludes)
  }
}
