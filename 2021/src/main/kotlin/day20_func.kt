import utils.Grid
import utils.Parser

fun main() {
  Day20Func.run()
}

object Day20Func : Solution<Pair<Grid, List<Int>>> {
  override val name = "day20"
  override val parser = Parser { input ->
    val (lookupString, imageString) = input.split("\n\n", limit = 2)

    val lookup = lookupString.replace("\n", "").trim().map { if (it == '#') 1 else 0 }
    val gridLines = imageString.split("\n").map { it.trim() }.filter { it.isNotBlank() }

    val gridW = gridLines.first().length
    val gridH = gridLines.size

    val grid = Grid(gridW, gridH) { (x, y) -> if (gridLines[y][x] == '#') 1 else 0 }

    return@Parser grid.borderWith(0, borderWidth = 2) to lookup
  }

  fun enhance(input: Grid, lookup: List<Int>): Grid {
    val padding = if (input[0][0] == 0) lookup[0] else lookup[511]

    return Grid(input.width, input.height) { coord ->
      if (coord.x < 1 || coord.y < 1 || coord.x >= input.width - 1 || coord.y >= input.height - 1) {
        return@Grid padding
      }
      val lookupIndex = (coord.y - 1 .. coord.y + 1).flatMapIndexed { yPos, y ->
        (coord.x - 1 .. coord.x + 1).mapIndexed { xPos, x ->
          val shift = (8 - (yPos * 3 + xPos))
          require(shift in 0 .. 8) { "Shift out of bounds" }
          input[x][y] shl shift
        }
      }.sum()

      require(lookupIndex <= lookup.size) { "Lookup index too large!" }

      return@Grid lookup[lookupIndex]
    }.borderWith(padding)
  }

  override fun part1(input: Pair<Grid, List<Int>>): Int {
    val (grid, lookup) = input
    val round2 = (0 until 2).fold(grid) { acc, _ -> enhance(acc, lookup) }
    return round2.values.count { it > 0 }
  }

  override fun part2(input: Pair<Grid, List<Int>>): Int {
    val (grid, lookup) = input

    val result = (0 until 50).fold(grid) { acc, _ -> enhance(acc, lookup) }

    return result.values.count { it > 0 }
  }
}
