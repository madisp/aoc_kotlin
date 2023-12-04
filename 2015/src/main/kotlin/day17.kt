import utils.Parser
import utils.Solution
import utils.combinations

fun main() {
  Day17.run()
}

object Day17 : Solution<List<Int>>() {
  override val name = "day17"
  override val parser = Parser.intLines

  override fun part1(input: List<Int>): Int {
    return input.combinations.count { it.sum() == 150 }
  }

  override fun part2(input: List<Int>): Int {
    val counts = input.combinations.filter { it.sum() == 150 }
      .map { containers -> containers.count() }
      .toList()
    val min = counts.min()
    return counts.count { it == min }
  }
}
