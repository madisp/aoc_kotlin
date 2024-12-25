import utils.*

fun main() {
  Day23.run()
}

typealias Day23In = List<String>

object Day23 : Solution<Day23In>() {
  override val name = "day23"
  override val parser: Parser<Day23In> = Parser.lines

  private fun isBalanced(line: String): Boolean {
    var cur = 0
    line.forEach { c ->
      when (c) {
        '(' -> cur++
        ')' -> cur--
      }
      if (cur < 0) return false
    }
    return cur == 0
  }

  override fun part1(input: Day23In): Int {
    return input.count { isBalanced(it) }
  }
}
