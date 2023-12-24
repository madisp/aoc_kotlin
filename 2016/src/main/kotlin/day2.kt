import utils.Grid
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.mapItems

fun main() {
  Day2.run()
}

object Day2 : Solution<List<String>>() {
  override val name = "day2"
  override val parser = Parser.lines

  val pad = Parser.charGrid("123\n456\n789")
  val pad2 = Parser.charGrid("00100\n02340\n56789\n0ABC0\n00D00")

  val dirs = mapOf(
    'U' to Vec2i.UP,
    'L' to Vec2i.LEFT,
    'R' to Vec2i.RIGHT,
    'D' to Vec2i.DOWN,
  )

  override fun part1(): String {
    return solve(pad)
  }

  override fun part2(): String {
    return solve(pad2)
  }

  private fun solve(pad: Grid<Char>): String {
    var location = pad.cells.first { (_, c) -> c == '5' }.first
    val out = StringBuilder()

    input.forEach { line ->
      line.forEach { c ->
        location = dirs[c]?.let { location + it }?.takeIf { it in pad && pad[it] != '0' } ?: location
      }
      out.append(pad[location])
    }

    return out.toString()
  }
}
