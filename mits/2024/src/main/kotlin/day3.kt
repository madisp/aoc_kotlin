import utils.Parser
import utils.Solution

fun main() {
  Day3.run()
}

typealias Day3In = String

object Day3 : Solution<Day3In>() {
  override val name = "day3"
  override val parser: Parser<Day3In> = Parser { it.trim() }

  override fun part1(input: Day3In): Long {
    return input.replace(" ", "").replace('v', '0').replace('p', '1').toLong(2) + 1
  }
}
