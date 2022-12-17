import utils.IntGrid
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.badInput

fun main() {
  Day17.run()
}

object Day17 : Solution<String>() {
  override val name = "day17"
  override val parser = Parser { it.trim() }

  private val rocks = listOf(
    // ####
    listOf(Vec2i(0, 0), Vec2i(1, 0), Vec2i(2, 0), Vec2i(3, 0)),
    // .#.
    // ###
    // .#.
    listOf(Vec2i(0, 1), Vec2i(1, 1), Vec2i(2, 1), Vec2i(1, 0), Vec2i(1, 2)),
    // ..#
    // ..#
    // ###
    listOf(Vec2i(0, 0), Vec2i(1, 0), Vec2i(2, 0), Vec2i(2, 1), Vec2i(2, 2)),
    // #
    // #
    // #
    // #
    listOf(Vec2i(0, 0), Vec2i(0, 1), Vec2i(0, 2), Vec2i(0, 3)),
    // ##
    // ##
    listOf(Vec2i(0, 0), Vec2i(0, 1), Vec2i(1, 0), Vec2i(1, 1)),
  )

  @Suppress("unused")
  private fun render(grid: IntGrid, height: Int, rock: Int, rockPos: Vec2i) {
    val rockCoords = rocks[rock].map { it + rockPos }.toSet()

    for (y in height downTo 0) {
      println(buildString {
        for (x in 0 until grid.width) {
          if (grid[x][y] == 1) {
            append("#")
          } else if (Vec2i(x, y) in rockCoords) {
            append("@")
          } else {
            append(".")
          }
        }
      })
    }
  }

  override fun part1(input: String): Long {
    return solve(input, 2022)
  }

  override fun part2(input: String): Long {
    return solve(input, 1000000000000)
  }

  private fun solve(input: String, targetRocks: Long): Long {
    var repeatsAdded = false
    var simulatedHeight = 0L

    // input-specific repeat params, test:
    var repeatInjectionHeight = 44
    var heightPerCycle = 53
    var rocksPerCycle = 35

    if (input.length > 50) {
      // not test
      repeatInjectionHeight = 330
      heightPerCycle = 2759
      rocksPerCycle = 1740
    }

    val grid = IntGrid(7, 24000, 0).toMutable()

    var highest = 0
    var rock = 0
    var rockPos = Vec2i(2, 3)
    var inputPos = 0
    var spawnedRocks = 1L

    while (spawnedRocks < targetRocks + 1) {
      // apply input
      val translate = when (input[inputPos]) {
        '>' -> Vec2i(1, 0)
        '<' -> Vec2i(-1, 0)
        else -> badInput()
      }
      inputPos = (inputPos + 1) % input.length
      if (rocks[rock].map { it + rockPos + translate }.all { it.x in 0..6 && grid[it] == 0 }) {
        rockPos += translate
      }

      val rockPts = rocks[rock].map { it + rockPos }

      // apply gravity
      if (rockPts.map { it + Vec2i(0, -1) }.any { it.y < 0 || grid[it] != 0 }) {
        // solidify, spawn new
        rockPts.forEach {
          grid[it] = 1
        }

        highest = maxOf(highest, rockPts.maxOf { it.y } + 1)
        spawnedRocks++
        rock = (rock + 1) % rocks.size
        rockPos = Vec2i(2, highest + 3)
      } else {
        // fall by one
        rockPos += Vec2i(0, -1)
      }

      // pt2 optimization, using hand-calculated repeat values
      if (!repeatsAdded && highest == repeatInjectionHeight) {
        // simulate X cycles out of band
        val cycles = (targetRocks - spawnedRocks) / rocksPerCycle
        spawnedRocks += cycles * rocksPerCycle
        simulatedHeight = cycles * heightPerCycle
        repeatsAdded = true
      }
    }

    return highest + simulatedHeight
  }
}
