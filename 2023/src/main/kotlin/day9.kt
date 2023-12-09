import utils.Parser
import utils.Solution
import utils.mapItems

fun main() {
  Day9.run()
}

object Day9 : Solution<List<List<Int>>>() {
  override val name = "day9"
  override val parser = Parser.lines
    .mapItems { line -> line.split(" ").map { it.trim().toInt() } }

  private fun expand(values: List<Int>): Pair<Int, Int> {
    if (values.all { it == 0}) {
      return 0 to 0
    } else {
      val reduced = expand(values.windowed(2).map { (a, b) -> b - a })
      return values.last() + reduced.first to values.first() - reduced.second
    }
  }

  override fun part1(input: List<List<Int>>): Int {
    return input.sumOf { expand(it).first }
  }

  override fun part2(input: List<List<Int>>): Int {
    return input.sumOf { expand(it).second }
  }
}
