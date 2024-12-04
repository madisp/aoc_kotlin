import utils.Grid
import utils.Parser
import utils.Solution

fun main() {
  Day2.run()
}

typealias Day2In = Grid<Char>

object Day2 : Solution<Day2In>() {
  override val name = "day2"
  override val parser: Parser<Day2In> = Parser.charGrid

  override fun part1(input: Day2In): Int {
    val (tl, br) = input.bounds { it == 'X' }
    return (tl.manhattanDistanceTo(br) + 2) * 2 + 4
  }
}
