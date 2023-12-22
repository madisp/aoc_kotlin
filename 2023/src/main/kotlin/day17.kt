import utils.Graph
import utils.Grid
import utils.Parser
import utils.Solution
import utils.Vec2i

fun main() {
  Day17.run()
}

object Day17 : Solution<Grid<Char>>() {
  override val name = "day17"
  override val parser = Parser.charGrid

  data class Node(
    val location: Vec2i,
    val entryDirection: Vec2i,
  )

  data class Edge(
    val from: Vec2i,
    val direction: Vec2i,
    val len: Int,
  )

  private val DIRECTIONS = listOf(Vec2i.UP, Vec2i.RIGHT, Vec2i.DOWN, Vec2i.LEFT)

  private fun solve(moves: IntRange): Int {
    val g = Graph<Node, Edge>(
      edgeFn = { node ->
        val edges = DIRECTIONS.filter { node.entryDirection.x != 0 && it.x == 0 || node.entryDirection.y != 0 && it.y == 0 }.flatMap { direction ->
          moves.map { len ->
            Edge(node.location, direction, len)
          }.filter {
            (node.location + (it.direction * it.len)) in input
          }.map {
            it to Node(node.location + (it.direction * it.len), it.direction)
          }
        }
        edges
      },
      weightFn = { edge ->
        val weight = (1 .. edge.len).sumOf {
          input[edge.from + (edge.direction * it)] - '0'
        }
        weight
      }
    )

    val end = Vec2i(input.width - 1, input.height - 1)

    return g.shortestPath(
      start = Node(Vec2i(0, 0), Vec2i(1, 1)),
      heuristic = { node -> node.location.manhattanDistanceTo(end) },
      end = { it.location == end },
    ).first
  }

  override fun part1() = solve(1 .. 3)
  override fun part2() = solve(4 .. 10)
}
