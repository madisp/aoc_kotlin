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
    var smallest = 0
    var r1 = 0
    var r2 = 0

    input.forEach { calories ->
      val sum = calories.sum()
      if (smallest < sum) {
        smallest = sum

        // rebalance smallest between r1 and r2
        if (smallest > r1) {
          smallest = r1
          r1 = sum
        }
        if (smallest > r2) {
          val r3 = r2
          r2 = smallest
          smallest = r3
        }
      }
    }
    return smallest + r1 + r2
  }
}
