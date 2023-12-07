import utils.Parse
import utils.Parser
import utils.Solution
import utils.mapItems
import utils.pow
import utils.withCounts

fun main() {
  Day7.run()
}

object Day7 : Solution<List<Day7.Hand>>() {
  override val name = "day7"
  override val parser = Parser.lines.mapItems { parseHand(it) }

  @Parse("{cards} {bid}")
  data class Hand(
    val cards: String,
    val bid: Long,
  ) {
    fun rank(wildcards: Boolean): Long {
      val counts = cardCounts(wildcards)
      val rank = when (counts) {
        listOf(5) -> 8L
        listOf(4, 1) -> 7L
        listOf(3, 2) -> 6L
        listOf(3, 1, 1) -> 5L
        listOf(2, 2, 1) -> 4L
        listOf(2, 1, 1, 1) -> 3L
        else -> 2
      }

      val chars = if (wildcards) "J23456789TQKA" else "23456789TJQKA"
      val strength = cards.toCharArray().mapIndexed { index, char -> chars.indexOf(char) * 100L.pow(4 - index) }.reduce { a, b -> a + b }
      return rank * 100L.pow(5) + strength
    }

    private fun cardCounts(wildcards: Boolean): List<Int> {
      val counts = cards.toCharArray().toList().withCounts()
      return if (!wildcards) {
        counts.values.sortedDescending()
      } else {
        val wildcardCount = counts['J'] ?: 0
        val noWildcards = counts
          .filter { (k, _) -> k != 'J' }.values
          .sortedDescending().takeIf { it.isNotEmpty() } ?: listOf(0)
        listOf((noWildcards.first() + wildcardCount).coerceAtMost(5)) + noWildcards.drop(1)
      }
    }
  }

  override fun part1(input: List<Hand>): Long {
    val ranked = input.sortedBy { it.rank(wildcards = false) }
    return ranked.withIndex().sumOf { (i, hand) -> (i + 1) * hand.bid }
  }

  override fun part2(input: List<Hand>): Long {
    val ranked = input.sortedBy { it.rank(wildcards = true) }
    return ranked.withIndex().sumOf { (i, hand) -> (i + 1) * hand.bid }
  }
}
