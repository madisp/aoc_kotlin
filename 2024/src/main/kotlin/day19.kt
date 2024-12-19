import utils.Parser
import utils.Solution

fun main() {
  Day19.run()
}

typealias Day19In = Pair<List<String>, List<String>>

object Day19 : Solution<Day19In>() {
  override val name = "day19"
  override val parser: Parser<Day19In> = Parser.compound(
    first = { it.trim().split(", ") },
    second = Parser.lines
  )

  private fun countForms(target: String, memo: MutableMap<String, Long> = mutableMapOf()): Long {
    if (target.isEmpty()) { return 1L }
    memo[target]?.let { return it }
    return input.first.sumOf {
      if (target.startsWith(it)) {
        countForms(target.substring(it.length), memo)
      } else 0L
    }.also { memo[target] = it }
  }

  override fun part1(input: Day19In): Int {
    return input.second.count { countForms(it) > 0 }
  }

  override fun part2(input: Day19In): Long {
    return input.second.sumOf { countForms(it) }
  }
}
