import utils.Parser
import utils.Solution
import utils.parseItems

fun main() {
  Day1Func.run()
}

object Day1Func : Solution<List<List<Int>>>() {
  override val name = "day1"
  override val parser = Parser.blocks.parseItems(Parser.intLines)

  override fun part1(input: List<List<Int>>): Int {
    return input.maxOf { it.sum() }
  }

  override fun part2(input: List<List<Int>>): Int {
    return input.map { it.sum() }
      .sortedDescending()
      .take(3)
      .sum()
  }
}
