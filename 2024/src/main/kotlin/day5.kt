import utils.DefaultMap
import utils.Parser
import utils.Solution
import utils.cut
import utils.mapItems
import utils.middle
import utils.toInts
import utils.withDefault

fun main() {
  Day5.run()
}

data class Day5In(
  val rules: DefaultMap<Int, List<Int>>,
  val updates: List<List<Int>>,
)

object Day5 : Solution<Day5In>() {
  override val name = "day5"
  override val parser: Parser<Day5In> = Parser.compound(
    Parser.lines.mapItems { it.cut("|").toInts() }.map { rules ->
      rules.groupBy { it.first }.mapValues { (_, v) -> v.map { it.second } }
    },
    Parser.lines.mapItems { update -> update.split(",").map { it.toInt() } }
  ).map { (rules, updates) ->
    Day5In(rules.withDefault(emptyList()), updates)
  }

  private fun List<Int>.sort(): List<Int> = sortedWith { a, b ->
    when {
      a in input.rules[b] -> 1
      b in input.rules[a] -> -1
      else -> 0
    }
  }

  override fun part1(input: Day5In): Int {
    return input.updates.filter { it.sort() == it }.sumOf { it.middle }
  }

  override fun part2(input: Day5In): Int {
    val sorted = input.updates.map { it.sort() }
    return input.updates
      .zip(sorted)
      .filter { (update, correct) -> update != correct }
      .sumOf { (_, correct) -> correct.middle }
  }
}
