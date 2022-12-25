import utils.Graph
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.badInput
import utils.mapParser

fun main() {
  Day25.run(skipPart2 = true)
}

object Day25 : Solution<List<String>>() {
  override val name = "day25"
  override val parser = Parser.lines

  private fun parseSnafuNumber(s: String): Long {
    return s.reversed().map {
      when (it) {
        '2' -> 2L
        '1' -> 1L
        '0' -> 0L
        '-' -> -1L
        '=' -> -2L
        else -> badInput()
      }
    }.foldRight(0) { n, acc -> acc * 5L + n }
  }

  private fun toSnafuNumber(number: Long): String {
    if (number == 0L) return "0"
    return buildString {
      var num = number
      while (num != 0L) {
        val bit = num % 5L
        if (bit == 3L) {
          append("=")
          num += 5
        } else if (bit == 4L) {
          append("-")
          num += 5
        } else if {
          append(bit.toString())
        }
        num /= 5L
      }
    }.reversed()
  }

  override fun part1(input: List<String>): String {
    val sum = input.sumOf { parseSnafuNumber(it) }
    return toSnafuNumber(sum)
  }
}
