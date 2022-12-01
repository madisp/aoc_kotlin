import utils.Parser
import utils.Solution
import utils.parseItems

fun main() {
  Day1Imp.run()
}

object Day1Imp : Solution<List<List<Int>>>() {
  override val name = "day1"
  override val parser = Parser.blocks.parseItems(Parser.intLines)

  override fun part1(input: List<List<Int>>): Int {
    var largestSum = Integer.MIN_VALUE
    input.forEach { calories ->
      val sum = calories.sum()
      if (sum > largestSum) {
        largestSum = sum
      }
    }
    return largestSum
  }

  override fun part2(input: List<List<Int>>): Int {
    val sums = mutableListOf<Int>()
    input.forEach { calories ->
      sums.add(calories.sum())
    }
    sums.sortDescending()
    return sums[0] + sums[1] + sums[2]
  }
}
