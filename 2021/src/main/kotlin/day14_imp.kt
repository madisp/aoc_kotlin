import utils.Parser
import utils.mapItems

fun main() {
  Day14Imp.run()
}

object Day14Imp : Solution<Pair<Day14Imp.Polymer, List<Pair<String, String>>>> {
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

  private fun solve(steps: Int, input: Pair<Polymer, List<Pair<String, String>>>): Long {
    val pairs = input.first.pairs.toMutableMap().withDefault { 0L }
    val chars = input.first.chars.toMutableMap().withDefault { 0L }

    repeat(times = steps) {
      val pairDeltas = mutableMapOf<String, Long>().withDefault { 0L }
      val charDeltas = mutableMapOf<String, Long>().withDefault { 0L }

      for ((pair, char) in input.second) {
        val count = pairs.getValue(pair)
        if (count == 0L) continue
        pairDeltas[pair] = pairDeltas.getValue(pair) - count
        pairDeltas[pair.first() + char] = pairDeltas.getValue(pair.first() + char) + count
        pairDeltas[char + pair.last()] = pairDeltas.getValue(char + pair.last()) + count
        charDeltas[char] = charDeltas.getValue(char) + count
      }

      pairDeltas.forEach { (k, v) -> pairs[k] = pairs.getValue(k) + v }
      charDeltas.forEach { (k, v) -> chars[k] = chars.getValue(k) + v }
    }

    return chars.values.maxOrNull()!! - chars.values.minOrNull()!!
  }

  override fun part1(input: Pair<Polymer, List<Pair<String, String>>>): Long {
    return solve(10, input)
  }

  override fun part2(input: Pair<Polymer, List<Pair<String, String>>>): Long {
    return solve(40, input)
  }
}