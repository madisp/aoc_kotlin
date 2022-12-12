import utils.GGrid
import utils.Graph
import utils.Parser
import utils.Solution
import utils.Vec2i
import java.lang.IllegalStateException

fun main() {
  Day12.run()
}

object Day12 : Solution<GGrid<Char>>() {
  override val name = "day12"
  override val parser = Parser { GGrid.chars(' ')(it.trim()) }

  data class Node(val p: Vec2i, val height: Int)
  data class Edge(val weight: Int)

  private fun height(c: Char): Int {
    return when (c) {
      'S' -> 0
      'E' -> 'z' - 'a'
      else -> (c - 'a')
    }
  }

  private fun solve(input: GGrid<Char>, startChars: Set<Char>): Int {
    val g = Graph<Node, Edge>(
      edgeFn = { v ->
        val h1 = height(input[v.p])
        v.p.adjacent.filter { it in input }.map {
          val height = height(input[it])
          val node = Node(it, height)
          val edge = Edge(maxOf(height - h1, 0))

          edge to node
        }.filter { (edge, _) -> edge.weight <= 1 }
      },
      weightFn = { 1 }
    )

    val starts = input.cells.filter { (_, c) -> c in startChars }.map { it.first }
    val end = input.cells.first { (_, c) -> c == 'E' }.first

    return starts.minOf { start ->
      try {
        g.shortestPath(Node(start, 0), Node(end, height('E')))
      } catch (e: IllegalStateException) {
        Integer.MAX_VALUE // no path
      }
    }
  }

  override fun part1(input: GGrid<Char>): Int = solve(input, setOf('S'))
  override fun part2(input: GGrid<Char>): Int = solve(input, setOf('S', 'a'))
}
