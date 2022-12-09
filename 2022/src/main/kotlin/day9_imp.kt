import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.badInput
import utils.cut
import kotlin.math.sign

fun main() {
  Day9Imp.run()
}

enum class Direction(val delta: Vec2i) {
  UP(Vec2i(0, 1)),
  RIGHT(Vec2i(1, 0)),
  DOWN(Vec2i(0, -1)),
  LEFT(Vec2i(-1, 0))
}

object Day9Imp : Solution<List<Direction>>() {
  override val name = "day9"
  override val parser = Parser.lines.map { lines ->
    lines.flatMap { line ->
      val (dir, count) = line.cut(" ", {
        when (it) {
          "R" -> Direction.RIGHT
          "D" -> Direction.DOWN
          "U" -> Direction.UP
          "L" -> Direction.LEFT
          else -> badInput()
        }
      }, { it.toInt() })
      (0 until count).map { dir }
    }
  }

  private fun rubberBand(tail: Vec2i, head: Vec2i): Vec2i {
    if (tail in head.grow()) {
      // tail doesn't move
      return Vec2i(0, 0)
    }
    return Vec2i((head.x - tail.x).sign, (head.y - tail.y).sign)
  }

  override fun part1(input: List<Direction>): Int {
    return solve(input, 2)
  }

  override fun part2(input: List<Direction>): Int {
    return solve(input, 10)
  }

  private fun solve(input: List<Direction>, knotCount: Int): Int {
    val tailPositions = mutableSetOf<Vec2i>()
    val knots = MutableList(knotCount) { Vec2i(0, 0) }
    tailPositions.add(knots.last())

    for (move in input) {
      knots[0] = knots[0] + move.delta
      for (i in 1 until knots.size) {
        knots[i] = knots[i] + rubberBand(knots[i], knots[i - 1])
      }
      tailPositions.add(knots.last())
    }

    return tailPositions.size
  }
}
