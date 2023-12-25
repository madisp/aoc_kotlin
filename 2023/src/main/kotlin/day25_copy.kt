import utils.Parse
import utils.Parser
import utils.Solution
import utils.parseItems
import utils.selections
import java.util.concurrent.atomic.AtomicInteger

fun main() {
  Day25copy.run(skipTest = true)
}

object Day25copy : Solution<List<Day25.EdgeDesc>>() {
  override val name = "day25"
  override val parser = Parser.lines.parseItems { Day25.parseEdgeDesc(it) }

  private fun group(g: Map<String, Set<String>>, cuts: List<Pair<String, String>>, start: String): Set<String> {
    val q = ArrayDeque(listOf(start))
    val visited = mutableSetOf(start)
    while (q.isNotEmpty()) {
      val s = q.removeFirst()
      g[s]!!.forEach {
        if (it !in visited && (s to it !in cuts) && (it to s !in cuts)) {
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

    val g = mutableMapOf<String, MutableSet<String>>()
    edges.forEach { e ->
      (g.getOrPut(e.first) { mutableSetOf() }) += e.second
      (g.getOrPut(e.second) { mutableSetOf() }) += e.first
    }

    val counter = AtomicInteger(0)
    val sz = edges.size * (edges.size - 1) * (edges.size - 2)

    val cuts = listOf(
      "pzc" to "vps",
      "cvx" to "dph",
      "sgc" to "xvk",
    )

    val g1 = group(g, cuts, g.keys.first())
    return g1.size * (g.keys.size - g1.size)

//    return edges.toList().selections(3).firstNotNullOf { cuts ->
//      counter.incrementAndGet().takeIf { it % 10000 == 0 }?.let {
//        println("$it / $sz")
//      }
//      val g1 = group(g, cuts, g.keys.first())
//      if (g1.size < g.keys.size) {
//        // we cut something
//        val g2 = group(g, cuts, (g.keys - g1).first())
//        if (g1.size + g2.size == g.size) {
//          return@firstNotNullOf (g1.size * g2.size)
//        }
//      }
//
//      null
//    }
  }
}
