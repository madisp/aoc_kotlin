import utils.Grid
import utils.MutableGrid
import utils.Parser
import utils.Solution

fun main() {
  Day20Fast.run()
}

object Day20All {
  @JvmStatic fun main(args: Array<String>) {
    mapOf("func" to Day20Func, "fast" to Day20Fast).forEach { (header, solution) ->
      solution.run(header = header, skipPart1 = false, skipTest = false, printParseTime = true)
    }
  }
}

object Day20Fast : Solution<Pair<Grid, List<Int>>>() {
  override val name = "day20"
  override val parser = Parser { input ->
    val (lookupString, imageString) = input.split("\n\n", limit = 2)

    val lookup = lookupString.replace("\n", "").trim().map { if (it == '#') 1 else 0 }
    val gridLines = imageString.split("\n").map { it.trim() }.filter { it.isNotBlank() }

    val gridW = gridLines.first().length
    val gridH = gridLines.size

    val grid = Grid(gridW, gridH) { (x, y) -> if (gridLines[y][x] == '#') 1 else 0 }

    return@Parser grid to lookup
  }

  fun enhance(input: Grid, output: MutableGrid, lookup: List<Int>) {
    val padding = lookup[if (input[0][0] == 0) 0 else 511]

    val width = input.width
    val height = input.height

    for (x in 0 until width) {
      output[x][0] = padding
      output[x][height - 1] = padding
    }

    for (y in 0 until height) {
      output[0][y] = padding
      output[width - 1][y] = padding
    }

    for (x in 1 until width - 1) {
      for (y in 1 until height - 1) {
        val outIndex =
          (input[x-1][y-1] shl 8) or
          (input[x][y-1] shl 7) or
          (input[x+1][y-1] shl 6) or
          (input[x-1][y] shl 5) or
          (input[x][y] shl 4) or
          (input[x+1][y] shl 3) or
          (input[x-1][y+1] shl 2) or
          (input[x][y+1] shl 1) or
          (input[x+1][y+1])
        output[x][y] = lookup[outIndex]
      }
    }
  }

  override fun part1(input: Pair<Grid, List<Int>>): Int {
    val output = solve(input, 2)
    return output.values.count { it != 0 }
  }

  override fun part2(input: Pair<Grid, List<Int>>): Int {
    val output = solve(input, 50)
    return output.values.count { it != 0 }
  }

  private fun solve(input: Pair<Grid, List<Int>>, days: Int): MutableGrid {
    val b1 = input.first.borderWith(0, days + 1).toMutable()
    val w = b1.width
    val h = b1.height

    val b2 = Grid(w, h) { 0 }.toMutable()

    repeat(days) { day ->
      if (day % 2 == 0) {
        enhance(b1, b2, input.second)
      } else {
        enhance(b2, b1, input.second)
      }
    }

    return if (days % 2 == 0) b1 else b2
  }
}
