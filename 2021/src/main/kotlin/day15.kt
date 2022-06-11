import utils.Graph
import utils.Grid
import utils.Solution
import utils.Vec2i

fun main() {
  Day15.run()
}

object Day15 : Solution<Grid>() {
  override val name = "day15"
  override val parser = Grid.singleDigits

  override fun part1(input: Grid): Int {
    return solve(input)
  }

  override fun part2(input: Grid): Number? {
    val wide = Grid(input.width * 5, input.height * 5) { (x, y) ->
      val dist = (x / input.width) + (y / input.height)
      val orig = input[x % input.width][y % input.height]
      val new = orig + dist

      if (new > 9) new - 9 else new
    }

    return solve(wide)
  }

  private fun solve(input: Grid): Int {
    val start = Vec2i(0, 0)
    val end = Vec2i(input.width - 1, input.height - 1)

    val graph = Graph<Vec2i, Int>(
      edgeFn = { node -> node.adjacent.filter { it in input }.map { input[it] to it } },
      weightFn = { it }
    )

    return graph.shortestPath(start, end)
  }
}
