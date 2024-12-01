import me.champeau.jmh.JmhParameters
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.the

@Suppress("unused")
class PuzzleConvention : Plugin<Project> {
  override fun apply(target: Project) {

    val libs = target.the<LibrariesForLibs>()

    target.plugins.apply(libs.plugins.jmh.get().pluginId)
    target.plugins.apply(libs.plugins.kotlin.jvm.get().pluginId)
    target.plugins.apply(libs.plugins.ksp.get().pluginId)

    target.dependencies {
      add("implementation", target.project(":lib"))
      add("implementation", libs.coroutines)
      add("implementation", target.files(target.rootDir.resolve("z3/com.microsoft.z3.jar")))

      add("testImplementation", libs.bundles.test)

      add("ksp", target.project(":parser-processor"))
    }

    val jmhIncludes = target.properties["jmhFilter"]?.toString()

    target.the<JmhParameters>().apply {
      warmup.set("2s")
      warmupIterations.set(3)

      iterations.set(3)
      timeOnIteration.set("1s")

      fork.set(3)

      if (jmhIncludes != null) {
        includes.add(jmhIncludes)
      }
    }
  }
}
