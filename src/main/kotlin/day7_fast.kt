import utils.Parser
import kotlin.math.abs

fun main() {
  Day7Fast.run()
}

object Day7All {
  @JvmStatic fun main(args: Array<String>) {
    mapOf("func" to Day7Func, "imp" to Day7Imp, "fast" to Day7Fast).forEach { (header, solution) ->
      solution.run(header = header, skipPart1 = true, skipTest = true, printParseTime = false)
    }
  }
}

object Day7Fast : Solution<List<Int>> {
  override val name = "day7"
  override val parser = Parser.ints

  override fun part1(crabs: List<Int>): Int {
    return solve(crabs, this::identity)
  }

  override fun part2(crabs: List<Int>): Int {
    return solve(crabs, this::cost)
  }

  private fun solve(crabs: List<Int>, costFn: (Int) -> Int): Int {
    fun totalFuel(target: Int): Int {
      return crabs.sumOf { costFn(abs(it - target)) }
    }

    var lower = 0
    var upper = crabs.maxOrNull()!!

    while (lower != upper) {
      val mid = (lower + upper) / 2

      if (mid > lower && totalFuel(mid) < totalFuel(mid - 1)) {
        lower = mid
      } else {
        upper = mid
      }
    }

    return totalFuel(lower)
  }

  private fun identity(distance: Int) = distance
  private fun cost(distance: Int) = distance * (distance + 1) / 2
}