import utils.Parser
import utils.Solution
import utils.mapItems
import utils.mergeToMap
import utils.withLCounts

fun main() {
  Day14Func.run()
}

object Day14Func : Solution<Pair<Day14Func.Polymer, List<Pair<String, String>>>>() {
  override val name = "day14"
  override val parser = Parser { input ->
    val (polymer, ruleLines) = input.split("\n\n")
    return@Parser Polymer(polymer) to Parser.lines.mapItems {
      val (from, replacement) = it.split(" -> ")
      from to replacement
    }(ruleLines)
  }

  data class Polymer(val pairs: Map<String, Long>, val chars: Map<String, Long>) {
    constructor(input: String) : this(
      input.windowed(2).withLCounts(),
      input.windowed(1).withLCounts()
    )
  }

  /**
   * doesn't really return a Polymer but more of a delta instead
   */
  private fun deltaByRule(input: Polymer, rule: Pair<String, String>): Polymer {
    val pair = rule.first
    val char = rule.second
    val count = input.pairs[pair] ?: 0L

    if (count == 0L) {
      return Polymer(emptyMap(), emptyMap())
    }

    val ret = Polymer(
      // need to merge because resulting rules can overwrite the original decrement
      listOf(
        pair to 0 - count,
        pair.first() + char to count,
        char + pair.last() to count
      ).mergeToMap { _, c1, c2 -> c1 + c2 },
      mapOf(char to count)
    )

    return ret
  }

  private fun polymerize(input: Polymer, rules: List<Pair<String, String>>): Polymer {
    val deltas = rules.map { deltaByRule(input, it) }
    return (deltas + input).reduce { acc, polymer ->
      val reduced = Polymer(
        (acc.pairs.toList() + polymer.pairs.toList()).mergeToMap { _, c1, c2 -> c1 + c2 },
        (acc.chars.toList() + polymer.chars.toList()).mergeToMap { _, c1, c2 -> c1 + c2 }
      )
      return@reduce reduced
    }
  }

  private fun solve(steps: Int, input: Pair<Polymer, List<Pair<String, String>>>): Long {
    val answ = (0 until steps).fold(input.first) { acc, _ -> polymerize(acc, input.second) }
      .chars.entries.sortedBy { it.value }
    return answ.last().value - answ.first().value
  }

  override fun part1(input: Pair<Polymer, List<Pair<String, String>>>): Long {
    return solve(10, input)
  }

  override fun part2(input: Pair<Polymer, List<Pair<String, String>>>): Long {
    return solve(40, input)
  }
}