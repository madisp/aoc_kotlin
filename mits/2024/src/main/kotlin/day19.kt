import utils.*

fun main() {
  Day19.run()
}

typealias Day19In = List<Int>

object Day19 : Solution<Day19In>() {
  override val name = "day19"
  override val parser: Parser<Day19In> = Parser.ints

  private fun len(start: Int, index: Int, memo: MutableMap<Pair<Int, Int>, Int> = mutableMapOf()): Int {
    return memo.getOrPut(start to index) {
      if (index == input.size) 0 else maxOf(
        if (input[index] <= start) Int.MIN_VALUE else 1 + len(input[index], index + 1, memo),
        len(start, index + 1, memo)
      )
    }
  }

  override fun part1(input: Day19In): Int {
    return len(Int.MIN_VALUE, 0)
  }
}
