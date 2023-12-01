import utils.Parser
import utils.Solution

fun main() {
  Day1.run()
}

object Day1 : Solution<List<String>>() {
  override val name = "day1"
  override val parser: Parser<List<String>> = Parser.lines

  private val digits = (0..9).associateBy { it.toString() }
  private val words = mapOf(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9,
  )

  private fun getDigit(dictionary: Map<String, Int>, line: String, index: Int): Int? {
    return dictionary.entries.firstOrNull { line.substring(index).startsWith(it.key) }?.value
  }

  private fun solve(input: List<String>, dictionary: Map<String, Int>): Int {
    return input
      .sumOf { line ->
        val a = line.indices.firstNotNullOf { getDigit(dictionary, line, it) }
        val b = line.indices.reversed().firstNotNullOf { getDigit(dictionary, line, it) }
        a * 10 + b
      }
  }

  override fun part1(input: List<String>): Int {
    return solve(input, digits)
  }

  override fun part2(input: List<String>): Int {
    return solve(input, digits + words)
  }
}
