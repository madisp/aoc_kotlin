import utils.Parser
import utils.Solution

fun main() {
  Day5Func.run()
}

object Day5Func : Solution<List<String>>() {
  override val name = "day5"
  override val parser = Parser.lines

  override fun part1(input: List<String>): Int {
    return input
      .filter { word -> word.toCharArray().filter { it in setOf('a', 'e', 'i', 'o', 'u') }.count() >= 3 }
      .filter { word -> word.toCharArray().toList().windowed(size = 2).any { (a, b) -> a == b } }
      .filter { word -> listOf("ab", "cd", "pq", "xy").none { it in word } }
      .count()
  }

  override fun part2(input: List<String>): Int {
    return input
      .filter { word -> word.toCharArray().toList().windowed(size = 3).any { (a, _, b) -> a == b } }
      .filter { word ->
        val subSequences = word.toCharArray().toList().windowed(size = 2).map { it.joinToString("") }.withIndex()
        subSequences.any { a ->
          subSequences.filter { it.value == a.value }.any { b ->
            a.index - b.index >= 2
          }
        }
      }
      .count()
  }
}
