import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.mapItems

fun main() {
  Day3Func.run()
}

fun Char.toVec() = when (this) {
  '>' -> Vec2i(1, 0)
  '<' -> Vec2i(-1, 0)
  '^' -> Vec2i(0, -1)
  'v' -> Vec2i(0, 1)
  else -> throw IllegalArgumentException("Unacceptable input char $this")
}

object Day3Func : Solution<List<Vec2i>>() {
  override val name = "day3"
  override val parser = Parser.chars.mapItems { it.toVec() }

  override fun part1(input: List<Vec2i>): Int {
    return getTrack(input).count()
  }

  override fun part2(input: List<Vec2i>): Number? {
    val (santa, roboSanta) = input.withIndex()
      .partition { value -> value.index % 2 == 0 }
      .toList()
      .map { list -> list.map { it.value } }
    return (getTrack(santa) + getTrack(roboSanta)).count()
  }

  private fun getTrack(input: List<Vec2i>) =
    input.runningFold(Vec2i(0, 0)) { location, vec -> location + vec }
      .toSet()
}
