import utils.IntGrid
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.wrap

fun main() {
  Day25Imp.run()
}

object Day25Imp : Solution<IntGrid>() {
  override val name = "day25"

  private const val EMPTY = 0
  private const val EAST = 1
  private const val SOUTH = 2

  override val parser = Parser { input -> input
    .replace('.', '0')
    .replace('>', '1')
    .replace('v', '2')
  }.map { IntGrid.singleDigits(it) }

  override fun part1(input: IntGrid): Number? {
    val grid = input.toMutable()

    var steps = 0

    val swaps = mutableSetOf<Pair<Vec2i, Vec2i>>()

    while (true) {
      var moves = 0
      // move to east first..
      for (x in 0 until grid.width) {
        for (y in 0 until grid.height) {
          val toX = (x + 1).wrap(grid.width)
          if (grid[x][y] == EAST && grid[toX][y] == EMPTY) {
            moves++
            swaps.add(Vec2i(x, y) to Vec2i(toX, y))
          }
        }
      }

      for (swap in swaps) {
        grid.swap(swap.first, swap.second)
      }
      swaps.clear()

      // ..then move to south
      for (x in 0 until grid.width) {
        for (y in 0 until grid.height) {
          val toY = (y + 1).wrap(grid.height)
          if (grid[x][y] == SOUTH && grid[x][toY] == EMPTY) {
            moves++
            swaps.add(Vec2i(x, y) to Vec2i(x, toY))
          }
        }
      }

      for (swap in swaps) {
        grid.swap(swap.first, swap.second)
      }
      swaps.clear()

      steps++

      if (moves == 0) {
        break
      }
    }

    return steps
  }

  fun IntGrid.print() {
    for (y in 0 until height) {
      for (x in 0 until width) {
        print(when (this[x][y]) {
          EAST -> '>'
          SOUTH -> 'v'
          else -> '.'
        })
      }
      println()
    }
  }
}
