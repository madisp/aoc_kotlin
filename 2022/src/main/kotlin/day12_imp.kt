import utils.Grid
import utils.Graph
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.mapParser
import java.lang.IllegalStateException

fun main() {
  Day12Imp.run()
}

object Day12Imp : Solution<Grid<Char>>() {
  override val name = "day12"
  override val parser = Parser(String::trim).mapParser(Parser.charGrid)

  data class Node(val p: Vec2i, val height: Int)
  data class Edge(val weight: Int)

  private fun height(c: Char): Int {
    return when (c) {
      'S' -> 0
      'E' -> 'z' - 'a'
      else -> (c - 'a')
    }
  }

  private fun solve(input: Grid<Char>, startChars: Set<Char>): Int {
    val g = Graph<Node, Edge>(
      edgeFn = { v ->
        val h1 = height(input[v.p])

        // grab 4-way adjacent squares to the coordinate...
        v.p.adjacent
          // ...that are still in the input...
          .filter { it in input }
          .map {
            val height = height(input[it])
            val node = Node(it, height)
            val edge = Edge(maxOf(height - h1, 0))

            edge to node
          }
          // ...and are either lower or no more higher than 1
          .filter { (edge, _) -> edge.weight <= 1 }
      }
    )

    val starts = input.cells.filter { (_, c) -> c in startChars }.map { it.first }
    val end = input.cells.first { (_, c) -> c == 'E' }.first

    return starts.mapNotNull { start ->
      try {
        g.shortestPath(Node(start, 0), Node(end, height('E')))
      } catch (e: IllegalStateException) {
        null
      }
    }.min()
  }

  override fun part1(input: Grid<Char>): Int = solve(input, setOf('S'))
  override fun part2(input: Grid<Char>): Int = solve(input, setOf('S', 'a'))
}
