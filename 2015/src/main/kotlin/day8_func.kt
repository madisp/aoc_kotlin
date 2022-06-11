import utils.Parser
import utils.Solution

fun main() {
  Day8Func.run()
}

object Day8Func : Solution<List<String>>() {
  override val name = "day8"
  override val parser = Parser.lines

  private fun unescape(input: String): String {
    val slash = input.indexOf('\\')
    if (slash == -1) return input

    val second = input[slash + 1]
    if (second == '\\') {
      return input.substring(0, slash) + "\\" + unescape(input.substring(slash + 2))
    }

    if (second == '"') {
      return input.substring(0, slash) + "\"" + unescape(input.substring(slash + 2))
    }

    if (second == 'x') {
      val hex = input.substring(slash + 2, slash + 4)
      return input.substring(0, slash) + hex.toInt(16).toChar() + unescape(input.substring(slash + 4))
    }

    throw IllegalArgumentException("Illegal escape sequence \\${second} at $slash")
  }

  private fun escape(input: String): String {
    val slash = input.indexOf('\\')
    val quote = input.indexOf('"')
    val escapeIndex = minOf(slash, quote).takeIf { it != -1 } ?: maxOf(slash, quote)

    if (escapeIndex == -1) return input

    return input.substring(0, escapeIndex) + "\\" + input[escapeIndex] + escape(input.substring(escapeIndex + 1))
  }

  override fun part1(input: List<String>): Int {
    return input.sumOf { it.length } - input.sumOf { unescape(it.removeSurrounding("\"")).length }
  }

  override fun part2(input: List<String>): Int {
    return input.sumOf { "\"${escape(it)}\"".length } - input.sumOf { it.length }
  }
}
