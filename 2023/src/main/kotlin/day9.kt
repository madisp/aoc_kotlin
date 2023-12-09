import utils.Parser
import utils.Solution
import utils.mapItems

fun main() {
  Day9.run()
}

object Day9 : Solution<List<List<Long>>>() {
  override val name = "day9"
  override val parser = Parser.lines.mapItems { Parser.spacedLongs(it) }

  private fun expand(values: List<Long>): Pair<Long, Long> {
    if (values.all { it == 0 }) {
      return 0 to 0
    } else {
      val reduced = expand(values.windowed(2).map { (a, b) -> b - a })
      return values.last() + reduced.first to values.first() - reduced.second
    }
  }

  override fun part1(input: List<List<Long>>): Long {
    return input.sumOf { expand(it).first }
  }

  override fun part2(input: List<List<Long>>): Long {
    return input.sumOf { expand(it).second }
  }
}
