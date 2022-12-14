import utils.IntGrid
import utils.MutableIntGrid
import utils.Parser
import utils.Segment
import utils.Solution
import utils.Vec2i
import utils.mapItems

fun main() {
  Day14Imp.run()
}

object Day14Imp : Solution<List<List<Segment>>>() {
  override val name = "day14"
  override val parser = Parser.lines.mapItems { line ->
    val parts = line.split(" -> ").map { it.trim() }
    // turn a -> b -> c into "a -> b" and "b -> c"
    parts.windowed(2).map { Segment.parse(it.joinToString(" -> ")) }
  }

  private val source = Vec2i(500, 0)
  private const val SPACE = 0
  private const val ROCK = 1
  private const val SAND = 2
  private val fall = listOf(Vec2i(0, 1), Vec2i(-1, 1), Vec2i(1, 1))

  private fun simulate(grid: MutableIntGrid) {
    while (true) {
      // spawn sand
      var sand = source
      grain@ while (true) {
        if (sand.y == grid.height - 1) {
          return
        }

        val newPos = fall.map { sand + it }.firstOrNull { grid[it] == SPACE }

        if (newPos == null) {
          // at rest
          grid[sand] = SAND
          if (sand == source) return
          break@grain
        }

        sand = newPos
      }
    }
  }

  override fun part1(input: List<List<Segment>>): Any? {
    val bottom = input.flatten().maxOf { maxOf(it.start.y, it.end.y) }
    val grid = IntGrid(1000, bottom + 1, 0).toMutable()

    input.flatten().forEach { seg ->
      seg.points.forEach { p -> grid[p] = ROCK }
    }

    simulate(grid)

    return grid.cells.count { (_, value) -> value == SAND }
  }

  override fun part2(input: List<List<Segment>>): Any? {
    val bottom = input.flatten().maxOf { maxOf(it.start.y, it.end.y) }
    val grid = IntGrid(1000, bottom + 3, 0).toMutable()

    input.flatten().forEach { seg ->
      seg.points.forEach { p -> grid[p] = ROCK }
    }

    (0 until grid.width).forEach { x ->
      grid[x][grid.height - 1] = ROCK
    }

    simulate(grid)

    return grid.cells.count { (_, value) -> value == SAND }
  }
}
