import utils.Graph
import utils.IntGrid
import utils.Solution
import utils.Vec2i

fun main() {
  Day15.run()
}

object Day15 : Solution<IntGrid>() {
  override val name = "day15"
  override val parser = IntGrid.singleDigits

  override fun part1(input: IntGrid): Int {
    return solve(input)
  }

  override fun part2(input: IntGrid): Number? {
    val wide = IntGrid(input.width * 5, input.height * 5) { (x, y) ->
      val dist = (x / input.width) + (y / input.height)
      val orig = input[x % input.width][y % input.height]
      val new = orig + dist

      if (new > 9) new - 9 else new
    }

    return solve(wide)
  }

  private fun solve(input: IntGrid): Int {
    val start = Vec2i(0, 0)
    val end = Vec2i(input.width - 1, input.height - 1)

    val graph = Graph<Vec2i, Int>(
      edgeFn = { node -> node.adjacent.filter { it in input }.map { input[it] to it } },
      weightFn = { it }
    )

    return graph.shortestPath(start, end).first
  }
}
