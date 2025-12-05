package year2025

import utils.Parser
import utils.Solution
import utils.mapItems
import utils.mergeWith
import utils.toLongRange

typealias Day5In = Pair<List<LongRange>, List<Long>>

object Day5 : Solution<Day5In>() {
  override val name = "day5"
  override val parser: Parser<Day5In> = Parser.compound(
    Parser.lines.mapItems { it.toLongRange() },
    Parser.longLines
  )

  override fun part1(input: Day5In): Int {
    return input.second.count { ingredient -> input.first.any { ingredient in it } }
  }

  override fun part2(input: Day5In): Long {
    val queue = input.first.toMutableList()
    val joined = input.first.toMutableSet()
    while (queue.isNotEmpty()) {
      val r = queue.removeLast()
      val p = joined.firstNotNullOfOrNull { o -> (r mergeWith o)?.let { o to it } }
      if (p != null) {
        joined.remove(p.first)
        queue.add(p.second)
      } else {
        joined.add(r)
      }
    }
    return joined.sumOf { it.last + 1 - it.first }
  }
}
