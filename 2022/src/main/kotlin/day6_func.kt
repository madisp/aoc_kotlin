import utils.Parser
import utils.Solution

fun main() {
  Day6Func.run()
}

object Day6Func : Solution<String>() {
  override val name = "day6"
  override val parser = Parser { it }

  override fun part1(input: String) = solve(input, 4)

  override fun part2(input: String) = solve(input, 14)

  private fun solve(input: String, windowSize: Int): Int {
    return input.trim().toCharArray().toList().windowed(windowSize)
      .map { it.toSet() }
      .indexOfFirst { it.size == windowSize } + windowSize
  }
}
