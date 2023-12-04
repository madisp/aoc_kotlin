import utils.Parser
import utils.Solution
import utils.mapItems
import utils.pow

fun main() {
  Day4Imp.run(skipTest = false)
}

object Day4Imp : Solution<List<Day4Imp.Card>>() {
  override val name = "day4"
  override val parser = Parser.lines
    .mapItems { Day4.parseCard(it) }
    .mapItems { card ->
      val sz = card.numbers.max() + 1
      Card(BooleanArray(sz) { it in card.winning }, card.numbers.toIntArray())
    }

  class Card(
    private val winning: BooleanArray,
    private val numbers: IntArray,
  ) {
    val score: Pair<Int, Int> get() {
      val count = numbers.count { winning[it] }
      val score = if (count == 0) 0 else 2.pow(count - 1)
      return count to score
    }
  }

  override fun part1(input: List<Card>): Int {
    return input.sumOf { it.score.second }
  }

  override fun part2(input: List<Card>): Int {
    val counts = IntArray(input.size) { 1 }

    input.indices.forEach { i ->
      val card = input[i]
      val (count, _) = card.score
      (0 until count).forEach {
        counts[i + it + 1] += counts[i]
      }
    }

    return counts.sum()
  }
}
