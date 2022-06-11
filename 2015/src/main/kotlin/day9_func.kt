import utils.Graph
import utils.Parser
import utils.Solution
import utils.cut
import utils.mapItems

fun main() {
  Day9Func.run()
}

object Day9Func : Solution<Graph<String, Int>>() {
  override val name = "day9"
  override val parser = Parser.lines.mapItems { line ->
    val (cities, dist) = line.cut(" = ")
    val (a, b) = cities.cut(" to ")
    Triple(a, b, dist.toInt())
  }.map { list ->
    val graph = list.flatMap { (a, b, dist) ->
      listOf(Triple(a, b, dist), Triple(b, a, dist))
    }.groupBy { (src, _, _) -> src }
    Graph(
      edgeFn = { node -> graph[node]?.map { it.third to it.second } ?: emptyList() },
      weightFn = { it },
      nodes = graph.keys
    )
  }

  override fun part1(input: Graph<String, Int>) = input.shortestTour()

  override fun part2(input: Graph<String, Int>) = input.longestTour()
}
