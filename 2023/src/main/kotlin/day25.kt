import utils.Parse
import utils.Parser
import utils.Solution
import utils.parseItems

fun main() {
  Day25.run()
}

object Day25 : Solution<List<Day25.EdgeDesc>>() {
  override val name = "day25"
  override val parser = Parser.lines.parseItems { parseEdgeDesc(it) }

  @Parse("{from}: {r ' ' to}")
  data class EdgeDesc(
    val from: String,
    val to: List<String>,
  )

  private fun group(g: Map<String, Set<String>>, start: String): Set<String> {
    val q = ArrayDeque(listOf(start))
    val visited = mutableSetOf(start)
    while (q.isNotEmpty()) {
      val s = q.removeFirst()
      g[s]!!.forEach {
        if (it !in visited) {
          visited.add(it)
          q.add(it)
        }
      }
    }
    return visited
  }

  override fun part1(): Int {
    val edges = mutableSetOf<Pair<String, String>>()

    input.forEach { e ->
      e.to.forEach { to ->
        edges += if (e.from < to) e.from to to else to to e.from
      }
    }

    edges.forEach {
      println("  ${it.first} -- ${it.second}")
    }

    val g = mutableMapOf<String, MutableSet<String>>()
    edges.forEach { e ->
      (g.getOrPut(e.first) { mutableSetOf() }) += e.second
      (g.getOrPut(e.second) { mutableSetOf() }) += e.first
    }

    return 0
  }
}
