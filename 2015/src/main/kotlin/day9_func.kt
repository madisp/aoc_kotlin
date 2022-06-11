import utils.Graph
import utils.Parser
import utils.Solution
import utils.mapItems
import utils.triplicut

fun main() {
  Day9Func.run()
}

object Day9Func : Solution<Graph<String, Int>>() {
  override val name = "day9"
  override val parser = Parser.lines.mapItems { line ->
    line.triplicut(" to ", " = ")
  }.map { list ->
    val bidirectional = list.flatMap { (a, b, dist) ->
      listOf(Triple(a, b, dist), Triple(b, a, dist))
    }
    val edges = bidirectional.groupBy { (src, _, _) -> src }.mapValues { (_, v) ->
      v.map { (_, city, dist) -> dist.toInt() to city }
    }
    Graph(
      edgeFn = { node -> edges[node] ?: emptyList() },
      weightFn = { it },
      nodes = edges.keys
    )
  }

  override fun part1(input: Graph<String, Int>) = input.shortestTour()

  override fun part2(input: Graph<String, Int>) = input.longestTour()
}
