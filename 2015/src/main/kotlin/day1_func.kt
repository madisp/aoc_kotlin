import utils.Parser
import utils.Solution
import utils.mapItems

fun main() {
  Day1Func.run()
}

object Day1Func : Solution<List<Int>>() {
  override val name = "day1"
  override val parser = Parser.chars.mapItems {
    when (it) {
      '(' -> 1
      ')' -> -1
      else -> throw IllegalArgumentException("Unexpected char $it")
    }
  }

  override fun part1(input: List<Int>): Int {
    return input.sum()
  }

  override fun part2(input: List<Int>): Int {
    return input.asSequence().runningReduce { acc, item -> acc + item }
      .takeWhile { it > -1 }
      .count() + 1
  }
}
