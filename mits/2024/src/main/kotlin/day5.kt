import utils.*

fun main() {
  Day5.run()
}

data class Day5In(
  val events: Map<String, Int>,
  val neighbours: List<List<String>>,
)

object Day5 : Solution<Day5In>() {
  private fun sanitizeLine(action: String): String {
    return action.replace(", kes", " kes").replace(", et", "et")
  }

  override val name = "day5"
  override val parser: Parser<Day5In> = Parser.compound(
    "Naabrid:",
    Parser.lines.map { it.drop(1) }.mapItems { line ->
      val (action, score) = sanitizeLine(line).cut(":")
      action to score.toInt()
    }.map { it.toMap() },
    Parser.lines.mapItems { line ->
      sanitizeLine(line).cut(":").second.split(",").map { it.trim() }
    },
  ).map { (events, neighbours) ->
    Day5In(events, neighbours)
  }

  override fun part1(input: Day5In): Int {
    return input.neighbours.count { n ->
      n.sumOf { input.events[it]!! } > 10
    }
  }
}
