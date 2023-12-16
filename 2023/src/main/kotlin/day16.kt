import utils.Grid
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.createMutableGrid
import java.util.EnumSet

fun main() {
  Day16.run()
}

object Day16 : Solution<Grid<Char>>() {
  override val name = "day16"
  override val parser = Parser.charGrid

  data class Beam(
    val location: Vec2i,
    val direction: Direction,
  )

  enum class Direction(val delta: Vec2i) {
    UP(Vec2i(0, -1)),
    DOWN(Vec2i(0, 1)),
    LEFT(Vec2i(-1, 0)),
    RIGHT(Vec2i(1, 0));

    companion object {
      fun byVec(v: Vec2i): Direction {
        return Direction.entries.first { it.delta == v }
      }
    }
  }

  private fun solve(start: Beam): Int {
    val beams = mutableListOf(start)
    val seen = createMutableGrid<Set<Direction>>(input.width, input.height) { EnumSet.noneOf(Direction::class.java) }

    while (beams.isNotEmpty()) {
      val beam = beams.removeLast()
      val nextLocation = beam.location + beam.direction.delta

      if (nextLocation.x !in 0 until input.width || nextLocation.y !in 0 until input.height) {
        // out of bounds
        continue
      }

      if (beam.direction in seen[nextLocation]) {
        // already tracked a beam going that way
        continue
      }

      seen[nextLocation] = seen[nextLocation] + beam.direction
      val newBeams = when (input[nextLocation]) {
        '.' -> listOf(Beam(nextLocation, beam.direction))
        '-' -> if (beam.direction.delta.y == 0) {
          listOf(Beam(nextLocation, beam.direction))
        } else {
          listOf(Beam(nextLocation, Direction.LEFT), Beam(nextLocation, Direction.RIGHT))
        }
        '|' -> if (beam.direction.delta.x == 0) {
          listOf(Beam(nextLocation, beam.direction))
        } else {
          listOf(Beam(nextLocation, Direction.UP), Beam(nextLocation, Direction.DOWN))
        }
        '\\' -> if (beam.direction.delta.y == 0) {
          listOf(Beam(nextLocation, Direction.byVec(beam.direction.delta.rotateCcw())))
        } else {
          listOf(Beam(nextLocation, Direction.byVec(beam.direction.delta.rotateCw())))
        }
        '/' -> if (beam.direction.delta.y == 0) {
          listOf(Beam(nextLocation, Direction.byVec(beam.direction.delta.rotateCw())))
        } else {
          listOf(Beam(nextLocation, Direction.byVec(beam.direction.delta.rotateCcw())))
        }
        else -> throw IllegalStateException("Bad grid char ${input[beam.location]}")
      }
      beams.addAll(newBeams)
    }

    return seen.values.count { it.isNotEmpty() }
  }

  override fun part1(): Int {
    return solve(Beam(Vec2i(-1, 0), Direction.RIGHT))
  }

  override fun part2(): Any? {
    val entries = listOf(
      (0 until input.height).flatMap {
        listOf(
          Beam(Vec2i(-1, it), Direction.RIGHT),
          Beam(Vec2i(input.width, it), Direction.LEFT)
        )
      },
      (0 until input.width).flatMap {
        listOf(
          Beam(Vec2i(it, -1), Direction.DOWN),
          Beam(Vec2i(it, input.height), Direction.UP)
        )
      },
    ).flatten()

    return entries.maxOf { solve(it) }
  }
}
