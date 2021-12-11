import utils.Parser
import utils.mapItems

fun main() {
  Day9Imp.run()
}

object Day9Imp : Solution<Day9Imp.Grid> {
  override val name = "day9"
  override val parser = Parser.lines.mapItems {
    listOf(10) + it.trim().toCharArray().map { cell -> cell.code - '0'.code } + listOf(10)
  }.map { Grid(listOf(List(it.first().size) { 10 }) + it + listOf(List(it.first().size) { 10 })) }

  override fun part1(input: Grid): Int {
    var sum = 0

    for (y in 1 until input.height - 1) {
      for (x in 1 until input.width - 1) {
        if (input[y][x] < input[y - 1][x] &&
            input[y][x] < input[y][x - 1] &&
            input[y][x] < input[y + 1][x] &&
            input[y][x] < input[y][x + 1]) {
          sum += 1 + input[y][x]
        }
      }
    }

    return sum
  }

  override fun part2(input: Grid): Number? {
    val visited = Array(input.height) { BooleanArray(input.width) { false } }

    // recursively 4-way-fill at x + y and return the number of cells filled
    fun fill(y: Int, x: Int): Int {
      if (visited[y][x]) {
        return 0
      }
      if (input[y][x] >= 9) {
        return 0
      }
      visited[y][x] = true
      return 1 + fill(y-1, x) + fill(y+1, x) + fill(y, x-1) + fill(y, x+1)
    }

    val sizes = mutableListOf<Int>()

    for (y in 1 until input.height - 1) {
      for (x in 1 until input.width - 1) {
        val sz = fill(y, x)
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

  data class Grid(val rows: List<List<Int>>) {
    val width = rows.first().size
    val height = rows.size

    operator fun get(y: Int): List<Int> {
      return rows[y]
    }
  }
}
