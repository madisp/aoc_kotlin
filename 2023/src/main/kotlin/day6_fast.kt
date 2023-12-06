import utils.Solution
import utils.solveQuadratic

fun main() {
  Day6Fast.run(skipTest = false)
}

object Day6Fast : Solution<List<Day6.Race>>() {
  override val name = "day6"
  override val parser = Day6.parser

  private val Day6.Race.winVariants: Int get() {
    val (t1, t2) = solveQuadratic(1.0, -time.toDouble(), record.toDouble())
    return (t2.toInt() - t1.toInt())
  }

  override fun part1(input: List<Day6.Race>): Int {
    return input.map { it.winVariants }.reduce { a, b -> a * b }
  }

  override fun part2(input: List<Day6.Race>): Int {
    return input.reduce { a, b -> a + b }.winVariants
  }
}
