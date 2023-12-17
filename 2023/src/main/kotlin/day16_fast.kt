import utils.IntGrid
import utils.MutableIntGrid
import utils.Parser
import utils.Solution
import utils.Vec2i

fun main() {
  Day16Fast.run()
}

object Day16Fast : Solution<IntGrid>() {
  override val name = "day16"
  override val parser = Parser.charGrid.map {
    IntGrid(it.width, it.height) { p ->
      when (it[p]) {
        '.' -> AIR
        '|' -> SPLITV
        '-' -> SPLITH
        '/' -> MIRROR_LR
        '\\' -> MIRROR_RL
        else -> throw IllegalArgumentException("Bad input char ${it[p]} at $p")
      }
    }
  }

  private val directions = listOf(Vec2i.UP, Vec2i.DOWN, Vec2i.LEFT, Vec2i.RIGHT)

  private const val AIR = 0
  private const val SPLITV = 1
  private const val SPLITH = 2
  private const val MIRROR_LR = 3
  private const val MIRROR_RL = 4

  private const val UP = 0
  private const val DOWN = 1
  private const val LEFT = 2
  private const val RIGHT = 3

  private val cw = listOf(RIGHT, LEFT, UP, DOWN)
  private val ccw = listOf(LEFT, RIGHT, DOWN, UP)

  private fun solve(start: Int): Int {
    val beams = IntArray(128)
    beams[0] = start
    var beamsSize = 1
    val seen = MutableIntGrid(IntArray(input.width * input.height), input.width, input.height)
    var energizedCount = 0

    while (beamsSize > 0) {
      val beam = beams[--beamsSize]

      val direction = beam and 0b11
      val x = (beam ushr 2 and 0xff) - 1
      val y = (beam ushr 10 and 0xff) - 1
      val location = Vec2i(x, y)

      val dir = directions[direction]
      val nextLocation = location + dir

      if (nextLocation.x !in 0 until input.width || nextLocation.y !in 0 until input.height) {
        // out of bounds
        continue
      }

      val seenAtNext = seen[nextLocation]
      if (seenAtNext == 0) {
        energizedCount++
      } else if ((1 shl direction) and seenAtNext != 0) {
        // already tracked a beam going that way
        continue
      }

      val nextLocationPacked = ((nextLocation.x + 1) shl 2) or ((nextLocation.y + 1) shl 10)

      seen[nextLocation] = seen[nextLocation] or (1 shl direction)
      when (input[nextLocation]) {
        AIR -> beams[beamsSize++] = nextLocationPacked or direction
        SPLITH -> if (dir.y == 0) {
          beams[beamsSize++] = nextLocationPacked or direction
        } else {
          beams[beamsSize++] = nextLocationPacked or LEFT
          beams[beamsSize++] = nextLocationPacked or RIGHT
        }
        SPLITV -> if (dir.x == 0) {
          beams[beamsSize++] = nextLocationPacked or direction
        } else {
          beams[beamsSize++] = nextLocationPacked or UP
          beams[beamsSize++] = nextLocationPacked or DOWN
        }
        MIRROR_RL -> if (dir.y == 0) {
          beams[beamsSize++] = nextLocationPacked or cw[direction]
        } else {
          beams[beamsSize++] = nextLocationPacked or ccw[direction]
        }
        MIRROR_LR -> if (dir.y == 0) {
          beams[beamsSize++] = nextLocationPacked or ccw[direction]
        } else {
          beams[beamsSize++] = nextLocationPacked or cw[direction]
        }
        else -> throw IllegalStateException("Bad grid char ${input[location]}")
      }
    }

    return energizedCount
  }

  override fun part1(): Int {
    return solve((1 shl 10) or RIGHT)
  }

  override fun part2(): Int {
    val inputs = mutableListOf<Int>()
    for (y in 0 until input.height) {
      inputs += (y + 1 shl 10) or RIGHT
      inputs += (input.width + 1 shl 2) or (y + 1 shl 10) or LEFT
    }
    for (x in 0 until input.width) {
      inputs += (x + 1 shl 2) or DOWN
      inputs += (x + 1 shl 2) or (input.height + 1 shl 10) or UP
    }

    return inputs.maxOf { solve(it) }
  }
}
