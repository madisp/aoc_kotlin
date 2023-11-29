import utils.Parser
import utils.Solution

fun main() {
  Day1.run()
}

object Day1 : Solution<List<Int>>() {
  override val name = "day1"
  override val parser: Parser<List<Int>> = Parser.intLines

  override fun part1(input: List<Int>): Int {
    return input.sumOf { it / 3 - 2 }
  }

  override fun part2(input: List<Int>): Int {
    return input.sumOf { p2fuel(it) }
  }

  private fun p2fuel(mass: Int): Int {
    val fuel = mass / 3 - 2
    return if (fuel > 0) { fuel + p2fuel(fuel) } else { 0 }
  }
}
