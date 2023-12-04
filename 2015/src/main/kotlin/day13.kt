import utils.Parse
import utils.Parser
import utils.Solution
import utils.mapItems
import utils.permutations

fun main() {
  Day13.run()
}

object Day13 : Solution<List<Day13.Rule>>() {
  override val name = "day13"
  override val parser = Parser.lines.mapItems { parseRule(it) }

  @Parse("{person} would {direction} {amount} happiness units by sitting next to {partner}.")
  data class Rule(
    val person: String,
    val direction: Direction,
    val amount: Int,
    val partner: String,
  )

  enum class Direction(val mul: Int) {
    gain(1), lose(-1)
  }

  private fun score(people: List<String>, weights: Map<Pair<String, String>, Int>): Int {
    return people.windowed(2).sumOf { (a, b) -> weights[a to b]!! + weights[b to a]!! } +
      weights[people.last() to people.first()]!! +
      weights[people.first() to people.last()]!!
  }

  private fun bestSeating(rules: List<Rule>): Int {
    val people = rules.map { it.person }.toSet()
    val weights = rules.associate {
      (it.partner to it.person) to (it.direction.mul * it.amount)
    }

    val first = people.first()
    return (people - first).toList().permutations.maxOf { others ->
      score(others + first, weights)
    }
  }

  override fun part1(input: List<Rule>): Int {
    return bestSeating(input)
  }

  override fun part2(input: List<Rule>): Int {
    val modifiedRules = input + input.map { it.person }.toSet().flatMap { person ->
      listOf(
        Rule("Madis", Direction.gain, 0, person),
        Rule(person, Direction.gain, 0, "Madis"),
      )
    }

    return bestSeating(modifiedRules)
  }
}
