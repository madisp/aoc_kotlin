import utils.IntGrid
import utils.Solution

fun main() {
  Day11Fast.run()
}

object Day11All {
  @JvmStatic fun main(args: Array<String>) {
    mapOf("func" to Day11Func, "imp" to Day11Imp, "fast" to Day11Fast).forEach { (header, solution) ->
      solution.run(header = header, skipPart1 = false, skipTest = false, printParseTime = false)
    }
  }
}

object Day11Fast : Solution<IntGrid>() {
  override val name = "day11"
  override val parser = IntGrid.singleDigits

  fun evolve(grid: IntArray): Int {
    val sz = 10
    val flashing = ArrayDeque<Int>()
    val flashed = BooleanArray(sz * sz) { false }
    var flashes = 0

    fun evolve(index: Int) {
      if (++grid[index] == 10) {
        flashes++
        flashed[index] = true
        flashing.add(index)
        grid[index] = 0
      }
    }

    fun check(index: Int) {
      if (index < 0 || index >= 100) return
      if (flashed[index]) return
      evolve(index)
    }

    for (i in 0 until sz*sz) {
      evolve(i)
    }

    while (flashing.isNotEmpty()) {
      val i = flashing.removeFirst()

      val col = i % 10

      if (col != 0) {
        check(i - 1 - sz) // top-left
        check(i - 1)      // left
        check(i - 1 + sz) // bottom-left
      }

      if (col != 9) {
        check(i + 1 - sz) // top-right
        check(i + 1)      // right
        check(i + 1 + sz) // bottom-right
      }

      check(i - sz)     // top
      check(i + sz)     // bottom
    }

    return flashes
  }

  override fun part1(input: IntGrid): Int {
    val grid = input.values.toIntArray()
    var totalFlashes = 0
    repeat(100) {
      totalFlashes += evolve(grid)
    }
    return totalFlashes
  }

  override fun part2(input: IntGrid): Int {
    val grid = input.values.toIntArray()
    for (day in 1 .. Integer.MAX_VALUE) {
      if (evolve(grid) == 100) {
        return day
      }
    }

    throw IllegalStateException("Never reached synchronization!")
  }
}
