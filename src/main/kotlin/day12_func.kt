import utils.Parser
import utils.mapItems

fun main() {
  Day12Func.run()
}

object Day12Func : Solution<Map<String, List<String>>> {
  override val name = "day12"
  override val parser = Parser.lines.mapItems {
      val (start, end) = it.split('-', limit = 2)
      start to end
    }.map {
      it.flatMap { (start, end) ->
        listOf(start to end, end to start)
      }
      .groupBy({ (start, _) -> start }) { (_, end) -> end }
    }

  val String.isBig get() = toCharArray().all(Char::isUpperCase)

  fun countPaths(graph: Map<String, List<String>>, visited: Set<String>, doubleVisit: String?, vertex: String): Int {
    if (vertex == "end") {
      return 1
    }
    val novisit = graph[vertex]!!.filter { it.isBig || it !in visited }.sumOf {
      countPaths(graph, visited + it, doubleVisit, it)
    }
    val doublevisit = if (doubleVisit == null) {
      graph[vertex]!!.filter { !it.isBig && it in visited && it !in setOf("start", "end") }.sumOf {
        countPaths(graph, visited, it, it)
      }
    } else {
      0
    }

    return novisit + doublevisit
  }

  override fun part1(input: Map<String, List<String>>): Int {
    return countPaths(input, setOf("start"), "start", "start")
  }

  override fun part2(input: Map<String, List<String>>): Int {
    return countPaths(input, setOf("start"), null, "start")
  }
}
