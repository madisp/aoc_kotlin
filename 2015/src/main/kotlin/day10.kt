import utils.Parser
import utils.Solution

fun main() {
  Day10.run(skipTest = false)
}

object Day10 : Solution<String>() {
  override val name = "day10"
  override val parser = Parser { it.trim() }

  private fun next(input: String): String {
    return buildString {
      var prev = -1
      var count = 0
      input.chars().forEach { c ->
        val num = c - '0'.code
        if (num != prev) {
          if (prev != -1) {
            append(count)
            append(prev)
          }
          prev = num
          count = 1
        } else {
          count++
        }
      }
      append(count)
      append(prev)
    }
  }

  override fun part1(input: String): Int {
    return generateSequence(input) { next(it) }
      .drop(50)
      .first().length
  }
}
