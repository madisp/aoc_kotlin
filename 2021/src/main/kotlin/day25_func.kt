import utils.Grid
import utils.Parser
import utils.Solution
import utils.wrap

fun main() {
  Day25Func.run()
}

object Day25Func : Solution<Grid>() {
  override val name = "day25"

  private const val EMPTY = 0
  private const val EAST = 1
  private const val SOUTH = 2

  override val parser = Parser { input -> input
    .replace('.', '0')
    .replace('>', '1')
    .replace('v', '2')
  }.map { Grid.singleDigits(it) }

  private fun tryMove(grid: Grid, type: Int): Grid {
    val xAxis = type == EAST
    return grid.map { coord, value ->
      if (value == type) {
        // move if next is empty
        val toX = if (xAxis) (coord.x + 1).wrap(grid.width) else coord.x
        val toY = if (!xAxis) (coord.y + 1).wrap(grid.height) else coord.y
        if (grid[toX][toY] == EMPTY) EMPTY else type
      } else if (value == EMPTY) {
        val fromX = if (xAxis) (coord.x - 1).wrap(grid.width) else coord.x
        val fromY = if (!xAxis) (coord.y - 1).wrap(grid.height) else coord.y
        if (grid[fromX][fromY] == type) type else EMPTY
      } else {
        value
      }
    }
  }

  fun step(grid: Grid): Grid {
    return tryMove(tryMove(grid, EAST), SOUTH)
  }

  override fun part1(input: Grid): Number? {
    return generateSequence(Grid.EMPTY to input) { it.second to step(it.second) }
      .takeWhile { it.first != it.second }
      .count()
  }
}
