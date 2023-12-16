import utils.Grid
import utils.MutableIntGrid
import utils.Parser
import utils.Solution
import utils.Vec2i

fun main() {
  Day16.run()
}

object Day16 : Solution<Grid<Char>>() {
  override val name = "day16"
  override val parser = Parser.charGrid

  private val directions = listOf(Vec2i.UP, Vec2i.DOWN, Vec2i.LEFT, Vec2i.RIGHT)

  private const val UP = 0
  private const val DOWN = 1
  private const val LEFT = 2
  private const val RIGHT = 3

  private val cw = listOf(RIGHT, LEFT, UP, DOWN)
  private val ccw = listOf(LEFT, RIGHT, DOWN, UP)

  data class Beam(
    val location: Vec2i,
    val direction: Int,
  )

  private fun solve(start: Beam): Int {
    val beams = mutableListOf(start)
    val seen = MutableIntGrid(IntArray(input.width * input.height), input.width, input.height)

    while (beams.isNotEmpty()) {
      val beam = beams.removeLast()
      val dir = directions[beam.direction]
      val nextLocation = beam.location + dir

      if (nextLocation.x !in 0 until input.width || nextLocation.y !in 0 until input.height) {
        // out of bounds
        continue
      }

      if ((1 shl beam.direction) and seen[nextLocation] != 0) {
        // already tracked a beam going that way
        continue
      }


      seen[nextLocation] = seen[nextLocation] or (1 shl beam.direction)
      val newBeams = when (input[nextLocation]) {
        '.' -> listOf(Beam(nextLocation, beam.direction))
        '-' -> if (dir.y == 0) {
          listOf(Beam(nextLocation, beam.direction))
        } else {
          listOf(Beam(nextLocation, LEFT), Beam(nextLocation, RIGHT))
        }
        '|' -> if (dir.x == 0) {
          listOf(Beam(nextLocation, beam.direction))
        } else {
          listOf(Beam(nextLocation, UP), Beam(nextLocation, DOWN))
        }
        '\\' -> if (dir.y == 0) {
          listOf(Beam(nextLocation, cw[beam.direction]))
        } else {
          listOf(Beam(nextLocation, ccw[beam.direction]))
        }
        '/' -> if (dir.y == 0) {
          listOf(Beam(nextLocation, ccw[beam.direction]))
        } else {
          listOf(Beam(nextLocation, cw[beam.direction]))
        }
        else -> throw IllegalStateException("Bad grid char ${input[beam.location]}")
      }
      beams.addAll(newBeams)
    }

    return seen.values.count { it != 0 }
  }

  override fun part1(): Int {
    return solve(Beam(Vec2i(-1, 0), RIGHT))
  }

  override fun part2(): Int {
    val startLocations = listOf(
      (0 until input.height).flatMap {
        listOf(
          Beam(Vec2i(-1, it), RIGHT),
          Beam(Vec2i(input.width, it), LEFT)
        )
      },
      (0 until input.width).flatMap {
        listOf(
          Beam(Vec2i(it, -1), DOWN),
          Beam(Vec2i(it, input.height), UP)
        )
      },
    ).flatten()

    return startLocations.maxOf { solve(it) }
  }
}
