import utils.Parse
import utils.Parser
import utils.Solution
import utils.mapItems
import utils.pow

fun main() {
  Day4.run(skipTest = false)
}

object Day4 : Solution<List<Day4.Card>>() {
  override val name = "day4"
  override val parser = Parser.lines.mapItems { parseCard(it) }

  @Parse("Card {id}: {r ' ' winning} | {r ' ' numbers}")
  data class Card(
    val id: Int,
    val winning: List<Int>,
    val numbers: List<Int>,
  ) {
    val score: Pair<Int, Int> get() {
      val count = (winning.toSet() intersect numbers.toSet()).count()
      val score = if (count == 0) 0 else 2.pow(count - 1)
      return count to score
    }
  }

  override fun part1(input: List<Card>): Int {
    return input.sumOf { it.score.second }
  }

  override fun part2(input: List<Card>): Int {
    if (input.isEmpty()) {
      return 0
    }
    val card = input.first()
    val count = input.count { it.id == card.id }
    val (winCount, _) = card.score

    // add extra cards
    val next = input.filter { it.id != card.id } + (1 .. count).flatMap { input.subList(1, winCount + 1) }
    return count + part2(next)
  }
}
