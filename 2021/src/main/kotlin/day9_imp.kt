import utils.Grid
import utils.Parser
import utils.Solution
import utils.mapItems

fun main() {
  Day9Imp.run()
}

object Day9Imp : Solution<Grid>() {
  override val name = "day9"
  override val parser = Grid.singleDigits.map { it.borderWith(9) }

  override fun part1(input: Grid): Int {
    var sum = 0

    for (y in 1 until input.height - 1) {
      for (x in 1 until input.width - 1) {
        if (input[x][y] < input[x][y - 1] &&
            input[x][y] < input[x - 1][y] &&
            input[x][y] < input[x][y + 1] &&
            input[x][y] < input[x + 1][y]) {
          sum += 1 + input[x][y]
        }
      }
    }

    return sum
  }

  override fun part2(input: Grid): Number? {
    val visited = Array(input.height) { BooleanArray(input.width) { false } }

    // recursively 4-way-fill at x + y and return the number of cells filled
    fun fill(x: Int, y: Int): Int {
      if (visited[x][y]) {
        return 0
      }
      if (input[x][y] >= 9) {
        return 0
      }
      visited[x][y] = true
      return 1 + fill(y-1, x) + fill(y+1, x) + fill(y, x-1) + fill(y, x+1)
    }

    val sizes = mutableListOf<Int>()

    for (y in 1 until input.height - 1) {
      for (x in 1 until input.width - 1) {
        val sz = fill(x, y)
        if (sz > 0) sizes += sz
      }
    }

    sizes.sortDescending()

    var product = 1
    for (i in 0 until 3) {
      product *= sizes[i]
    }

    return product
  }
}
