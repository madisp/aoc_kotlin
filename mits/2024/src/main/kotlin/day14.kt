import utils.Grid
import utils.Parser
import utils.Solution
import utils.withDefault

fun main() {
  Day14.run()
}

typealias Day14In = Grid<Char>

object Day14 : Solution<Day14In>() {
  override val name = "day14"
  override val parser: Parser<Day2In> = Parser.charGrid.map { it.withDefault('#') }

  override fun part1(input: Day2In): Int {
    return input.cells.count { (p, c) ->
      c == '_' && p.adjacent.any { input[it] == 'L' } && p.adjacent.none { input[it] in "UO" }
    }
  }
}
