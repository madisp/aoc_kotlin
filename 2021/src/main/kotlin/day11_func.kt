import utils.IntGrid
import utils.Solution
import utils.Vec2i
import utils.withCounts

fun main() {
  Day11Func.run()
}

object Day11Func : Solution<IntGrid>() {
  override val name = "day11"
  override val parser = IntGrid.singleDigits

  private tailrec fun flash(alreadyFlashed: Set<Vec2i>, grid: IntGrid): IntGrid {
    val flashPts = grid.coords.filter { it !in alreadyFlashed && grid[it] == 0 }.toSet()

    val flashSurrounding = flashPts.flatMap { it.surrounding }
      .filter { it !in flashPts && it !in alreadyFlashed }
      .withCounts()

    val flashedGrid = grid.map { coord, value ->
      (value + (flashSurrounding[coord] ?: 0)).coerceAtMost(10) % 10
    }

    if (flashedGrid.coords.none { flashedGrid[it] == 0 && it !in alreadyFlashed && it !in flashPts }) {
      return flashedGrid
    }

    return flash(alreadyFlashed + flashPts, flashedGrid)
  }

  /**
   * Generates a sequence of Grid, Int pairs with the Int representing
   * how many flashes there were that day
   */
  fun evolve(input: IntGrid): Sequence<IntGrid> {
    return generateSequence(input) {
      flash(emptySet(), it.map { _, v -> (v + 1) % 10 })
    }
  }

  override fun part1(input: IntGrid): Int {
    return evolve(input)
      .take(101)
      .map { grid -> grid.values.count { it == 0 } }
      .sum()
  }

  override fun part2(input: IntGrid): Int {
    return evolve(input)
      .takeWhile { grid -> grid.values.any { it != 0 } }
      .count()
  }
}
