import utils.Grid
import utils.MutableGrid
import utils.Solution
import utils.Vec2i

fun main() {
  Day11Imp.run()
}

object Day11Imp : Solution<Grid>() {
  override val name = "day11"
  override val parser = Grid.singleDigits

  fun evolve(grid: MutableGrid): Int {
    val flashing = ArrayDeque<Vec2i>()
    val flashed = mutableSetOf<Vec2i>()

    fun evolve(coord: Vec2i) {
      grid[coord] = grid[coord] + 1
      if (grid[coord] == 10) {
        flashed.add(coord)
        flashing.add(coord)
        grid[coord] = 0
      }
    }

    for (c in grid.coords) {
      evolve(c)
    }

    while (flashing.isNotEmpty()) {
      val c = flashing.removeFirst()
      for (sc in c.surrounding.filter { it in grid && it !in flashed }) {
        evolve(sc)
      }
    }

    return flashed.size
  }

  override fun part1(input: Grid): Int {
    val grid = input.toMutable()
    var totalFlashes = 0
    repeat(100) {
      totalFlashes += evolve(grid)
    }
    return totalFlashes
  }

  override fun part2(input: Grid): Int {
    val grid = input.toMutable()
    for (day in 1 .. Integer.MAX_VALUE) {
      if (evolve(grid) == 100) {
        return day
      }
    }

    throw IllegalStateException("Never reached synchronization!")
  }
}
