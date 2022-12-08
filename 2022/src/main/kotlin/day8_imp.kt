import utils.Grid
import utils.Solution
import utils.Vec2i
import utils.takeWhileInclusive

fun main() {
  Day8Imp.run()
}

object Day8Imp : Solution<Grid>() {
  override val name = "day8"
  override val parser = Grid.singleDigits

  override fun part1(input: Grid): Int {
    val rows = input.rows.flatMap { row -> getAscending(row.cells) + getAscending(row.cells.reversed()) }
    val cols = input.columns.flatMap { col -> getAscending(col.cells) + getAscending(col.cells.reversed()) }
    val trees = (rows + cols).distinct().sortedBy { it.y * 10 + it.x }
    return trees.count()
  }

  override fun part2(input: Grid): Int {
    return input.coords.maxOf { scenicScore(it, input) }
  }

  private fun scenicScore(coord: Vec2i, grid: Grid): Int {
    val height = grid[coord]
    val left = getVisibleDistance(height, grid.rows[coord.y].values.take(coord.x).reversed())
    val right = getVisibleDistance(height, grid.rows[coord.y].values.drop(coord.x + 1))
    val up = getVisibleDistance(height, grid[coord.x].values.take(coord.y).reversed())
    val down = getVisibleDistance(height, grid[coord.x].values.drop(coord.y + 1))
    return left * right * up * down
  }

  private fun getAscending(trees: Collection<Pair<Vec2i, Int>>): Collection<Vec2i> {
    var curMax = Integer.MIN_VALUE
    val ret = mutableListOf<Vec2i>()
    for (tree in trees) {
      if (tree.second > curMax) {
        ret.add(tree.first)
        curMax = tree.second
      }
    }
    return ret
  }

  private fun getVisibleDistance(anchor: Int, trees: Collection<Int>): Int {
    return trees.takeWhileInclusive { it < anchor }.count()
  }
}
