import utils.Parser
import utils.Solution

fun main() {
  Day12Fast.run()
}

object Day12All {
  @JvmStatic fun main(args: Array<String>) {
    mapOf("func" to Day12Func, "imp" to Day12Imp, "fast" to Day12Fast).forEach { (header, solution) ->
      solution.run(header = header, skipPart1 = false, skipTest = false, printParseTime = false)
    }
  }
}

object Day12Fast : Solution<Day12Fast.Vertex>() {
  override val name = "day12"
  override val parser = Parser { input ->
    val vertices = mutableMapOf<String, Vertex>()

    input.lines().filter { it.isNotBlank() }.forEach { line ->
      val (n1, n2) = line.split('-', limit = 2)
      val v1 = vertices.getOrPut(n1) { Vertex(n1.toKind(), false, mutableListOf()) }
      val v2 = vertices.getOrPut(n2) { Vertex(n2.toKind(), false, mutableListOf()) }
      v1.edges.add(v2)
      v2.edges.add(v1)
    }

    return@Parser vertices["start"]!!
  }

  enum class Kind { BIG, SMALL, START, END }

  fun String.toKind(): Kind {
    return when (this) {
      "start" -> Kind.START
      "end" -> Kind.END
      else -> {
        if (toCharArray().all { it.isUpperCase() }) Kind.BIG else Kind.SMALL
      }
    }
  }

  class Vertex(val kind: Kind, var visited: Boolean, val edges: MutableList<Vertex>)

  /**
   * Perform a depth-first search to count all the paths
   */
  fun countPaths(vertex: Vertex, doubleVisitAllowed: Boolean): Int {
    if (vertex.kind == Kind.END) {
      return 1
    }

    var count = 0
    for (v in vertex.edges) {
      if (!v.visited || v.kind == Kind.BIG) {
        v.visited = true
        count += countPaths(v, doubleVisitAllowed)
        v.visited = false
      } else if (v.visited && v.kind == Kind.SMALL && doubleVisitAllowed) {
        count += countPaths(v, false)
      }
    }
    return count
  }

  override fun part1(input: Vertex): Int {
    return countPaths(input, false)
  }

  override fun part2(input: Vertex): Int {
    return countPaths(input, true)
  }
}
