import utils.Parser
import utils.Solution
import utils.parseItems

fun main() {
  Day1Fast.run()
}

object Day1All {
  @JvmStatic fun main(args: Array<String>) {
    mapOf("func" to Day1Func, "imp" to Day1Imp, "fast" to Day1Fast).forEach { (header, solution) ->
      solution.run(
        header = header,
        printParseTime = false
      )
    }
  }
}

object Day1Fast : Solution<List<IntArray>>() {
  override val name = "day1"
  override val parser = Parser.blocks.parseItems(Parser.intLines.map { it.toIntArray() })

  override fun part1(input: List<IntArray>): Int {
    var largestSum = Integer.MIN_VALUE
    input.forEach { calories ->
      val sum = calories.sum()
      if (sum > largestSum) {
        largestSum = sum
      }
    }
    return largestSum
  }

  override fun part2(input: List<IntArray>): Int {
    val threeLargest = IntArray(3) { 0 }
    var answer = 0

    input.forEach { calories ->
      val sum = calories.sum()

      var smallestIndex = -1

      for (i in 0 until 3) {
        if (sum > threeLargest[i] && (smallestIndex == -1 || threeLargest[i] < threeLargest[smallestIndex])) {
          smallestIndex = i
        }
      }

      if (smallestIndex != -1) {
        answer -= threeLargest[smallestIndex]
        threeLargest[smallestIndex] = sum
        answer += sum
      }
    }
    return answer
  }
}
