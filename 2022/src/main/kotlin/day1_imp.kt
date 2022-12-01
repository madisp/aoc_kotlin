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
    val largestSums = mutableListOf<Int>()
    input.forEach { calories ->
      val sum = calories.sum()
      var biggerCount = 0
      largestSums.forEach { calories2 ->
        if (calories2 >= sum) {
          biggerCount++
        }
      }
      if (biggerCount < 3) {
        largestSums.add(sum)
      }
    }

    var answer = 0
    largestSums.sortDescending()
    (0 until 3).forEach { i ->
      answer += largestSums[i]
    }
    return answer
  }
}
