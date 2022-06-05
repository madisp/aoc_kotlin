import utils.Coord
import utils.Parser
import utils.Solution
import utils.mapItems

fun main() {
  Day3Func.run()
}

enum class Direction(val dx: Int, val dy: Int) {
  UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);

  fun apply(coord: Coord) = Coord(coord.x + dx, coord.y + dy)

  companion object {
    fun fromChar(char: Char): Direction {
      return when (char) {
        '>' -> RIGHT
        '<' -> LEFT
        '^' -> UP
        'v' -> DOWN
        else -> throw IllegalArgumentException("Unacceptable input char $char")
      }
    }
  }
}

object Day3Func : Solution<List<Direction>>() {
  override val name = "day3"
  override val parser = Parser.chars.mapItems { Direction.fromChar(it) }

  override fun part1(input: List<Direction>): Int {
    return getTrack(input).count()
  }

  override fun part2(input: List<Direction>): Number? {
    val (santa, roboSanta) = input.withIndex().partition { value -> value.index % 2 == 0 }
    return (getTrack(santa.map { it.value }).toSet() + getTrack(roboSanta.map { it.value }).toSet()).count()
  }

  private fun getTrack(input: List<Direction>) =
    input.runningFold(Coord(0, 0)) { coord, dir -> dir.apply(coord) }
      .toSet()
}
